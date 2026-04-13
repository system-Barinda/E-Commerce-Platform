package dao;

import model.Cart;
import model.Product;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Add product to cart
    public void addToCart(int userId, int productId, int quantity) throws SQLException {
        // Check if item already exists in the user's cart
        String check = "SELECT id, quantity FROM cart WHERE user_id=? AND product_id=?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(check)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Update existing quantity
                    int cartId = rs.getInt("id");
                    int newQty = rs.getInt("quantity") + quantity;
                    
                    String update = "UPDATE cart SET quantity=? WHERE id=?";
                    try (PreparedStatement ups = con.prepareStatement(update)) {
                        ups.setInt(1, newQty);
                        ups.setInt(2, cartId);
                        ups.executeUpdate();
                    }
                } else {
                    // Insert new cart item
                    String insert = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?,?,?)";
                    try (PreparedStatement ins = con.prepareStatement(insert)) {
                        ins.setInt(1, userId);
                        ins.setInt(2, productId);
                        ins.setInt(3, quantity);
                        ins.executeUpdate();
                    }
                }
            }
        }
    }

    // Get all cart items for user
    public List<Cart> getCartItems(int userId) throws SQLException {
        List<Cart> list = new ArrayList<>();
        // Using standard concatenation for maximum compatibility
        String sql = "SELECT c.*, p.name, p.price, p.image_url, p.stock " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "WHERE c.user_id = ?";
                     
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cart cart = new Cart();
                    cart.setId(rs.getInt("id"));
                    cart.setUserId(userId);
                    cart.setProductId(rs.getInt("product_id"));
                    cart.setQuantity(rs.getInt("quantity"));

                    Product p = new Product();
                    p.setId(rs.getInt("product_id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getDouble("price"));
                    p.setImageUrl(rs.getString("image_url"));
                    p.setStock(rs.getInt("stock"));
                    
                    // Link product to cart item
                    cart.setProduct(p);
                    list.add(cart);
                }
            }
        }
        return list;
    }

    // Update quantity (e.g., from the shopping cart UI)
    public void updateQuantity(int cartId, int quantity) throws SQLException {
        // Validation to ensure quantity doesn't drop below 1
        if (quantity < 1) return; 
        
        String sql = "UPDATE cart SET quantity=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartId);
            ps.executeUpdate();
        }
    }

    // Remove single item
    public void removeItem(int cartId) throws SQLException {
        String sql = "DELETE FROM cart WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    // Clear cart after a successful checkout
    public void clearCart(int userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // Get total cost of all items in the cart
    public double getCartTotal(int userId) throws SQLException {
        // Coalesce handles empty carts so they return 0.0 instead of null
        String sql = "SELECT COALESCE(SUM(c.quantity * p.price), 0) as total " +
                     "FROM cart c JOIN products p ON c.product_id = p.id " +
                     "WHERE c.user_id = ?";
                     
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
            }
        }
        return 0.0;
    }
}