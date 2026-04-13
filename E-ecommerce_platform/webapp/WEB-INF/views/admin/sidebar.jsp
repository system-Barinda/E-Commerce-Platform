<%-- /WEB-INF/views/admin/sidebar.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
.sidebar {
    width: 220px;
    height: 100vh;
    position: fixed;
    left: 0;
    top: 0;
    background: #1e1e2f;
    color: white;
    padding: 20px 10px;
    box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar h2 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 20px;
    border-bottom: 1px solid #444;
    padding-bottom: 10px;
}

.sidebar a {
    display: block;
    padding: 12px 15px;
    margin: 8px 0;
    color: #ddd;
    text-decoration: none;
    border-radius: 6px;
    transition: 0.3s;
}

.sidebar a:hover {
    background: #343a40;
    color: #fff;
}

.sidebar a.active {
    background: #007bff;
    color: white;
}
</style>

<div class="sidebar">
    <h2>⚙️ Admin Panel</h2>

    <a href="${pageContext.request.contextPath}/admin/dashboard">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/admin/products">📦 Products</a>
    <a href="${pageContext.request.contextPath}/admin/orders">📋 Orders</a>
    <a href="${pageContext.request.contextPath}/admin/users">👥 Users</a>

    <hr style="border:0; border-top:1px solid #444; margin:20px 0;">

    <a href="${pageContext.request.contextPath}/logout" style="color:#ff6b6b;">
        🚪 Logout
    </a>
</div>