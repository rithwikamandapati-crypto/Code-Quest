package com.codequest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the current session, but do NOT create a new one if it doesn't exist
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();   // removes studentId, username, everything
        }

        // Send the student back to the login page
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}