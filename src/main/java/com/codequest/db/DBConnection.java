
package com.codequest.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/codequest_db";

    private static final String USER = "root";

    // Read the MySQL password from Windows environment variables
    private static final String PASSWORD =
            System.getenv("CODEQUEST_DB_PASSWORD");

    public static Connection getConnection() {

        try {
            // Check whether the password is configured
            if (PASSWORD == null || PASSWORD.isEmpty()) {
                throw new SQLException(
                    "MySQL password not configured. " +
                    "Set CODEQUEST_DB_PASSWORD in Windows."
                );
            }

            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            Connection con = DriverManager.getConnection(
                    URL, USER, PASSWORD
            );

            System.out.println("Database Connected Successfully!");

            return con;

        } catch (ClassNotFoundException e) {

            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();

        } catch (SQLException e) {

            System.err.println("Database connection failed!");
            e.printStackTrace();
        }

        return null;
    }
}
