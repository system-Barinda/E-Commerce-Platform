<%-- /WEB-INF/views/admin/dashboard.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Order" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%
    NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    Integer totalUsers    = (Integer) request.getAttribute("totalUsers");
    Integer totalProducts = (Integer) request.getAttribute("totalProducts");
    Integer totalOrders   = (Integer) request.getAttribute("totalOrders");
    Double totalRevenue   = (Double) request.getAttribute("totalRevenue");
    List<Order> recentOrders = (List<Order>) request.getAttribute("recentOrders");

    if (totalUsers == null) totalUsers = 0;
    if (totalProducts == null) totalProducts = 0;
    if (totalOrders == null) totalOrders = 0;
    if (totalRevenue == null) totalRevenue = 0.0;
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
        }

        .main-content {
            margin-left: 220px;
            padding: 20px;
        }

        h2 {
            margin-bottom: 20px;
        }

        /* ===== CARDS ===== */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }

        .stat-card {
            padding: 20px;
            border-radius: 10px;
            color: white;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }

        .stat-card h3 {
            font-size: 28px;
            margin-bottom: 5px;
        }

        .stat-card p {
            opacity: 0.9;
        }

        .blue { background: #007bff; }
        .green { background: #28a745; }
        .orange { background: #fd7e14; }
        .purple { background: #6f42c1; }

        /* ===== TABLE ===== */
        .table-card {
            background: white;
            margin-top: 30px;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.05);
        }

        .admin-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .admin-table th {
            background: #333;
            color: white;
            padding: 10px;
        }

        .admin-table td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        .admin-table tr:hover {
            background: #f1f1f1;
        }

        /* ===== BADGES ===== */
        .badge {
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            color: white;
        }

        .pending { background: orange; }
        .completed { background: green; }
        .cancelled { background: red; }
    </style>
</head>

<body>

    <jsp:include page="/WEB-INF/views/admin/sidebar.jsp" />

    <div class="main-content">

        <h2>📊 Dashboard Overview</h2>

        <!-- ===== STATS ===== -->
        <div class="stats-grid">
            <div class="stat-card blue">
                <h3><%= totalUsers %></h3>
                <p>Total Users</p>
            </div>

            <div class="stat-card green">
                <h3><%= totalProducts %></h3>
                <p>Total Products</p>
            </div>

            <div class="stat-card orange">
                <h3><%= totalOrders %></h3>
                <p>Total Orders</p>
            </div>

            <div class="stat-card purple">
                <h3><%= cur.format(totalRevenue) %></h3>
                <p>Total Revenue</p>
            </div>
        </div>

        <!-- ===== ORDERS ===== -->
        <div class="table-card">
            <h3>🧾 Recent Orders</h3>

            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Customer</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Date</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    if (recentOrders != null && !recentOrders.isEmpty()) {
                        for (Order o : recentOrders) {
                %>
                    <tr>
                        <td>#<%= o.getId() %></td>

                        <td>
                            <%= (o.getUser() != null)
                                ? o.getUser().getName()
                                : "Guest" %>
                        </td>

                        <td><%= cur.format(o.getTotalAmount()) %></td>

                        <td>
                            <span class="badge <%= o.getStatus().toLowerCase() %>">
                                <%= o.getStatus() %>
                            </span>
                        </td>

                        <td>
                            <%= (o.getCreatedAt() != null)
                                ? sdf.format(o.getCreatedAt())
                                : "-" %>
                        </td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr>
                        <td colspan="5" style="text-align:center;">
                            No orders found.
                        </td>
                    </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

    </div>

</body>
</html>