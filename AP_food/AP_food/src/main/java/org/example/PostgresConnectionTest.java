package org.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "P100p200";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("اتصال به PostgreSQL با موفقیت انجام شد!");
        } catch (SQLException e) {
            System.err.println("خطا در اتصال به PostgreSQL:");
            e.printStackTrace();
        }
    }
}