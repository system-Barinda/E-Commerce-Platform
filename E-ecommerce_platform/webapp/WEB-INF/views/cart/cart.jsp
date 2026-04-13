<%-- views/cart/cart.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.OrderItem" %>
<%@ page import="java.text.NumberFormat" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Cart</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%-- Navbar include --%>
    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <%
        // Formatting helper
        NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);

        // Retrieve cart items and total from request
        List<OrderItem> cartItems = (List<OrderItem>) request.getAttribute("OrderItem");
        Double cartTotal = (Double) request.getAttribute("cartTotal");
        
        // Context path helper
        String cp = request.getContextPath();
    %>

    <div class="container">
        <h2>🛒 My Cart</h2>

        <%
            if (cartItems == null || cartItems.isEmpty()) {
        %>
            <div class="empty-cart-message">
                <p>Your cart is empty. <a href="<%= cp %>/products">Shop Now</a></p>
            </div>
        <%
            } else {
        %>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Price</th>
                        <th>Quantity</th>
                        <th>Subtotal</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (OrderItem item : cartItems) {
                            double subtotal = item.getProduct().getPrice() * item.getQuantity();
                    %>
                        <tr>
                            <td>
                                <div style="display:flex; align-items:center;">
                                    <img src="<%= item.getProduct().getImageUrl() %>" width="60" style="margin-right:15px; border-radius:4px;"/>
                                    <span><%= item.getProduct().getName() %></span>
                                </div>
                            </td>
                            <td><%= cur.format(item.getProduct().getPrice()) %></td>
                            <td>
                                <form action="<%= cp %>/cart" method="post" style="margin:0;">
                                    <input type="hidden" name="action" value="update"/>
                                    <input type="hidden" name="cartId" value="<%= item.getId() %>"/>
                                    <input type="number" name="quantity"
                                           value="<%= item.getQuantity() %>" 
                                           min="0"
                                           max="<%= item.getProduct().getStock() %>"
                                           onchange="this.form.submit()"
                                           style="width: 60px; padding: 5px;"/>
                                </form>
                            </td>
                            <td><%= cur.format(subtotal) %></td>
                            <td>
                                <form action="<%= cp %>/cart" method="post" style="margin:0;">
                                    <input type="hidden" name="action" value="remove"/>
                                    <input type="hidden" name="cartId" value="<%= item.getId() %>"/>
                                    <button type="submit" class="btn-danger" style="padding: 5px 10px;">Remove</button>
                                </form>
                            </td>
                        </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>

            <div class="cart-summary" style="text-align: right; margin-top: 20px; padding: 20px; border-top: 2px solid #eee;">
                <h3>Total: <%= (cartTotal != null) ? cur.format(cartTotal) : "$0.00" %></h3>
                <br>
                <a href="<%= cp %>/checkout" class="btn-success" style="text-decoration: none; font-size: 1.2em; padding: 10px 25px;">
                    Proceed to Checkout →
                </a>
            </div>
        <%
            }
        %>
    </div>
</body>
</html>