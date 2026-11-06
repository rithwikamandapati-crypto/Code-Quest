package com.codequest.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

// Runs before every protected page and tells the browser: "do not save this page"
@WebFilter(urlPatterns = {
    // Student pages
    "/DashboardServlet", "/dashboard.jsp",
    "/MissionsServlet", "/missions.jsp",
    "/SolveMissionServlet", "/solveMission.jsp",
    "/SubmitAnswerServlet", "/result.jsp",
    "/LeaderboardServlet", "/leaderboard.jsp",
    // Admin pages
    "/AdminDashboardServlet", "/adminDashboard.jsp",
    "/addMission.jsp",
    "/ViewMissionsServlet", "/viewMissions.jsp"
})
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);

        // Continue to the real page
        chain.doFilter(request, response);
    }
}