package com.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class cConfig {

    // SESUAIKAN PASSWORD XAMPP DI SINI
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dbparkir";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Driver modern (opsional)
            // Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            System.err.println("❌ Gagal Konek Database: " + e.getMessage());
        }
        return conn;
    }
}