
package com.codequest.db;

import java.sql.Connection;

public class DBTest {

    public static void main(String[] args) {

        Connection con = DBConnection.getConnection();

        if (con != null) {
            System.out.println("Connection Test Passed!");
        } else {
            System.out.println("Connection Test Failed!");
        }
    }
}
