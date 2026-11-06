<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%
    if (session.getAttribute("adminId") == null) {
        response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
        return;
    }
    @SuppressWarnings("unchecked")
    List<String[]> missions = (List<String[]>) request.getAttribute("missions");
    String error = (String) request.getAttribute("error");
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - All Missions</title>
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
        .container { max-width: 1000px; margin: 35px auto; padding: 0 20px; }
        .box { background: #1a2540; padding: 30px; border-radius: 14px; overflow-x: auto; }
        h1 { color: #29b6f6; margin-top: 0; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #2a3860; }
        th { color: #29b6f6; }
        .del {
            background: #ef4444; color: white; border: none;
            padding: 8px 14px; border-radius: 6px; cursor: pointer; font-size: 14px;
        }
        .ok { background: #22c55e; color: #04200d; padding: 10px; border-radius: 8px; margin-bottom: 15px; text-align: center; font-weight: bold; }
        .bad { background: #ef4444; padding: 10px; border-radius: 8px; margin-bottom: 15px; text-align: center; }
        .empty { text-align: center; padding: 30px; color: #9fb0d0; }
    </style>
</head>
<body>

<div class="navbar">
    <h2>CodeQuest Admin</h2>
    <a href="<%= request.getContextPath() %>/AdminDashboardServlet">Back to Dashboard</a>
</div>

<div class="container">
    <div class="box">
        <h1>All Missions</h1>

        <% if ("deleted".equals(msg)) { %>
            <div class="ok">Mission deleted.</div>
        <% } else if ("failed".equals(msg)) { %>
            <div class="bad">Could not delete the mission.</div>
        <% } %>
        <% if (error != null) { %>
            <div class="bad"><%= error %></div>
        <% } %>

        <% if (missions == null || missions.isEmpty()) { %>
            <div class="empty">No missions yet. Add your first mission.</div>
        <% } else { %>
        <table>
            <tr>
                <th>ID</th><th>Title</th><th>Category</th>
                <th>Difficulty</th><th>Answer</th><th>XP</th><th>Action</th>
            </tr>
            <% for (String[] m : missions) { %>
            <tr>
                <td><%= m[0] %></td>
                <td><%= m[1].replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") %></td>
                <td><%= m[2] %></td>
                <td><%= m[3] %></td>
                <td><%= m[4] %></td>
                <td><%= m[5] %></td>
                <td>
                    <form action="<%= request.getContextPath() %>/DeleteMissionServlet"
                          method="post"
                          onsubmit="return confirm('Delete this mission?');">
                        <input type="hidden" name="id" value="<%= m[0] %>">
                        <button type="submit" class="del">Delete</button>
                    </form>
                </td>
            </tr>
            <% } %>
        </table>
        <% } %>
    </div>
</div>

</body>
</html>