<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%!
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String medal(String rank) {
        if ("1".equals(rank)) return "&#129351;";
        if ("2".equals(rank)) return "&#129352;";
        if ("3".equals(rank)) return "&#129353;";
        return rank;
    }
%>
<%
    if (session.getAttribute("studentId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    @SuppressWarnings("unchecked")
    List<String[]> top = (List<String[]>) request.getAttribute("top");
    String[] myRow = (String[]) request.getAttribute("myRow");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Leaderboard</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: Arial, sans-serif; background: #0a1020; color: white; }
        .navbar {
            background: #101a33; padding: 18px 40px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .navbar h2 { margin: 0; color: #29b6f6; }
        .navbar a {
            background: #29b6f6; color: #0a1020; text-decoration: none;
            padding: 10px 18px; border-radius: 8px; font-weight: bold;
        }
        .container { max-width: 850px; margin: 35px auto; padding: 0 20px; }
        h1 { color: #29b6f6; margin-top: 0; }
        .mine {
            background: #1a2540; border: 2px solid #29b6f6; border-radius: 14px;
            padding: 20px; margin-bottom: 25px; text-align: center; font-size: 18px;
        }
        .mine b { color: #facc15; }
        .box { background: #1a2540; padding: 25px; border-radius: 14px; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #2a3860; }
        th { color: #29b6f6; }
        td.rank { font-size: 20px; font-weight: bold; width: 70px; }
        tr.me td { background: #0f2a4a; font-weight: bold; }
        .xp { color: #facc15; font-weight: bold; }
        .bad { background: #ef4444; padding: 10px; border-radius: 8px; margin-bottom: 15px; text-align: center; }
        .empty { text-align: center; padding: 30px; color: #9fb0d0; }
    </style>
</head>
<body>

<div class="navbar">
    <h2>CodeQuest</h2>
    <a href="<%= request.getContextPath() %>/DashboardServlet">Back to Dashboard</a>
</div>

<div class="container">
    <h1>&#127942; Leaderboard</h1>

    <% if (error != null) { %>
        <div class="bad"><%= esc(error) %></div>
    <% } %>

    <% if (myRow != null) { %>
    <div class="mine">
        Your rank: <b>#<%= esc(myRow[0]) %></b>
        &nbsp;|&nbsp; XP: <b><%= esc(myRow[3]) %></b>
        &nbsp;|&nbsp; Level: <b><%= esc(myRow[2]) %></b>
    </div>
    <% } %>

    <div class="box">
        <% if (top == null || top.isEmpty()) { %>
            <div class="empty">No students yet.</div>
        <% } else { %>
        <table>
            <tr>
                <th>Rank</th><th>Student</th><th>Level</th>
                <th>XP</th><th>Missions Completed</th>
            </tr>
            <% for (String[] r : top) { %>
            <tr class="<%= "true".equals(r[5]) ? "me" : "" %>">
                <td class="rank"><%= medal(r[0]) %></td>
                <td><%= esc(r[1]) %><%= "true".equals(r[5]) ? " (You)" : "" %></td>
                <td><%= esc(r[2]) %></td>
                <td class="xp"><%= esc(r[3]) %></td>
                <td><%= esc(r[4]) %></td>
            </tr>
            <% } %>
        </table>
        <% } %>
    </div>
</div>

</body>
</html>