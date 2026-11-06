<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Student Registration</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="center">

<div class="box">
    <h2>Create Your Account</h2>

    <% if (error != null) { %>
        <div class="error"><%= esc(error) %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="success"><%= esc(success) %></div>
    <% } %>

    <form action="<%= request.getContextPath() %>/RegisterServlet" method="post">

        <label>Username</label>
        <input type="text" name="username" placeholder="Enter username" required>

        <label>Email Address</label>
        <input type="email" name="email" placeholder="Enter email" required>

        <label>Password</label>
        <input type="password" name="password" placeholder="Enter password (min 8 characters)" required>

        <label>Confirm Password</label>
        <input type="password" name="confirmPassword" placeholder="Confirm password" required>

        <button type="submit">Register Now</button>
    </form>

    <div class="back">
        Already registered?
        <a href="<%= request.getContextPath() %>/login.jsp">Login Here</a>
    </div>
    <div class="back">
        <a href="<%= request.getContextPath() %>/index.html">Home</a>
    </div>
</div>

</body>
</html>