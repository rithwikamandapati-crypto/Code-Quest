package com.codequest.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;

import com.codequest.db.DBConnection;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// initParams = ServletConfig values that belong to THIS servlet only
@WebServlet(
    urlPatterns = "/AdminDashboardServlet",
    initParams = {
        @WebInitParam(name = "panelTitle", value = "Admin Control Panel")
    }
)
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private int count(Statement st, String sql) throws Exception {
        try (ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
            return;
        }

        // ---- SERVLETCONFIG: init parameter of this servlet ----
        ServletConfig config = getServletConfig();
        request.setAttribute("panelTitle", config.getInitParameter("panelTitle"));

        // ---- SERVLETCONTEXT: values shared by the whole application ----
        ServletContext context = getServletContext();
        request.setAttribute("appName", context.getAttribute("appName"));

        AtomicInteger counter = (AtomicInteger) context.getAttribute("loginCount");
        request.setAttribute("loginCount", counter == null ? 0 : counter.get());

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            request.setAttribute("totalStudents",
                    count(st, "SELECT COUNT(*) FROM users"));
            request.setAttribute("totalMissions",
                    count(st, "SELECT COUNT(*) FROM missions"));
            request.setAttribute("totalSubmissions",
                    count(st, "SELECT COUNT(*) FROM mission_submissions"));

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("totalStudents", 0);
            request.setAttribute("totalMissions", 0);
            request.setAttribute("totalSubmissions", 0);
        }

        request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
    }
}