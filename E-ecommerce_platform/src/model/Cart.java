package model;

public class Cart {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private Product product; // joined product details

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    // Helper: subtotal
    public double getSubtotal() {
        return product != null ? product.getPrice() * quantity : 0;
    }
}