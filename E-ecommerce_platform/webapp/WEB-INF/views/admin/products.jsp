<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.Product, java.text.NumberFormat" %>

<%
    List<Product> products = (List<Product>) request.getAttribute("products");
    NumberFormat cur = NumberFormat.getCurrencyInstance(java.util.Locale.US);

    String success = request.getParameter("success");
    String error   = request.getParameter("error");

    String cp = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Manage Products</title>

<link rel="stylesheet" href="<%= cp %>/css/admin.css">

<style>
.main-content {
    margin-left: 240px;
    padding: 20px;
    background: #f4f6f9;
    min-height: 100vh;
}

.alert { padding:10px; margin-bottom:15px; border-radius:5px; }
.success { background:#d4edda; color:#155724; }
.error { background:#f8d7da; color:#721c24; }

.form-card {
    background:#fff;
    padding:20px;
    border-radius:10px;
    margin-bottom:25px;
}

.form-card input, .form-card textarea {
    width:100%;
    padding:10px;
    margin-bottom:10px;
}

.admin-table {
    width:100%;
    background:white;
    border-collapse:collapse;
}

.admin-table th {
    background:#333;
    color:white;
    padding:10px;
}

.admin-table td {
    padding:10px;
}

.btn-edit { background:#007bff; color:white; border:none; padding:5px; }
.btn-danger { background:#dc3545; color:white; border:none; padding:5px; }
.btn-success { background:#28a745; color:white; border:none; padding:8px; }

.modal {
    display:none;
    position:fixed;
    top:0; left:0;
    width:100%; height:100%;
    background:rgba(0,0,0,0.5);
    justify-content:center;
    align-items:center;
}

.modal-content {
    background:white;
    padding:20px;
    width:400px;
}
</style>
</head>

<body>

<jsp:include page="/WEB-INF/views/admin/sidebar.jsp" />

<div class="main-content">

<h2>📦 Manage Products</h2>

<!-- ALERTS -->
<% if ("true".equals(success)) { %>
    <div class="alert success">✅ Operation successful!</div>
<% } %>

<% if (error != null) { %>
    <div class="alert error">❌ <%= error %></div>
<% } %>

<!-- ADD PRODUCT -->
<div class="form-card">
<h3>Add Product</h3>

<form action="<%= cp %>/admin/products" method="post" enctype="multipart/form-data">
<input type="hidden" name="action" value="add"/>

<input type="text" name="name" placeholder="Product Name" required/>
<textarea name="description" placeholder="Description"></textarea>

<input type="number" name="price" step="0.01" placeholder="Price" required/>
<input type="number" name="stock" placeholder="Stock" required/>

<!-- FILE UPLOAD -->
<input type="file" name="imageFile" accept="image/*"/>

<input type="text" name="category" placeholder="Category" required/>

<button class="btn-success">➕ Add</button>
</form>
</div>

<!-- TABLE -->
<table class="admin-table">
<thead>
<tr>
<th>ID</th><th>Image</th><th>Name</th><th>Category</th>
<th>Price</th><th>Stock</th><th>Actions</th>
</tr>
</thead>

<tbody>
<%
if (products != null && !products.isEmpty()) {
for (Product p : products) {

String safeName = p.getName() != null ? p.getName().replace("'", "\\'") : "";
String safeDesc = p.getDescription() != null ? p.getDescription().replace("'", "\\'") : "";
String safeCat  = p.getCategory() != null ? p.getCategory().replace("'", "\\'") : "";

/* FIX IMAGE PATH */
String img = p.getImageUrl();
if (img == null || img.isEmpty()) {
    img = cp + "/images/no-image.png";
} else {
    img = cp + "/images/" + img;
}
%>

<tr>
<td><%= p.getId() %></td>

<td>
<img src="<%= img %>" width="50" height="50"
     style="border-radius:6px; object-fit:cover;">
</td>

<td><%= safeName %></td>
<td><%= safeCat %></td>
<td><%= cur.format(p.getPrice()) %></td>
<td><%= p.getStock() %></td>

<td>
<button class="btn-edit"
onclick="openEditModal(<%= p.getId() %>, '<%= safeName %>', '<%= safeDesc %>', <%= p.getPrice() %>, <%= p.getStock() %>, '<%= safeCat %>')">
✏️
</button>

<form action="<%= cp %>/admin/products" method="post" style="display:inline;">
<input type="hidden" name="action" value="delete"/>
<input type="hidden" name="id" value="<%= p.getId() %>"/>
<button class="btn-danger" onclick="return confirm('Delete?')">🗑️</button>
</form>
</td>
</tr>

<% } } else { %>
<tr>
<td colspan="7">No products</td>
</tr>
<% } %>

</tbody>
</table>

</div>

<!-- EDIT MODAL -->
<div id="editModal" class="modal">
<div class="modal-content">

<h3>Edit Product</h3>

<form action="<%= cp %>/admin/products" method="post" enctype="multipart/form-data">
<input type="hidden" name="action" value="update"/>
<input type="hidden" id="edit-id" name="id"/>

<input type="text" id="edit-name" name="name" required/>
<textarea id="edit-desc" name="description"></textarea>

<input type="number" id="edit-price" name="price" step="0.01"/>
<input type="number" id="edit-stock" name="stock"/>

<!-- FILE UPLOAD -->
<input type="file" name="imageFile" accept="image/*"/>

<input type="text" id="edit-category" name="category"/>

<div>
<button class="btn-success">Update</button>
<button type="button" class="btn-danger" onclick="closeModal()">Cancel</button>
</div>

</form>
</div>
</div>

<script>
function openEditModal(id, name, desc, price, stock, category) {
document.getElementById('edit-id').value = id;
document.getElementById('edit-name').value = name || "";
document.getElementById('edit-desc').value = desc || "";
document.getElementById('edit-price').value = price || 0;
document.getElementById('edit-stock').value = stock || 0;
document.getElementById('edit-category').value = category || "";

document.getElementById('editModal').style.display = 'flex';
}

function closeModal() {
document.getElementById('editModal').style.display = 'none';
}
</script>

</body>
</html>