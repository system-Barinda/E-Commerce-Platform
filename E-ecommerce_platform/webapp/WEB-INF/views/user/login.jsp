<%-- views/user/login.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Retrieve potential messages from the Servlet
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String cp = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - ShopZone</title>
    <link rel="stylesheet" href="<%= cp %>/css/auth.css">
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <h2>🔐 Welcome Back</h2>

        <%-- Error Message Display --%>
        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert error" style="background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; border: 1px solid #f5c6cb;">
                <%= error %>
            </div>
        <% } %>

        <%-- Success Message Display (e.g., after registration) --%>
        <% if (success != null && !success.isEmpty()) { %>
            <div class="alert success" style="background: #d4edda; color: #155724; padding: 10px; border-radius: 4px; margin-bottom: 15px; border: 1px solid #c3e6cb;">
                <%= success %>
            </div>
        <% } %>

        <form action="<%= cp %>/login" method="post">
            <div class="form-group" style="margin-bottom: 15px;">
                <label style="display: block; margin-bottom: 5px;">Email Address</label>
                <input type="email" name="email" 
                       placeholder="john@example.com" 
                       required 
                       style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"/>
            </div>

            <div class="form-group" style="margin-bottom: 20px;">
                <label style="display: block; margin-bottom: 5px;">Password</label>
                <input type="password" name="password" 
                       placeholder="Your password" 
                       required 
                       style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"/>
            </div>

            <button type="submit" class="btn-primary" 
                    style="width: 100%; padding: 12px; background: #333; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem;">
                Login
            </button>
        </form>

        <p class="auth-link" style="text-align: center; margin-top: 15px;">
            Don't have an account? 
            <a href="<%= cp %>/register" style="color: #007bff; text-decoration: none;">Register here</a>
        </p>
    </div>
</div>
</body>
</html>