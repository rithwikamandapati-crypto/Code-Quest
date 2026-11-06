<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Safety check: only admin can open this page
    if (session.getAttribute("adminId") == null) {
        response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
        return;
    }
    String adminName = (String) session.getAttribute("adminUsername");
    Object students = request.getAttribute("totalStudents");
    Object missions = request.getAttribute("totalMissions");
    Object submissions = request.getAttribute("totalSubmissions");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Admin Dashboard</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: Arial, sans-serif; background: #0a1020; color: white; }
        .navbar {
            background: #101a33;
            padding: 18px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .navbar h2 { margin: 0; color: #29b6f6; }
        .navbar a {
            background: #ef4444; color: white; text-decoration: none;
            padding: 10px 18px; border-radius: 8px;
        }
        .container { max-width: 1000px; margin: 40px auto; padding: 0 20px; }
        .welcome { background: #1a2540; padding: 30px; border-radius: 14px; }
        .welcome h1 { color: #29b6f6; margin-top: 0; }
        .stats { display: flex; gap: 20px; margin-top: 25px; flex-wrap: wrap; }
        .card {
            flex: 1; min-width: 200px; background: #1a2540;
            padding: 30px; border-radius: 14px; text-align: center;
        }
        .card .num { font-size: 32px; font-weight: bold; color: #29b6f6; }
        .actions { margin-top: 25px; }
        .actions a {
            display: inline-block; background: #29b6f6; color: #0a1020;
            text-decoration: none; font-weight: bold;
            padding: 14px 24px; border-radius: 10px; margin-right: 10px;
        }
    </style>
</head>
<body>

<div class="navbar">
    <h2>CodeQuest Admin</h2>
    <a href="<%= request.getContextPath() %>/AdminLogoutServlet">Logout</a>
</div>

<div class="container">
    <div class="welcome">
        <h1>Welcome, <%= adminName %>!</h1>
        <p>Manage programming missions for your students.</p>
    </div>

    <div class="stats">
        <div class="card"><div class="num"><%= students %></div>Total Students</div>
        <div class="card"><div class="num"><%= missions %></div>Total Missions</div>
        <div class="card"><div class="num"><%= submissions %></div>Total Submissions</div>
    </div>
    <div class="progress-card">
        <div class="top">
            <span><b><%= request.getAttribute("panelTitle") %></b></span>
            <span>Application: <%= request.getAttribute("appName") %></span>
        </div>
        <small>Student logins since the server started: <b><%= request.getAttribute("loginCount") %></b></small>
    </div>

        <div class="actions">
        <a href="<%= request.getContextPath() %>/addMission.jsp">+ Add New Mission</a>
        <a href="<%= request.getContextPath() %>/ViewMissionsServlet">View All Missions</a>
    </div>
</div>

</body>
</html>