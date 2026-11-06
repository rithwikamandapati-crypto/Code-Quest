package com.codequest.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int studentId = (Integer) session.getAttribute("studentId");

        try (Connection con = DBConnection.getConnection()) {

            // Student details
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT username, email, xp, level FROM users WHERE id = ?")) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        request.setAttribute("username", rs.getString("username"));
                        request.setAttribute("email", rs.getString("email"));
                        request.setAttribute("xp", rs.getInt("xp"));
                        request.setAttribute("level", rs.getInt("level"));
                    } else {
                        // Student no longer exists
                        session.invalidate();
                        response.sendRedirect(request.getContextPath() + "/login.jsp");
                        return;
                    }
                }
            }

            // Missions answered correctly
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM mission_submissions "
                  + "WHERE user_id = ? AND is_correct = TRUE")) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    request.setAttribute("missionsCompleted", rs.next() ? rs.getInt(1) : 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("username", session.getAttribute("username"));
            request.setAttribute("email", "");
            request.setAttribute("xp", 0);
            request.setAttribute("level", 1);
            request.setAttribute("missionsCompleted", 0);
        }

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}