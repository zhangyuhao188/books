package com.book.utils;

import java.sql.*;

public class DatabaseUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/library_system?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    // getConnection获取连接
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return  DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * test connect
     * @param args
     */
    public static void main(String[] args) {
        DatabaseUtils dButil = new DatabaseUtils();
        try {
            dButil.getConnection();
            System.out.println("数据库连接成功");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }
    }
}
