<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%!
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    if (session.getAttribute("studentId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    @SuppressWarnings("unchecked")
    List<String[]> missions = (List<String[]>) request.getAttribute("missions");
    String error = (String) request.getAttribute("error");
    String msg = request.getParameter("msg");

    String ctx = request.getContextPath();
    String currentCategory = "";
    String currentDifficulty = "";
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Missions</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>

<div class="navbar">
    <h2>CodeQuest</h2>
    <div class="nav-links">
        <a href="<%= ctx %>/DashboardServlet">Dashboard</a>
        <a href="<%= ctx %>/LeaderboardServlet">Leaderboard</a>
        <a class="logout btn-small" href="<%= ctx %>/LogoutServlet">Logout</a>
    </div>
</div>

<div class="container">
    <h1>Programming Missions</h1>

    <% if ("done".equals(msg)) { %>
        <div class="info">You have already answered that mission.</div>
    <% } %>
    <% if (error != null) { %>
        <div class="bad"><%= esc(error) %></div>
    <% } %>

    <% if (missions == null || missions.isEmpty()) { %>
        <div class="empty">No missions available yet. Please check back later.</div>
    <% } else {
        for (String[] m : missions) {

            // New category heading
            if (!m[3].equals(currentCategory)) {
                currentCategory = m[3];
                currentDifficulty = "";
    %>
        <h2 class="cat-title"><%= esc(currentCategory) %></h2>
    <%
            }
            // New difficulty heading
            if (!m[4].equals(currentDifficulty)) {
                currentDifficulty = m[4];
    %>
        <h3 class="diff-title"><%= esc(currentDifficulty) %></h3>
    <%
            }
    %>
        <div class="box mission-box">
            <div class="tags">
                <span><%= esc(m[3]) %></span>
                <span><%= esc(m[4]) %></span>
            </div>
            <h3><%= esc(m[1]) %></h3>
            <div class="xp">Reward: +<%= esc(m[9]) %> XP</div>
            <div class="question"><%= esc(m[2]) %></div>

            <% if ("new".equals(m[10])) { %>
            <form action="<%= ctx %>/SubmitAnswerServlet" method="post">
                <input type="hidden" name="missionId" value="<%= esc(m[0]) %>">

                <label class="option"><input type="radio" name="answer" value="A" required> A. <%= esc(m[5]) %></label>
                <label class="option"><input type="radio" name="answer" value="B"> B. <%= esc(m[6]) %></label>
                <label class="option"><input type="radio" name="answer" value="C"> C. <%= esc(m[7]) %></label>
                <label class="option"><input type="radio" name="answer" value="D"> D. <%= esc(m[8]) %></label>

                <button type="submit">Submit Answer</button>
            </form>
            <% } else if ("correct".equals(m[10])) { %>
                <span class="done correct">&#10004; Completed</span>
            <% } else { %>
                <span class="done wrong">&#10006; Answered (incorrect)</span>
            <% } %>
        </div>
    <%
        }
    } %>
</div>

</body>
</html>