package com.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        try {
            url = PropertiesUtil.get("db.url");
            username = PropertiesUtil.get("db.username");
            password = PropertiesUtil.get("db.password");
            driver = PropertiesUtil.get("db.driver");

            if (driver == null || driver.isEmpty()) {
                driver = "org.postgresql.Driver";
            }
            if (url == null || url.isEmpty()) {
                url = "jdbc:postgresql://localhost:5432/travel_agency";
            }
            if (username == null || username.isEmpty()) {
                username = "postgres";
            }
            if (password == null || password.isEmpty()) {
                password = "18/20";
            }

            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trouvé: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
