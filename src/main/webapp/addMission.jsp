<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Only admin can open this page
    if (session.getAttribute("adminId") == null) {
        response.sendRedirect(request.getContextPath() + "/adminLogin.jsp");
        return;
    }
    String error = (String) request.getAttribute("error");
    String success = request.getParameter("success");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CodeQuest - Add Mission</title>
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
        label { display: block; margin: 16px 0 6px; }
        input, textarea, select {
            width: 100%; padding: 12px; border: none; border-radius: 8px;
            background: #0f1830; color: white; font-size: 15px; font-family: Arial, sans-serif;
        }
        textarea { min-height: 110px; resize: vertical; }
        .row { display: flex; gap: 15px; }
        .row > div { flex: 1; }
        button {
            width: 100%; margin-top: 25px; padding: 14px; border: none;
            border-radius: 8px; background: #29b6f6; color: #0a1020;
            font-size: 16px; font-weight: bold; cursor: pointer;
        }
        .error { background: #ef4444; padding: 10px; border-radius: 8px; margin-bottom: 10px; text-align: center; }
        .success { background: #22c55e; color: #04200d; padding: 10px; border-radius: 8px; margin-bottom: 10px; text-align: center; font-weight: bold; }
    </style>
</head>
<body>

<div class="navbar">
    <h2>CodeQuest Admin</h2>
    <a href="<%= request.getContextPath() %>/AdminDashboardServlet">Back to Dashboard</a>
</div>

<div class="container">
    <div class="box">
        <h1>Add New Mission</h1>

        <% if (error != null) { %>
            <div class="error"><%= error %></div>
        <% } %>
        <% if ("1".equals(success)) { %>
            <div class="success">Mission added successfully!</div>
        <% } %>

        <form action="<%= request.getContextPath() %>/AddMissionServlet" method="post">

            <label>Mission Title</label>
            <input type="text" name="title" maxlength="150" required>

            <label>Question / Description</label>
            <textarea name="description" required></textarea>

            <div class="row">
                <div>
                    <label>Category</label>
                    <select name="category" required>
                        <option value="Java">Java</option>
                        <option value="Python">Python</option>
                        <option value="C">C</option>
                        <option value="C++">C++</option>
                        <option value="HTML">HTML</option>
                        <option value="CSS">CSS</option>
                        <option value="JavaScript">JavaScript</option>
                         <option value="DSA">DSA</option>
                    </select>
                </div>
                <div>
                    <label>Difficulty</label>
                    <select name="difficulty" required>
                        <option value="Easy">Easy</option>
                        <option value="Medium">Medium</option>
                        <option value="Hard">Hard</option>
                    </select>
                </div>
            </div>

            <label>Option A</label>
            <input type="text" name="optionA" maxlength="255" required>

            <label>Option B</label>
            <input type="text" name="optionB" maxlength="255" required>

            <label>Option C</label>
            <input type="text" name="optionC" maxlength="255" required>

            <label>Option D</label>
            <input type="text" name="optionD" maxlength="255" required>

            <div class="row">
                <div>
                    <label>Correct Answer</label>
                    <select name="correctAnswer" required>
                        <option value="A">A</option>
                        <option value="B">B</option>
                        <option value="C">C</option>
                        <option value="D">D</option>
                    </select>
                </div>
                <div>
                    <label>XP Reward</label>
                    <input type="number" name="xpReward" min="1" max="1000" value="10" required>
                </div>
            </div>

            <button type="submit">Save Mission</button>
        </form>
    </div>
</div>

</body>
</html>