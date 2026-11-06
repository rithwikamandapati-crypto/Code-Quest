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
    String[] m = (String[]) request.getAttribute("mission");
    if (m == null) {
        response.sendRedirect(request.getContextPath() + "/MissionsServlet");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - <%= esc(m[1]) %></title>
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
        .container { max-width: 750px; margin: 35px auto; padding: 0 20px; }
        .box { background: #1a2540; padding: 35px; border-radius: 14px; }
        h1 { color: #29b6f6; margin-top: 0; }
        .tags span {
            display: inline-block; background: #0f1830; color: #29b6f6;
            padding: 4px 10px; border-radius: 20px; font-size: 13px; margin-right: 6px;
        }
        .xp { color: #facc15; font-weight: bold; margin: 12px 0; }
        .question { font-size: 18px; margin: 20px 0; line-height: 1.5; white-space: pre-wrap; }
        .option {
            display: block; background: #0f1830; padding: 14px 16px;
            border-radius: 10px; margin-bottom: 12px; cursor: pointer;
            border: 2px solid transparent;
        }
        .option:hover { border-color: #29b6f6; }
        .option input { margin-right: 12px; }
        button {
            width: 100%; margin-top: 15px; padding: 14px; border: none;
            border-radius: 8px; background: #29b6f6; color: #0a1020;
            font-size: 16px; font-weight: bold; cursor: pointer;
        }
    </style>
</head>
<body>

<div class="navbar">
    <h2>CodeQuest</h2>
    <a href="<%= request.getContextPath() %>/MissionsServlet">Back to Missions</a>
</div>

<a href="<%= request.getContextPath() %>/MissionsServlet"
       style="display:inline-block; background:#29b6f6; color:#0a1020; text-decoration:none; font-weight:bold; padding:14px 26px; border-radius:10px; margin-bottom:20px;">
       Start Missions
    </a>
    <div class="box">
        <h1><%= esc(m[1]) %></h1>
        <div class="tags">
            <span><%= esc(m[3]) %></span>
            <span><%= esc(m[4]) %></span>
        </div>
        <div class="xp">Reward: +<%= esc(m[9]) %> XP</div>

        <div class="question"><%= esc(m[2]) %></div>

        <form action="<%= request.getContextPath() %>/SubmitAnswerServlet" method="post">
            <input type="hidden" name="missionId" value="<%= esc(m[0]) %>">

            <label class="option"><input type="radio" name="answer" value="A" required> A. <%= esc(m[5]) %></label>
            <label class="option"><input type="radio" name="answer" value="B"> B. <%= esc(m[6]) %></label>
            <label class="option"><input type="radio" name="answer" value="C"> C. <%= esc(m[7]) %></label>
            <label class="option"><input type="radio" name="answer" value="D"> D. <%= esc(m[8]) %></label>

            <button type="submit">Submit Answer</button>
        </form>
    </div>

</body>
</html>