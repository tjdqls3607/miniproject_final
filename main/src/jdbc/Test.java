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
        // 회원 목록 출력
        List<MemberDto> list = listmember();
        for (MemberDto dto : list) {
            System.out.println(dto);
        }

        // 특정 회원 상세 정보 출력
        MemberDto dto = detailMember(2);
        System.out.println(dto);

        // 휴대폰 재고 조회
        int stock = getStock(1); // model_id가 1인 휴대폰 재고 조회
        System.out.println("재고: " + stock);

        // 휴대폰 판매 테스트
        int result = sellPhone(1, 1, 2); // member_id 1, model_id 1, quantity 2
        if (result == -1) {
            System.out.println("구매 불가: 재고가 부족합니다.");
        } else {
            System.out.println("구매 성공!");
        }
    }

    // 회원 추가
    public static int insertMember(int member_id, String name, String phone_number, Date birth_date) {
        String insertSql = "INSERT INTO member VALUES (?, ?, ?, ?)";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(insertSql)) {
            pstmt.setInt(1, member_id);
            pstmt.setString(2, name);
            pstmt.setString(3, phone_number);
            pstmt.setDate(4, new java.sql.Date(birth_date.getTime()));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 회원 정보 수정
    public static int updateMember(int member_id, String name, String phone_number) {
        String updateSql = "UPDATE member SET name = ?, phone_number = ? WHERE member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(updateSql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone_number);
            pstmt.setInt(3, member_id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 회원 삭제
    public static int deleteMember(int member_id) {
        String deleteSql = "DELETE FROM member WHERE member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(deleteSql)) {
            pstmt.setInt(1, member_id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 모든 회원 조회
    public static List<MemberDto> listmember() {
        List<MemberDto> list = new ArrayList<>();
        String selectSql = "SELECT * FROM member";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(selectSql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MemberDto dto = new MemberDto();
                dto.setId(rs.getInt("member_id"));
                dto.setName(rs.getString("name"));
                dto.setPhoneNumber(rs.getString("phone_number"));
                dto.setBirthDate(rs.getDate("birth_date"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 특정 회원 상세 정보 조회
    public static MemberDto detailMember(int member_id) {
        String selectSql = "SELECT * FROM member WHERE member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(selectSql)) {
            pstmt.setInt(1, member_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MemberDto dto = new MemberDto();
                    dto.setId(rs.getInt("member_id"));
                    dto.setName(rs.getString("name"));
                    dto.setPhoneNumber(rs.getString("phone_number"));
                    dto.setBirthDate(rs.getDate("birth_date"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 휴대폰 재고 조회
    public static int getStock(int modelId) {
        String selectSql = "SELECT quantity FROM stock WHERE model_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(selectSql)) {
            pstmt.setInt(1, modelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 재고 조회 실패
    }

    // 휴대폰 판매
    public static int sellPhone(int memberId, int modelId, int quantity) {
        // 재고 확인
        int stock = getStock(modelId);
        if (stock < quantity) {
            return -1; // 재고 부족
        }

        // 멤버십 할인율 조회
        double discountRate = getMembershipDiscountRate(memberId);
        if (discountRate == -1) {
            return -1; // 멤버십 정보 조회 실패
        }

        // 휴대폰 가격 조회
        double price = getPhonePrice(modelId);
        if (price == -1) {
            return -1; // 휴대폰 정보 조회 실패
        }

        // 할인 적용된 최종 가격 계산
        double finalPrice = price * quantity * (1 - discountRate / 100);

        // 판매 기록 추가
        String salesSql = "INSERT INTO sales (member_id, model_id, final_price) VALUES (?, ?, ?)";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(salesSql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, modelId);
            pstmt.setDouble(3, finalPrice);
            int ret = pstmt.executeUpdate();

            // 재고 수량 업데이트
            String stockSql = "UPDATE stock SET quantity = quantity - ? WHERE model_id = ?";
            try (PreparedStatement pstmt2 = con.prepareStatement(stockSql)) {
                pstmt2.setInt(1, quantity);
                pstmt2.setInt(2, modelId);
                pstmt2.executeUpdate();
            }

            // 출고 기록 추가
            String logSql = "INSERT INTO inventory_log (model_id, change_quantity, change_type) VALUES (?, ?, 'OUT')";
            try (PreparedStatement pstmt3 = con.prepareStatement(logSql)) {
                pstmt3.setInt(1, modelId);
                pstmt3.setInt(2, quantity);
                pstmt3.executeUpdate();
            }

            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 멤버십 할인율 조회
    public static double getMembershipDiscountRate(int memberId) {
        String selectSql = "SELECT discount_rate FROM membership WHERE member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(selectSql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("discount_rate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 조회 실패
    }

    // 휴대폰 가격 조회
    public static double getPhonePrice(int modelId) {
        String selectSql = "SELECT price FROM model WHERE model_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(selectSql)) {
            pstmt.setInt(1, modelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 조회 실패
    }

    // 재고 추가
    public static int addStock(int modelId, int quantity) {
        String stockSql = "UPDATE stock SET quantity = quantity + ? WHERE model_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(stockSql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, modelId);
            int ret = pstmt.executeUpdate();

            // 입고 기록 추가
            String logSql = "INSERT INTO inventory_log (model_id, change_quantity, change_type) VALUES (?, ?, 'IN')";
            try (PreparedStatement pstmt2 = con.prepareStatement(logSql)) {
                pstmt2.setInt(1, modelId);
                pstmt2.setInt(2, quantity);
                pstmt2.executeUpdate();
            }

            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static String getMembershipLevel(int memberId) {
        String selectSql = "SELECT level FROM membership WHERE member_id = ?";
        try (Connection con = DBManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(selectSql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 조회 실패
    }
}