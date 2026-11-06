<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>CodeQuest - Admin Login</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #0a1020;
            color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .box {
            background: #1a2540;
            padding: 40px;
            border-radius: 14px;
            width: 380px;
        }
        h2 { color: #29b6f6; text-align: center; margin-top: 0; }
        label { display: block; margin: 15px 0 5px; }
        input {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            background: #0f1830;
            color: white;
            font-size: 15px;
        }
        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            border: none;
            border-radius: 8px;
            background: #29b6f6;
            color: #0a1020;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
        }
        .error {
            background: #ef4444;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 10px;
            text-align: center;
        }
        .back { text-align: center; margin-top: 18px; }
        .back a { color: #29b6f6; text-decoration: none; }
    </style>
</head>
<body>
    <div class="box">
        <h2>Admin Login</h2>

        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>

        <form action="<%= request.getContextPath() %>/AdminLoginServlet" method="post">
            <label>Username</label>
            <input type="text" name="username" required>

            <label>Password</label>
            <input type="password" name="password" required>

            <button type="submit">Login</button>
        </form>

        <div class="back"><a href="<%= request.getContextPath() %>/login.jsp">Student Login</a></div>
    </div>
</body>
</html>