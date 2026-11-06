<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLDecoder, java.nio.charset.StandardCharsets" %>
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
    String username = request.getParameter("username");

    // Read the "remember me" cookie
    boolean fromCookie = false;
    if (username == null) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("rememberedUser".equals(c.getName())) {
                    try {
                        username = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                        fromCookie = true;
                    } catch (Exception e) {
                        username = null;
                    }
                }
            }
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Student Login</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="center">

<div class="box">
    <h2>Welcome Back!</h2>

    <% if (error != null) { %>
        <div class="error"><%= esc(error) %></div>
    <% } %>
    <% if (success != null) { %>
        <div class="success"><%= esc(success) %></div>
    <% } %>

    <form action="<%= request.getContextPath() %>/LoginServlet" method="post">

        <label>Username or Email</label>
        <input type="text" name="username"
               placeholder="Enter username or email"
               value="<%= esc(username) %>" required>

        <label>Password</label>
        <input type="password" name="password"
               placeholder="Enter password" required>

        <label style="display:flex; align-items:center; gap:8px; margin:14px 0 0;">
            <input type="checkbox" name="remember" value="yes" <%= fromCookie ? "checked" : "" %>>
            Remember my username
        </label>

        <button type="submit">Login</button>
    </form>

    <div class="back">
        Don't have an account?
        <a href="<%= request.getContextPath() %>/register.jsp">Register Here</a>
    </div>
    <div class="back">
        <a href="<%= request.getContextPath() %>/index.html">Home</a>
    </div>
</div>

</body>
</html>