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

@WebServlet("/SolveMissionServlet")
public class SolveMissionServlet extends HttpServlet {

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
        String ctx = request.getContextPath();

        int missionId;
        try {
            missionId = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(ctx + "/MissionsServlet");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Already solved? Then do not open it again
            try (PreparedStatement chk = con.prepareStatement(
                    "SELECT id FROM mission_submissions WHERE user_id = ? AND mission_id = ?")) {
                chk.setInt(1, studentId);
                chk.setInt(2, missionId);
                try (ResultSet rs = chk.executeQuery()) {
                    if (rs.next()) {
                        response.sendRedirect(ctx + "/MissionsServlet?msg=done");
                        return;
                    }
                }
            }

            // Load the mission. We do NOT load correct_answer, so students cannot see it.
            String sql = "SELECT id, title, description, category, difficulty, "
                       + "option_a, option_b, option_c, option_d, xp_reward "
                       + "FROM missions WHERE id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, missionId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // 0=id 1=title 2=description 3=category 4=difficulty
                        // 5=A 6=B 7=C 8=D 9=xp
                        String[] mission = new String[] {
                            String.valueOf(rs.getInt("id")),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getString("difficulty"),
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d"),
                            String.valueOf(rs.getInt("xp_reward"))
                        };
                        request.setAttribute("mission", mission);
                        request.getRequestDispatcher("solveMission.jsp").forward(request, response);
                        return;
                    }
                }
            }

            // Mission id not found
            response.sendRedirect(ctx + "/MissionsServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(ctx + "/MissionsServlet");
        }
    }
}