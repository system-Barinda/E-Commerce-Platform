<%-- views/product/detail.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Product" %>
<%@ page import="model.User" %>
<%@ page import="java.text.NumberFormat" %>

<%
    // 1. Setup helpers
    NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);
    String cp = request.getContextPath();

    // 2. Retrieve Product from request (sent by ProductDetailServlet)
    Product product = (Product) request.getAttribute("product");
    
    // 3. Retrieve User from session for the "Add to Cart" check
    User loggedUser = (User) session.getAttribute("loggedUser");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= (product != null) ? product.getName() : "Product Detail" %></title>
    <link rel="stylesheet" href="<%= cp %>/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container product-detail" style="display: flex; gap: 40px; margin-top: 30px;">
    <% if (product != null) { %>
        
        <%-- Product Image --%>
        <div class="product-image">
            <img src="<%= product.getImageUrl() %>" alt="<%= product.getName() %>" 
                 style="max-width: 400px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
        </div>

        <%-- Product Info --%>
        <div class="info">
            <h1><%= product.getName() %></h1>
            <p class="category" style="color: #666; font-style: italic;">
                Category: <%= product.getCategory() %>
            </p>
            <p class="description" style="margin: 20px 0; line-height: 1.6;">
                <%= product.getDescription() %>
            </p>
            <h2 style="color: #28a745;"><%= cur.format(product.getPrice()) %></h2>
            <p><strong>Stock Available:</strong> <%= product.getStock() %></p>

            <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">

            <% if (loggedUser != null) { %>
                <%-- User is logged in: Show Add to Cart Form --%>
                <form action="<%= cp %>/cart" method="post">
                    <input type="hidden" name="productId" value="<%= product.getId() %>"/>
                    <input type="hidden" name="action" value="add"/>
                    
                    <div style="margin-bottom: 15px;">
                        <label>Quantity:</label>
                        <input type="number" name="quantity" value="1" min="1" 
                               max="<%= product.getStock() %>" 
                               style="padding: 8px; width: 60px;">
                    </div>
                    
                    <button type="submit" class="btn-success" 
                            style="padding: 12px 25px; font-size: 1.1em; cursor: pointer;">
                        Add to Cart 🛒
                    </button>
                </form>
            <% } else { %>
                <%-- User is NOT logged in: Show Login Link --%>
                <div style="padding: 15px; background: #fff3cd; border-radius: 5px;">
                    <a href="<%= cp %>/login" style="color: #856404; text-decoration: none; font-weight: bold;">
                        ⚠️ Login to add this product to your cart
                    </a>
                </div>
            <% } %>
        </div>

    <% } else { %>
        <div style="text-align: center; width: 100%;">
            <h2>Oops! Product not found.</h2>
            <p>The product you are looking for might have been removed.</p>
            <a href="<%= cp %>/products">Back to Shop</a>
        </div>
    <% } %>
</div>

</body>
</html>