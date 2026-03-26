package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_management?createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static Connection getConnection () {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Lỗi kết nối: " +e.getMessage());
            return null;
        }
    }
}
