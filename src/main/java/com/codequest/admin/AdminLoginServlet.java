package com.codequest.admin;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Same password check as the student login
    private boolean verifyPassword(String password, String storedPassword)
            throws Exception {

        String[] parts = storedPassword.split(":");

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] originalHash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), salt,
                iterations, originalHash.length * 8);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] newHash = factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(originalHash, newHash);

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
        String password = request.getParameter("password");

        if (username == null || password == null
                || username.trim().isEmpty() || password.isEmpty()) {

            request.setAttribute("error", "Please enter username and password.");
            request.getRequestDispatcher("adminLogin.jsp").forward(request, response);
            return;
        }

        String sql = "SELECT id, username, password FROM admins WHERE username = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new Exception("Database connection failed.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, username.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        String stored = rs.getString("password");

                        if (verifyPassword(password, stored)) {

                            HttpSession session = request.getSession();
                            session.setAttribute("adminId", rs.getInt("id"));
                            session.setAttribute("adminUsername", rs.getString("username"));

                            // Admin dashboard is the next task
                            response.sendRedirect(request.getContextPath() + "/AdminDashboardServlet");
                            return;
                        }
                    }
                }
            }

            request.setAttribute("error", "Invalid admin username or password.");
            request.getRequestDispatcher("adminLogin.jsp").forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error", "Login failed. Please try again.");
            request.getRequestDispatcher("adminLogin.jsp").forward(request, response);
        }
    }
}