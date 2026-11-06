<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
    // Makes text safe to show in HTML
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    // Tries several attribute names and returns the first one that has a value
    private String pick(jakarta.servlet.http.HttpServletRequest req,
                        jakarta.servlet.http.HttpSession ses,
                        String fallback, String... names) {
        for (String n : names) {
            Object v = req.getAttribute(n);
            if (v == null && ses != null) v = ses.getAttribute(n);
            if (v != null) return String.valueOf(v);
        }
        return fallback;
    }
%>
<%
    // Only a logged-in student can open this page
    if (session.getAttribute("studentId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String username = pick(request, session, "Student", "username", "studentName", "name");
    String email    = pick(request, session, "", "email", "studentEmail");
    String xp       = pick(request, session, "0", "xp", "totalXp", "totalXP");
    String level    = pick(request, session, "1", "level", "currentLevel");
    String missions = pick(request, session, "0", "missionsCompleted",
                           "completedMissions", "missionCount", "completed");

    // Progress toward the next level (every 50 XP = 1 level)
    int xpValue = 0;
    try { xpValue = Integer.parseInt(xp); } catch (Exception e) { xpValue = 0; }
    int levelValue = 1;
    try { levelValue = Integer.parseInt(level); } catch (Exception e) { levelValue = 1; }

    int xpPerLevel = 50;
    int xpIntoLevel = xpValue % xpPerLevel;
    int xpToNext = xpPerLevel - xpIntoLevel;
    int percent = (xpIntoLevel * 100) / xpPerLevel;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>

<div class="navbar">
    <h2>CodeQuest</h2>
    <a class="logout" href="<%= request.getContextPath() %>/LogoutServlet">Logout</a>
</div>

<div class="container">

    <a class="start-btn" href="<%= request.getContextPath() %>/MissionsServlet">Start Missions</a>
    <a class="start-btn" href="<%= request.getContextPath() %>/LeaderboardServlet">Leaderboard</a>

    <div class="welcome">
        <h1>Welcome, <%= esc(username) %>!</h1>
        <p>Ready to continue your programming journey?</p>
        <p>Email: <%= esc(email) %></p>
    </div>

    <div class="progress-card">
        <div class="top">
            <span>Progress to Level <%= levelValue + 1 %></span>
            <span><b><%= xpIntoLevel %></b> / <%= xpPerLevel %> XP</span>
        </div>
        <div class="progress-track">
            <div class="progress-fill" style="width: <%= percent %>%;"></div>
        </div>
        <small><%= xpToNext %> XP more to reach the next level</small>
    </div>

    <div class="stats">
        <div class="card"><div class="num"><%= esc(xp) %></div>Total XP</div>
        <div class="card"><div class="num"><%= esc(level) %></div>Current Level</div>
        <div class="card"><div class="num"><%= esc(missions) %></div>Missions Completed</div>
    </div>

</div>

</body>
</html>