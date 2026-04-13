<%-- views/admin/orders.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Order" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String cp = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Orders</title>

<link rel="stylesheet" href="<%= cp %>/css/admin.css">

<style>
/* ===== FIX SIDEBAR ALIGNMENT ===== */
.main-content {
    margin-left: 240px; /* SAME as sidebar width */
    padding: 25px;
    background: #f4f6f9;
    min-height: 100vh;
}

/* ===== TITLE ===== */
h2 {
    margin-bottom: 20px;
}

/* ===== TABLE ===== */
.admin-table {
    width: 100%;
    background: #fff;
    border-collapse: collapse;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}

.admin-table th {
    background: #333;
    color: white;
    padding: 12px;
    text-align: left;
}

.admin-table td {
    padding: 12px;
    border-bottom: 1px solid #eee;
}

.admin-table tr:hover {
    background: #f1f1f1;
}

/* ===== STATUS BADGES ===== */
.badge {
    padding: 5px 10px;
    border-radius: 6px;
    font-size: 0.85rem;
    color: white;
}

.badge.pending { background: #ffc107; color: black; }
.badge.confirmed { background: #17a2b8; }
.badge.shipped { background: #007bff; }
.badge.delivered { background: #28a745; }
.badge.cancelled { background: #dc3545; }

/* ===== FORM ===== */
select {
    padding: 5px;
    border-radius: 5px;
    margin-right: 5px;
}

/* ===== BUTTON ===== */
.btn-edit {
    background: #007bff;
    color: white;
    border: none;
    padding: 6px 10px;
    border-radius: 5px;
    cursor: pointer;
}

.btn-edit:hover {
    background: #0056b3;
}
</style>
</head>

<body>

<jsp:include page="/WEB-INF/views/admin/sidebar.jsp" />

<div class="main-content">

<h2>📋 Manage Orders</h2>

<table class="admin-table">
<thead>
<tr>
<th>Order ID</th>
<th>Customer</th>
<th>Email</th>
<th>Amount</th>
<th>Status</th>
<th>Date</th>
<th>Update</th>
</tr>
</thead>

<tbody>

<%
if (orders != null && !orders.isEmpty()) {
    for (Order o : orders) {
        String currentStatus = (o.getStatus() != null) ? o.getStatus() : "";
%>

<tr>
<td>#<%= o.getId() %></td>

<td><%= (o.getUser() != null) ? o.getUser().getName() : "N/A" %></td>

<td><%= (o.getUser() != null) ? o.getUser().getEmail() : "N/A" %></td>

<td><%= cur.format(o.getTotalAmount()) %></td>

<td>
<span class="badge <%= currentStatus.toLowerCase() %>">
    <%= currentStatus %>
</span>
</td>

<td><%= sdf.format(o.getCreatedAt()) %></td>

<td>
<form action="<%= cp %>/admin/orders" method="post">
<input type="hidden" name="orderId" value="<%= o.getId() %>"/>

<select name="status">
<option value="pending"   <%= currentStatus.equals("pending")   ? "selected" : "" %>>Pending</option>
<option value="confirmed" <%= currentStatus.equals("confirmed") ? "selected" : "" %>>Confirmed</option>
<option value="shipped"   <%= currentStatus.equals("shipped")   ? "selected" : "" %>>Shipped</option>
<option value="delivered" <%= currentStatus.equals("delivered") ? "selected" : "" %>>Delivered</option>
<option value="cancelled" <%= currentStatus.equals("cancelled") ? "selected" : "" %>>Cancelled</option>
</select>

<button type="submit" class="btn-edit">Update</button>
</form>
</td>

</tr>

<%
    }
} else {
%>

<tr>
<td colspan="7" style="text-align:center;">No orders found in the database.</td>
</tr>

<%
}
%>

</tbody>
</table>

</div>

</body>
</html>