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

@WebServlet("/LeaderboardServlet")
public class LeaderboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int TOP_COUNT = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int studentId = (Integer) session.getAttribute("studentId");

        // Each row: 0=rank 1=username 2=level 3=xp 4=missions completed 5=isMe (true/false)
        List<String[]> top = new ArrayList<>();
        String[] myRow = null;

        String sql = "SELECT u.id, u.username, u.xp, u.level, "
                   + "(SELECT COUNT(*) FROM mission_submissions s "
                   + " WHERE s.user_id = u.id AND s.is_correct = TRUE) AS completed "
                   + "FROM users u "
                   + "ORDER BY u.xp DESC, u.username ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int position = 0;      // how many students we have passed
            int rank = 0;          // the rank to show
            int previousXp = -1;

            while (rs.next()) {
                position++;
                int xp = rs.getInt("xp");

                // Same XP = same rank
                if (xp != previousXp) {
                    rank = position;
                    previousXp = xp;
                }

                boolean isMe = rs.getInt("id") == studentId;

                String[] row = new String[] {
                    String.valueOf(rank),
                    rs.getString("username"),
                    String.valueOf(rs.getInt("level")),
                    String.valueOf(xp),
                    String.valueOf(rs.getInt("completed")),
                    String.valueOf(isMe)
                };

                if (position <= TOP_COUNT) {
                    top.add(row);
                }
                if (isMe) {
                    myRow = row;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load the leaderboard.");
        }

        request.setAttribute("top", top);
        request.setAttribute("myRow", myRow);
        request.getRequestDispatcher("leaderboard.jsp").forward(request, response);
    }
}