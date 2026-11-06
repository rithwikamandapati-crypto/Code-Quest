
package com.codequest.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;

    // Password hashing method
    private String hashPassword(String password) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt,
            ITERATIONS, KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword =
            request.getParameter("confirmPassword");

        // Check empty fields
        if (username == null || email == null ||
            password == null || confirmPassword == null ||
            username.trim().isEmpty() ||
            email.trim().isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()) {

            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("register.jsp")
                   .forward(request, response);
            return;
        }

        username = username.trim();
        email = email.trim();

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error",
                "Passwords do not match.");

            request.getRequestDispatcher("register.jsp")
                   .forward(request, response);
            return;
        }

        // Basic username and email validation
        if (username.length() > 50 ||
            !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            request.setAttribute("error",
                "Enter a valid username and email.");

            request.getRequestDispatcher("register.jsp")
                   .forward(request, response);
            return;
        }

        if (password.length() < 8) {
            request.setAttribute("error",
                "Password must contain at least 8 characters.");

            request.getRequestDispatcher("register.jsp")
                   .forward(request, response);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            // Check duplicate username or email
            String checkSql =
                "SELECT id FROM users WHERE username = ? OR email = ?";

            try (PreparedStatement check =
                    con.prepareStatement(checkSql)) {

                check.setString(1, username);
                check.setString(2, email);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next()) {
                        request.setAttribute("error",
                            "Username or email already exists.");

                        request.getRequestDispatcher("register.jsp")
                               .forward(request, response);
                        return;
                    }
                }
            }

            // Insert new student
            String insertSql =
                "INSERT INTO users " +
                "(username, email, password, xp, level) " +
                "VALUES (?, ?, ?, 0, 1)";

            try (PreparedStatement ps =
                    con.prepareStatement(insertSql)) {

                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, hashPassword(password));

                int result = ps.executeUpdate();

                if (result > 0) {
                    request.setAttribute("success",
                        "Registration successful! Please login.");

                } else {
                    request.setAttribute("error",
                        "Registration failed. Please try again.");
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();

            request.setAttribute("error",
                "Database error. Please try again.");

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error",
                "Unable to process registration.");
        }

        request.getRequestDispatcher("register.jsp")
               .forward(request, response);
    }
}
