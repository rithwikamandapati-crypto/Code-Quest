package com.codequest.student;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/SubmitAnswerServlet")
public class SubmitAnswerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Every 50 XP = 1 level
    private static final int XP_PER_LEVEL = 50;

    private int calculateLevel(int xp) {
        return (xp / XP_PER_LEVEL) + 1;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/MissionsServlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String ctx = request.getContextPath();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect(ctx + "/login.jsp");
            return;
        }

        int studentId = (Integer) session.getAttribute("studentId");

        int missionId;
        try {
            missionId = Integer.parseInt(request.getParameter("missionId"));
        } catch (Exception e) {
            response.sendRedirect(ctx + "/MissionsServlet");
            return;
        }

        String answer = request.getParameter("answer");
        if (answer == null || !answer.matches("[ABCD]")) {
            response.sendRedirect(ctx + "/SolveMissionServlet?id=" + missionId);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // 1. Block duplicate submissions
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

            // 2. Load the mission
            String sql = "SELECT title, option_a, option_b, option_c, option_d, "
                       + "correct_answer, xp_reward FROM missions WHERE id = ?";

            String title, correct;
            String[] options = new String[4];
            int xp;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, missionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        response.sendRedirect(ctx + "/MissionsServlet");
                        return;
                    }
                    title = rs.getString("title");
                    options[0] = rs.getString("option_a");
                    options[1] = rs.getString("option_b");
                    options[2] = rs.getString("option_c");
                    options[3] = rs.getString("option_d");
                    correct = rs.getString("correct_answer").trim();
                    xp = rs.getInt("xp_reward");
                }
            }

            // 3. Compare the answers
            boolean isCorrect = answer.equals(correct);

            int newXp = 0;
            int newLevel = 1;
            boolean leveledUp = false;

            // 4. Save submission + give XP as ONE transaction
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO mission_submissions "
                      + "(user_id, mission_id, submitted_answer, is_correct) "
                      + "VALUES (?, ?, ?, ?)")) {
                    ins.setInt(1, studentId);
                    ins.setInt(2, missionId);
                    ins.setString(3, answer);
                    ins.setBoolean(4, isCorrect);
                    ins.executeUpdate();
                }

                // Read the student's current XP and level (row is locked until commit)
                int oldXp = 0;
                int oldLevel = 1;
                try (PreparedStatement sel = con.prepareStatement(
                        "SELECT xp, level FROM users WHERE id = ? FOR UPDATE")) {
                    sel.setInt(1, studentId);
                    try (ResultSet rs = sel.executeQuery()) {
                        if (rs.next()) {
                            oldXp = rs.getInt("xp");
                            oldLevel = rs.getInt("level");
                        }
                    }
                }

                newXp = oldXp;
                newLevel = oldLevel;

                if (isCorrect) {
                    newXp = oldXp + xp;
                    newLevel = calculateLevel(newXp);
                    leveledUp = newLevel > oldLevel;

                    try (PreparedStatement upd = con.prepareStatement(
                            "UPDATE users SET xp = ?, level = ? WHERE id = ?")) {
                        upd.setInt(1, newXp);
                        upd.setInt(2, newLevel);
                        upd.setInt(3, studentId);
                        upd.executeUpdate();
                    }
                }

                con.commit();

            } catch (SQLIntegrityConstraintViolationException dup) {
                con.rollback();
                response.sendRedirect(ctx + "/MissionsServlet?msg=done");
                return;

            } catch (Exception e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }

            // 5. Send the result to the result page
            // 0=title 1=your letter 2=your text 3=correct letter 4=correct text
            // 5=isCorrect 6=xp earned 7=total xp 8=level 9=leveledUp
            String[] result = new String[] {
                title,
                answer,
                options["ABCD".indexOf(answer)],
                correct,
                options["ABCD".indexOf(correct)],
                String.valueOf(isCorrect),
                String.valueOf(xp),
                String.valueOf(newXp),
                String.valueOf(newLevel),
                String.valueOf(leveledUp)
            };

            request.setAttribute("result", result);
            request.getRequestDispatcher("result.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(ctx + "/MissionsServlet");
        }
    }
}