package com.codequest.student;

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

@WebServlet("/MissionsServlet")
public class MissionsServlet extends HttpServlet {

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

        // Each mission:
        // 0=id 1=title 2=description 3=category 4=difficulty
        // 5=A 6=B 7=C 8=D 9=xp 10=status (new / correct / wrong)
        // NOTE: correct_answer is NOT loaded, so students can never see it.
        List<String[]> missions = new ArrayList<>();

        String sql = "SELECT m.id, m.title, m.description, m.category, m.difficulty, "
                   + "m.option_a, m.option_b, m.option_c, m.option_d, m.xp_reward, "
                   + "s.id AS sub_id, s.is_correct "
                   + "FROM missions m "
                   + "LEFT JOIN mission_submissions s "
                   + "ON s.mission_id = m.id AND s.user_id = ? "
                   + "ORDER BY m.category ASC, "
                   + "FIELD(m.difficulty, 'Easy', 'Medium', 'Hard'), m.id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    String status;
                    if (rs.getObject("sub_id") == null) {
                        status = "new";
                    } else if (rs.getBoolean("is_correct")) {
                        status = "correct";
                    } else {
                        status = "wrong";
                    }

                    missions.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("difficulty"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        String.valueOf(rs.getInt("xp_reward")),
                        status
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load missions.");
        }

        request.setAttribute("missions", missions);
        request.getRequestDispatcher("missions.jsp").forward(request, response);
    }
}