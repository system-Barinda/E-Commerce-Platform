<%-- views/cart/checkout.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.OrderItem" %>
<%@ page import="model.Product" %>
<%@ page import="java.text.NumberFormat" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <%-- Navbar include --%>
    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <%
        // Formatting helper
        NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);

        // Retrieve cart items and total from the request (sent by CheckoutServlet)
        List<OrderItem> cartItems = (List<OrderItem>) request.getAttribute("OrderItem");
        Double cartTotal = (Double) request.getAttribute("cartTotal");
        
        String cp = request.getContextPath();
    %>

    <div class="container">
        <h2>✅ Order Summary</h2>

        <% if (cartItems != null && !cartItems.isEmpty()) { %>
            <table class="cart-table" style="width:100%; border-collapse: collapse; margin-bottom: 20px;">
                <thead>
                    <tr style="border-bottom: 2px solid #ddd; text-align: left;">
                        <th>Product</th>
                        <th>Qty</th>
                        <th>Price</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (OrderItem item : cartItems) {
                            Product p = item.getProduct();
                            double subtotal = (p != null) ? p.getPrice() * item.getQuantity() : 0;
                    %>
                        <tr style="border-bottom: 1px solid #eee;">
                            <td style="padding: 10px 0;"><%= (p != null) ? p.getName() : "Unknown Product" %></td>
                            <td><%= item.getQuantity() %></td>
                            <td><%= (p != null) ? cur.format(p.getPrice()) : "$0.00" %></td>
                            <td><%= cur.format(subtotal) %></td>
                        </tr>
                    <% } %>
                </tbody>
            </table>

            <div class="cart-summary" style="text-align: right; padding: 20px; background: #fdfdfd; border: 1px solid #eee; border-radius: 8px;">
                <h3 style="margin-bottom: 15px;">Total Amount: <%= (cartTotal != null) ? cur.format(cartTotal) : "$0.00" %></h3>
                
                <form action="<%= cp %>/checkout" method="post">
                    <button type="submit" class="btn-success" style="padding: 12px 30px; font-size: 1.1em; cursor: pointer;">
                        Place Order 🎉
                    </button>
                </form>
                
                <div style="margin-top: 15px;">
                    <a href="<%= cp %>/cart" style="text-decoration: none; color: #666;">← Back to Cart</a>
                </div>
            </div>

        <% } else { %>
            <div style="text-align: center; padding: 40px;">
                <p>Your session may have expired or your cart is empty.</p>
                <a href="<%= cp %>/products" class="btn-primary">Return to Shop</a>
            </div>
        <% } %>
    </div>
</body>
</html>