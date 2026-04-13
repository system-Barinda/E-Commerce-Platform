package dao;

import model.Product;
import model.User;
import model.Order;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class AdminDAO {

    // --- DASHBOARD STATS ---
    public int getTotalUsers() throws SQLException {
        return getCount("SELECT COUNT(*) FROM users WHERE role='user'");
    }

    public int getTotalProducts() throws SQLException {
        return getCount("SELECT COUNT(*) FROM products");
    }

    public int getTotalOrders() throws SQLException {
        return getCount("SELECT COUNT(*) FROM orders");
    }

    public double getTotalRevenue() throws SQLException {
        // Use COALESCE to handle cases where there are no orders (returns 0 instead of null)
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status != 'cancelled'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    private int getCount(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // --- PRODUCT MANAGEMENT ---
    public boolean addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, stock, image_url, category) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImageUrl());
            ps.setString(6, p.getCategory());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock=?, image_url=?, category=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImageUrl());
            ps.setString(6, p.getCategory());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        // Warning: This will fail if Foreign Key constraints are active and this product is in an order
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // --- ORDER MANAGEMENT ---
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.name as username, u.email " +
                     "FROM orders o JOIN users u ON o.user_id = u.id " +
                     "ORDER BY o.created_at DESC";
                     
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setUserId(rs.getInt("user_id"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                
                User u = new User();
                u.setName(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                
                // Note: Ensure your Order class has the setUser(User u) method
                o.setUser(u); 
                orders.add(o);
            }
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    // --- USER MANAGEMENT ---
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        // Added created_at to the selection since it is used in the ORDER BY clause
        String sql = "SELECT id, name, email, role, created_at FROM users ORDER BY created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                users.add(u);
            }
        }
        return users;
    }

    public boolean deleteUser(int id) throws SQLException {
        // Prevents accidental deletion of Admin accounts from the user management panel
        String sql = "DELETE FROM users WHERE id=? AND role='user'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}