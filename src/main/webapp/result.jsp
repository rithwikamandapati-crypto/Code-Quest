<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    String[] r = (String[]) request.getAttribute("result");
    if (r == null) {
        response.sendRedirect(request.getContextPath() + "/MissionsServlet");
        return;
    }
    boolean ok = "true".equals(r[5]);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Result</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: Arial, sans-serif; background: #0a1020; color: white; }
        .navbar {
            background: #101a33; padding: 18px 40px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .navbar h2 { margin: 0; color: #29b6f6; }
        .container { max-width: 650px; margin: 40px auto; padding: 0 20px; }
        .box { background: #1a2540; padding: 40px; border-radius: 14px; text-align: center; }
        .icon { font-size: 60px; }
        .good { color: #22c55e; }
        .bad { color: #ef4444; }
        h1 { margin: 10px 0; }
        .mission { color: #9fb0d0; margin-bottom: 25px; }
        .answer {
            background: #0f1830; padding: 14px; border-radius: 10px;
            margin: 10px 0; text-align: left;
        }
        .answer small { color: #9fb0d0; display: block; margin-bottom: 4px; }
        .xp { color: #facc15; font-weight: bold; font-size: 18px; margin: 20px 0; }
        .btn {
            display: inline-block; background: #29b6f6; color: #0a1020;
            text-decoration: none; font-weight: bold;
            padding: 12px 22px; border-radius: 8px; margin: 6px;
        }
    </style>
</head>
<body>

<div class="navbar">
    <h2>CodeQuest</h2>
</div>

<div class="container">
    <div class="box">
        <% if (ok) { %>
            <div class="icon good">&#10004;</div>
            <h1 class="good">Correct!</h1>
        <% } else { %>
            <div class="icon bad">&#10006;</div>
            <h1 class="bad">Incorrect</h1>
        <% } %>

        <div class="mission">Mission: <%= esc(r[0]) %></div>

        <div class="answer">
            <small>Your answer</small>
            <%= esc(r[1]) %>. <%= esc(r[2]) %>
        </div>

        <% if (!ok) { %>
        <div class="answer">
            <small>Correct answer</small>
            <%= esc(r[3]) %>. <%= esc(r[4]) %>
        </div>
        <% } %>

               <% if (ok) { %>
            <div class="xp">+<%= esc(r[6]) %> XP earned!</div>
            <% if ("true".equals(r[9])) { %>
                <div class="xp" style="color:#22c55e;">&#127881; Level Up! You are now Level <%= esc(r[8]) %></div>
            <% } %>
        <% } else { %>
            <div class="xp">No XP this time. Keep going!</div>
        <% } %>
        <div class="answer" style="text-align:center;">
            Total XP: <b><%= esc(r[7]) %></b> &nbsp;|&nbsp; Level: <b><%= esc(r[8]) %></b>
        </div>
        <a class="btn" href="<%= request.getContextPath() %>/MissionsServlet">More Missions</a>
        <a class="btn" href="<%= request.getContextPath() %>/DashboardServlet">Dashboard</a>
    </div>
</div>

</body>
</html>