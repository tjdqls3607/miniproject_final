package jdbc;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        List <MemberDto> list = listmember();
        for (MemberDto dto : list) {
            System.out.println(dto);
        }

        MemberDto dto = detailMember(2);
        System.out.println(dto);

    }
    public static int insertMember (int member_id, String name, String phone_number, Date birth_date) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String insertSql = "insert into member values (?, ?, ?, ?); ";

        int ret = -1;
        try {
            con = DBManager.getConnection();
            pstmt = con.prepareStatement(insertSql);
            pstmt.setInt(1,member_id);
            pstmt.setString(2,name);
            pstmt.setString(3,phone_number);
            pstmt.setDate(4, (java.sql.Date) birth_date);

            ret = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }
        return ret;
    }

    public static int updateMember (int member_id, String name, String phone_number) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String updateSql = "update member set name = ?, phone_number = ? where member_id = ?";

        int ret = -1;
        try {
            con = DBManager.getConnection();
            pstmt = con.prepareStatement(updateSql);
            pstmt.setString(1, name);
            pstmt.setString(2, phone_number);
            pstmt.setInt(3, member_id);

            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }
        return ret;
    }

    public static int deleteMember (int member_id) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String deleteSql = "delete from member where member_id = ?";

        int ret = -1;
        try {
            con = DBManager.getConnection();
            pstmt = con.prepareStatement(deleteSql);
            pstmt.setInt(1,member_id);

            ret = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }

        return ret;
    }
    public static List<MemberDto> listmember() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<MemberDto> list = new ArrayList<>();

        String selectSql = "select * from member";

        try {
            con = DBManager.getConnection();
            pstmt = con.prepareStatement(selectSql);

            rs = pstmt.executeQuery();
            while(rs.next()) {
                MemberDto dto = new MemberDto();
                dto.setId(rs.getInt("member_id"));
                dto.setName(rs.getString("name"));
                dto.setPhoneNumber(rs.getString("phone_number"));
                dto.setBirthDate(rs.getDate("birth_date"));

                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }
        return list;
    }

    public static MemberDto detailMember(int member_id) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        MemberDto dto = null;

        String selectSql = "select * from member where member_id = ?";

        try {
            con = DBManager.getConnection();
            pstmt = con.prepareStatement(selectSql);
            pstmt.setInt(1, member_id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dto = new MemberDto();
                dto.setId(rs.getInt("member_id"));
                dto.setName(rs.getString("name"));
                dto.setPhoneNumber(rs.getString("phone_number"));
                dto.setBirthDate(rs.getDate("birth_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }
        return dto;
    }

    public static int sellPhone(int memberId, int modelId, int quantity) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int ret = -1;
        try {
            con = DBManager.getConnection();

            // 멤버십 등급과 할인율 조회
            String membershipSql = "SELECT discount_rate FROM membership WHERE member_id = ?";
            pstmt = con.prepareStatement(membershipSql);
            pstmt.setInt(1, memberId);
            rs = pstmt.executeQuery();
            double discountRate = 0.0;
            if (rs.next()) {
                discountRate = rs.getDouble("discount_rate");
            }

            // 휴대폰 가격 조회
            String priceSql = "SELECT price FROM model WHERE model_id = ?";
            pstmt = con.prepareStatement(priceSql);
            pstmt.setInt(1, modelId);
            rs = pstmt.executeQuery();
            double price = 0.0;
            if (rs.next()) {
                price = rs.getDouble("price");
            }

            // 할인 적용된 최종 가격 계산
            double finalPrice = price * quantity * (1 - discountRate / 100);

            // 판매 기록 추가
            String salesSql = "INSERT INTO sales (member_id, model_id, final_price) VALUES (?, ?, ?)";
            pstmt = con.prepareStatement(salesSql);
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, modelId);
            pstmt.setDouble(3, finalPrice);
            ret = pstmt.executeUpdate();

            // 재고 수량 업데이트
            String stockSql = "UPDATE stock SET quantity = quantity - ? WHERE model_id = ?";
            pstmt = con.prepareStatement(stockSql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, modelId);
            pstmt.executeUpdate();

            // 출고 기록 추가
            String logSql = "INSERT INTO inventory_log (model_id, change_quantity, change_type) VALUES (?, ?, 'OUT')";
            pstmt = con.prepareStatement(logSql);
            pstmt.setInt(1, modelId);
            pstmt.setInt(2, quantity);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(rs, pstmt, con);
        }
        return ret;
    }

    public static int addStock(int modelId, int quantity) {
        Connection con = null;
        PreparedStatement pstmt = null;

        int ret = -1;
        try {
            con = DBManager.getConnection();

            // 재고 수량 업데이트
            String stockSql = "UPDATE stock SET quantity = quantity + ? WHERE model_id = ?";
            pstmt = con.prepareStatement(stockSql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, modelId);
            ret = pstmt.executeUpdate();

            // 입고 기록 추가
            String logSql = "INSERT INTO inventory_log (model_id, change_quantity, change_type) VALUES (?, ?, 'IN')";
            pstmt = con.prepareStatement(logSql);
            pstmt.setInt(1, modelId);
            pstmt.setInt(2, quantity);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(pstmt, con);
        }
        return ret;
    }
}
