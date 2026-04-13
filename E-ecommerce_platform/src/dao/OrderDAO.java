package dao;

import model.*;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class OrderDAO {

    // Place order from cart
    public int placeOrder(int userId, List<Cart> cartItems, double total) throws SQLException {
        Connection con = null;
        PreparedStatement orderPs = null;
        PreparedStatement itemPs = null;
        PreparedStatement stockPs = null;
        ResultSet keys = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Transaction start

            // 1. Insert order
            String orderSql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, 'pending')";
            orderPs = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, userId);
            orderPs.setDouble(2, total);
            orderPs.executeUpdate();

            // Get the generated ID
            keys = orderPs.getGeneratedKeys();
            int orderId = -1;
            if (keys.next()) {
                orderId = keys.getInt(1);
            } else {
                throw new SQLException("Failed to obtain order ID.");
            }

            // 2. Prepare statements outside the loop to prevent resource leaks
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            String stockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";
            
            itemPs = con.prepareStatement(itemSql);
            stockPs = con.prepareStatement(stockSql);

            // 3. Batch processing for performance
            for (Cart item : cartItems) {
                // Set Order Item details
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, item.getProductId());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getProduct().getPrice());
                itemPs.addBatch();

                // Set Stock update details
                stockPs.setInt(1, item.getQuantity());
                stockPs.setInt(2, item.getProductId());
                stockPs.addBatch();
            }

            itemPs.executeBatch();
            stockPs.executeBatch();

            con.commit(); // Transaction commit
            return orderId;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Close resources in reverse order of creation
            if (keys != null) try { keys.close(); } catch (SQLException e) {}
            if (orderPs != null) try { orderPs.close(); } catch (SQLException e) {}
            if (itemPs != null) try { itemPs.close(); } catch (SQLException e) {}
            if (stockPs != null) try { stockPs.close(); } catch (SQLException e) {}
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {}
            }
        }
    }

    // Get orders by user
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        // Explicitly list columns for better performance/clarity
        String sql = "SELECT id, user_id, total_amount, status, created_at FROM orders WHERE user_id=? ORDER BY created_at DESC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getInt("id"));
                    o.setUserId(rs.getInt("user_id"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setStatus(rs.getString("status"));
                    o.setCreatedAt(rs.getTimestamp("created_at"));
                    orders.add(o);
                }
            }
        }
        return orders;
    }
}