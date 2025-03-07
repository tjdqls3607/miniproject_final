package jdbc;

import java.sql.*;
// Connection 객체를 생성, 전달
// ResultSet, PreparedStatement, Connection 객체 종료 close()
public class DBManager {
    static String url = "jdbc:mysql://localhost:3306/mini_project";
    static String user = "root";
    static String pwd = "root";

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, pwd);
            System.out.println("데이터베이스 연결 성공!");
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패!");
            e.printStackTrace();
        }
        return con;
    }



    public static void main(String[] args) {
        // 연결 테스트
        Connection conn = DBManager.getConnection();
        if (conn != null) {
            try {
                conn.close();
                System.out.println("연결 종료");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // releaseConnection Variable Args... (개인적으로는 별로 for 때문)
    public static void releaseConnection(PreparedStatement pstmt, Connection con) {
        try {
            pstmt.close();
            con.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void releaseConnection(ResultSet rs, PreparedStatement pstmt, Connection con) {
        try {
            if ( rs != null) rs.close();
            if ( pstmt != null) pstmt.close();
            if ( con != null) con.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
}