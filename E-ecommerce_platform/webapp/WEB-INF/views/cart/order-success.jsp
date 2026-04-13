<%-- views/cart/order-success.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Order Placed!</title></head>
<body>
<div class="container" style="text-align:center; margin-top:80px;">
    <h1>🎉 Order Placed Successfully!</h1>
    <p>Your Order ID: <strong>${param.id}</strong></p>
    <p>Thank you for shopping with us!</p>
    <a href="${pageContext.request.contextPath}/products">Continue Shopping</a>
</div>
</body>
</html>