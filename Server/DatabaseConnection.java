package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/atm?"+"useSSL=true&serverTimezone=GMT&characterEncoding=utf-8";
    private static final String USER = "root";
    private static final String PASSWORD = "Ljj862592";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 确保加载了 MySQL 驱动类
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // 驱动类加载失败
            throw new RuntimeException("找不到JDBC驱动类", e);
        }
    }
}
