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

@WebServlet("/DeleteMissionServlet")
public class DeleteMissionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Opening the URL directly just goes back to the missions list
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/ViewMissionsServlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
            return;
        }

        String message = "deleted";

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            try (Connection con = DBConnection.getConnection()) {

                // Remove student submissions for this mission first
                // (the foreign key would block the delete otherwise)
                try (PreparedStatement ps1 = con.prepareStatement(
                        "DELETE FROM mission_submissions WHERE mission_id = ?")) {
                    ps1.setInt(1, id);
                    ps1.executeUpdate();
                }

                try (PreparedStatement ps2 = con.prepareStatement(
                        "DELETE FROM missions WHERE id = ?")) {
                    ps2.setInt(1, id);
                    ps2.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "failed";
        }

        response.sendRedirect(request.getContextPath()
                + "/ViewMissionsServlet?msg=" + message);
    }
}