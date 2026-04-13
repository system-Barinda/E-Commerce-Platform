<%-- views/product/list.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Product" %>
<%@ page import="model.User" %>
<%@ page import="java.text.NumberFormat" %>

<%
    // 1. Setup helpers
    NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);
    String cp = request.getContextPath();

    // 2. Retrieve data from request (sent by ProductServlet)
    List<Product> products = (List<Product>) request.getAttribute("products");
    List<String> categories = (List<String>) request.getAttribute("categories");
    String keyword = (String) request.getAttribute("keyword");
    String selectedCategory = (String) request.getAttribute("selectedCategory");
    
    // 3. Retrieve User from session
    User loggedUser = (User) session.getAttribute("loggedUser");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Products - ShopZone</title>
    <link rel="stylesheet" href="<%= cp %>/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container">

    <%-- Search Bar --%>
    <div class="search-section" style="margin: 20px 0;">
        <form action="<%= cp %>/products" method="get">
            <input type="text" name="search" 
                   placeholder="Search products..." 
                   value="<%= (keyword != null) ? keyword : "" %>"
                   style="padding: 8px; width: 250px;">
            <button type="submit" style="padding: 8px 15px;">Search</button>
        </form>
    </div>

    <%-- Category Filter --%>
    <div class="categories" style="margin-bottom: 30px;">
        <a href="<%= cp %>/products" 
           class="cat-link <%= (selectedCategory == null) ? "active" : "" %>" 
           style="margin-right: 15px; text-decoration: none;">All</a>
        <%
            if (categories != null) {
                for (String cat : categories) {
                    boolean isActive = cat.equals(selectedCategory);
        %>
            <a href="<%= cp %>/products?category=<%= cat %>"
               class="cat-link <%= isActive ? "active" : "" %>"
               style="margin-right: 15px; text-decoration: none; <%= isActive ? "font-weight:bold; border-bottom: 2px solid #333;" : "" %>">
                <%= cat %>
            </a>
        <%
                }
            }
        %>
    </div>

    <%-- Product Grid --%>
    <div class="product-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px;">
        <%
            if (products != null && !products.isEmpty()) {
                for (Product p : products) {
        %>
            <div class="product-card" style="border: 1px solid #ddd; padding: 15px; border-radius: 8px; text-align: center;">
                <img src="<%= p.getImageUrl() %>" alt="<%= p.getName() %>" 
                     style="width: 100%; height: 200px; object-fit: cover; border-radius: 5px;"/>
                
                <h3 style="margin: 15px 0 5px 0;"><%= p.getName() %></h3>
                <p class="category" style="color: #666; font-size: 0.9em;"><%= p.getCategory() %></p>
                <p class="price" style="font-weight: bold; color: #28a745; font-size: 1.2em;">
                    <%= cur.format(p.getPrice()) %>
                </p>
                
                <p class="stock">
                    <% if (p.getStock() > 0) { %>
                        <span style="color: green;">In Stock (<%= p.getStock() %>)</span>
                    <% } else { %>
                        <span style="color: red;">Out of Stock</span>
                    <% } %>
                </p>

                <div style="margin-top: 15px; display: flex; flex-direction: column; gap: 10px;">
                    <a href="<%= cp %>/product?id=<%= p.getId() %>" 
                       style="text-decoration: none; color: #007bff; font-weight: bold;">
                        View Details
                    </a>

                    <% if (loggedUser != null && p.getStock() > 0) { %>
                        <form action="<%= cp %>/cart" method="post" style="margin: 0;">
                            <input type="hidden" name="productId" value="<%= p.getId() %>"/>
                            <input type="hidden" name="action" value="add"/>
                            <input type="hidden" name="quantity" value="1"/>
                            <button type="submit" style="background: #333; color: white; border: none; padding: 10px; border-radius: 4px; cursor: pointer; width: 100%;">
                                Add to Cart 🛒
                            </button>
                        </form>
                    <% } else if (loggedUser == null) { %>
                        <small><a href="<%= cp %>/login">Login to buy</a></small>
                    <% } %>
                </div>
            </div>
        <%
                }
            } else {
        %>
            <p style="grid-column: 1 / -1; text-align: center; padding: 50px;">No products found.</p>
        <%
            }
        %>
    </div>
</div>

</body>
</html>