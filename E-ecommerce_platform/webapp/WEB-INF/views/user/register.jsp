<%-- views/user/register.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String error = (String) request.getAttribute("error");
    String cp = request.getContextPath();

    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String role = request.getParameter("role"); // 👈 NEW
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - ShopZone</title>
    <link rel="stylesheet" href="<%= cp %>/css/auth.css">
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <h2>🛍️ Create Account</h2>

        <% if (error != null && !error.isEmpty()) { %>
            <div class="alert error" style="background: #f8d7da; color: #721c24; padding: 10px; border-radius: 4px; margin-bottom: 15px; border: 1px solid #f5c6cb;">
                <%= error %>
            </div>
        <% } %>

        <form action="<%= cp %>/register" method="post">

            <!-- NAME -->
            <div class="form-group" style="margin-bottom: 10px;">
                <label>Full Name</label>
                <input type="text" name="name"
                       placeholder="John Doe"
                       value="<%= (name != null) ? name : "" %>"
                       required />
            </div>

            <!-- EMAIL -->
            <div class="form-group" style="margin-bottom: 10px;">
                <label>Email Address</label>
                <input type="email" name="email"
                       placeholder="john@example.com"
                       value="<%= (email != null) ? email : "" %>"
                       required />
            </div>

            <!-- PASSWORD -->
            <div class="form-group" style="margin-bottom: 10px;">
                <label>Password</label>
                <input type="password" name="password"
                       placeholder="Min 6 characters"
                       required />
            </div>

            <!-- CONFIRM -->
            <div class="form-group" style="margin-bottom: 10px;">
                <label>Confirm Password</label>
                <input type="password" name="confirmPassword"
                       placeholder="Repeat password"
                       required />
            </div>

            <!-- ROLE (NEW) -->
            <div class="form-group" style="margin-bottom: 20px;">
                <label>Select Role</label>
                <select name="role" style="width:100%; padding:10px;">
                    <option value="user"
                        <%= (role == null || role.equals("user")) ? "selected" : "" %>>
                        User
                    </option>

                    <option value="admin"
                        <%= ("admin".equals(role)) ? "selected" : "" %>>
                        Admin
                    </option>
                </select>
            </div>

            <button type="submit" class="btn-primary"
                    style="width: 100%; padding: 12px; background: #333; color: white; border: none; border-radius: 4px; cursor: pointer;">
                Register
            </button>
        </form>

        <p style="text-align: center; margin-top: 15px;">
            Already have an account?
            <a href="<%= cp %>/login">Login here</a>
        </p>
    </div>
</div>
</body>
</html>