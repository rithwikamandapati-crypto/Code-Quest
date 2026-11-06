package com.codequest.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AddMissionServlet")
public class AddMissionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Only a logged-in admin can add missions
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String difficulty = request.getParameter("difficulty");
        String optionA = request.getParameter("optionA");
        String optionB = request.getParameter("optionB");
        String optionC = request.getParameter("optionC");
        String optionD = request.getParameter("optionD");
        String correctAnswer = request.getParameter("correctAnswer");
        String xpText = request.getParameter("xpReward");

        // Check empty fields
        if (isEmpty(title) || isEmpty(description) || isEmpty(category)
                || isEmpty(difficulty) || isEmpty(optionA) || isEmpty(optionB)
                || isEmpty(optionC) || isEmpty(optionD)
                || isEmpty(correctAnswer) || isEmpty(xpText)) {

            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("addMission.jsp").forward(request, response);
            return;
        }

        // Check correct answer is A, B, C or D
        if (!correctAnswer.matches("[ABCD]")) {
            request.setAttribute("error", "Correct answer must be A, B, C or D.");
            request.getRequestDispatcher("addMission.jsp").forward(request, response);
            return;
        }

        // Check XP is a number between 1 and 1000
        int xp;
        try {
            xp = Integer.parseInt(xpText.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("error", "XP reward must be a number.");
            request.getRequestDispatcher("addMission.jsp").forward(request, response);
            return;
        }

        if (xp < 1 || xp > 1000) {
            request.setAttribute("error", "XP reward must be between 1 and 1000.");
            request.getRequestDispatcher("addMission.jsp").forward(request, response);
            return;
        }

        String sql = "INSERT INTO missions "
                + "(title, description, category, difficulty, "
                + "option_a, option_b, option_c, option_d, "
                + "correct_answer, xp_reward) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title.trim());
            ps.setString(2, description.trim());
            ps.setString(3, category.trim());
            ps.setString(4, difficulty.trim());
            ps.setString(5, optionA.trim());
            ps.setString(6, optionB.trim());
            ps.setString(7, optionC.trim());
            ps.setString(8, optionD.trim());
            ps.setString(9, correctAnswer.trim());
            ps.setInt(10, xp);

            ps.executeUpdate();

            // Redirect so refreshing the page does not save the mission twice
            response.sendRedirect(request.getContextPath() + "/addMission.jsp?success=1");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error. Mission was not saved.");
            request.getRequestDispatcher("addMission.jsp").forward(request, response);
        }
    }
}