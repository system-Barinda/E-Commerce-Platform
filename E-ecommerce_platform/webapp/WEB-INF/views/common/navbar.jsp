<%-- views/common/navbar.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>

<%
    // Get the user and role from the session
    User loggedUser = (User) session.getAttribute("loggedUser");
    String role = (String) session.getAttribute("role");
    String cp = request.getContextPath();
%>

<nav class="navbar" style="display: flex; justify-content: space-between; align-items: center; padding: 1rem 2rem; background: #333; color: white;">
    <a class="brand" href="<%= cp %>/" style="text-decoration: none; color: white; font-size: 1.5rem; font-weight: bold;">
        🛍️ ShopZone
    </a>
    
    <div class="nav-links">
        <a href="<%= cp %>/products" style="color: white; margin-left: 15px; text-decoration: none;">Products</a>
        
        <%
            if (loggedUser != null) {
                // User is logged in
        %>
                <a href="<%= cp %>/cart" style="color: white; margin-left: 15px; text-decoration: none;">🛒 Cart</a>
                
                <a href="<%= cp %>/profile" style="color: white; margin-left: 15px; text-decoration: none;">
                    👤 <%= loggedUser.getName() %>
                </a>

                <% if ("admin".equals(role)) { %>
                    <a href="<%= cp %>/admin/dashboard" style="color: #ffcc00; margin-left: 15px; text-decoration: none; font-weight: bold;">
                        ⚙️ Admin
                    </a>
                <% } %>

                <a href="<%= cp %>/logout" style="color: #ff4d4d; margin-left: 15px; text-decoration: none;">Logout</a>
        <%
            } else {
                // User is not logged in
        %>
                <a href="<%= cp %>/login" style="color: white; margin-left: 15px; text-decoration: none;">Login</a>
                <a href="<%= cp %>/register" style="color: white; margin-left: 15px; text-decoration: none;">Register</a>
        <%
            }
        %>
    </div>
</nav>