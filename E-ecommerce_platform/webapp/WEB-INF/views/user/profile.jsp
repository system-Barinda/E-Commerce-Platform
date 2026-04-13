<%-- views/user/profile.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="model.Order" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    // Setup helpers
    NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    String cp = request.getContextPath();

    // Retrieve data from Servlet/Session
    User user = (User) session.getAttribute("loggedUser");
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile - ShopZone</title>
    <link rel="stylesheet" href="<%= cp %>/css/style.css">
    <link rel="stylesheet" href="<%= cp %>/css/auth.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="padding: 20px;">
    <h2>👤 My Profile</h2>

    <%-- Success & Error Alerts --%>
    <% if (success != null) { %>
        <div class="alert success" style="background:#d4edda; color:#155724; padding:10px; border-radius:4px; margin-bottom:15px; border:1px solid #c3e6cb;">
            <%= success %>
        </div>
    <% } %>
    <% if (error != null) { %>
        <div class="alert error" style="background:#f8d7da; color:#721c24; padding:10px; border-radius:4px; margin-bottom:15px; border:1px solid #f5c6cb;">
            <%= error %>
        </div>
    <% } %>

    <% if (user != null) { %>
    <div class="profile-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">

        <%-- Update Profile Card --%>
        <div class="profile-card" style="border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
            <h3>Update Profile</h3>
            <form action="<%= cp %>/profile" method="post">
                <input type="hidden" name="action" value="updateProfile"/>
                
                <label style="display:block; margin-top:10px;">Full Name</label>
                <input type="text" name="name" value="<%= user.getName() %>" required style="width:100%; padding:8px; margin-bottom:10px;"/>
                
                <label style="display:block;">Email</label>
                <input type="email" name="email" value="<%= user.getEmail() %>" required style="width:100%; padding:8px; margin-bottom:15px;"/>
                
                <button type="submit" class="btn-primary" style="padding:10px 20px; cursor:pointer;">Update</button>
            </form>
        </div>

        <%-- Change Password Card --%>
        <div class="profile-card" style="border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
            <h3>Change Password</h3>
            <form action="<%= cp %>/profile" method="post">
                <input type="hidden" name="action" value="changePassword"/>
                
                <label style="display:block; margin-top:10px;">Old Password</label>
                <input type="password" name="oldPassword" required style="width:100%; padding:8px; margin-bottom:10px;"/>
                
                <label style="display:block;">New Password</label>
                <input type="password" name="newPassword" required style="width:100%; padding:8px; margin-bottom:10px;"/>
                
                <label style="display:block;">Confirm New Password</label>
                <input type="password" name="confirmPassword" required style="width:100%; padding:8px; margin-bottom:15px;"/>
                
                <button type="submit" class="btn-primary" style="padding:10px 20px; cursor:pointer;">Change Password</button>
            </form>
        </div>
    </div>
    <% } %>

    <%-- Order History Section --%>
    <h3 style="margin-top:30px;">🧾 My Orders</h3>
    <%
        if (orders == null || orders.isEmpty()) {
    %>
        <p>You have no orders yet. <a href="<%= cp %>/products">Shop Now</a></p>
    <%
        } else {
    %>
        <table class="cart-table" style="width:100%; border-collapse: collapse; margin-top:10px;">
            <thead>
                <tr style="background:#f4f4f4; text-align:left;">
                    <th style="padding:10px;">Order ID</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <% for (Order o : orders) { %>
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding:10px;">#<%= o.getId() %></td>
                        <td><%= cur.format(o.getTotalAmount()) %></td>
                        <td>
                            <span class="badge <%= o.getStatus().toLowerCase() %>" style="padding:4px 8px; border-radius:4px; font-size:0.8em;">
                                <%= o.getStatus() %>
                            </span>
                        </td>
                        <td><%= sdf.format(o.getCreatedAt()) %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
</div>
</body>
</html>