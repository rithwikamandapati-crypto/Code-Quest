package com.codequest.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ViewMissionsServlet")
public class ViewMissionsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
            return;
        }

        // Each mission is stored as a String array:
        // 0=id, 1=title, 2=category, 3=difficulty, 4=correct answer, 5=xp
        List<String[]> missions = new ArrayList<>();

        String sql = "SELECT id, title, category, difficulty, correct_answer, xp_reward "
                   + "FROM missions ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                missions.add(new String[] {
                    String.valueOf(rs.getInt("id")),
                    rs.getString("title"),
                    rs.getString("category"),
                    rs.getString("difficulty"),
                    rs.getString("correct_answer"),
                    String.valueOf(rs.getInt("xp_reward"))
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load missions.");
        }

        request.setAttribute("missions", missions);
        request.getRequestDispatcher("viewMissions.jsp").forward(request, response);
    }
}