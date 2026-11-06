package com.codequest.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private boolean verifyPassword(String password, String storedPassword)
            throws Exception {

        String[] parts = storedPassword.split(":");

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] originalHash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt,
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
        String remember = request.getParameter("remember");

        if (username == null || password == null
                || username.trim().isEmpty() || password.isEmpty()) {

            request.setAttribute("error", "Please enter username and password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String sql = "SELECT id, username, password FROM users "
                   + "WHERE username = ? OR email = ?";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new Exception("Database connection failed.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, username.trim());
                ps.setString(2, username.trim());

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        String storedPassword = rs.getString("password");

                        if (verifyPassword(password, storedPassword)) {

                            String dbUsername = rs.getString("username");

                            HttpSession session = request.getSession();
                            session.setAttribute("studentId", rs.getInt("id"));
                            session.setAttribute("username", dbUsername);

                            // ---- COOKIE: remember the username (never the password) ----
                            Cookie cookie = new Cookie("rememberedUser",
                                    URLEncoder.encode(dbUsername, StandardCharsets.UTF_8));
                            String path = request.getContextPath();
                            cookie.setPath(path.isEmpty() ? "/" : path);
                            cookie.setHttpOnly(true);
                            // 7 days if the box was ticked, otherwise 0 = delete the cookie
                            cookie.setMaxAge(remember != null ? 7 * 24 * 60 * 60 : 0);
                            response.addCookie(cookie);

                            // ---- SERVLETCONTEXT: count logins for the whole application ----
                            AtomicInteger counter = (AtomicInteger)
                                    getServletContext().getAttribute("loginCount");
                            if (counter != null) {
                                counter.incrementAndGet();
                            }

                            response.sendRedirect(request.getContextPath()
                                    + "/DashboardServlet");
                            return;
                        }
                    }
                }
            }

            request.setAttribute("error", "Invalid username or password.");
            request.getRequestDispatcher("login.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Login failed. Please try again.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}