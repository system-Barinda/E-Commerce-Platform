package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UserDAO {

    // ================= PASSWORD HASH =================
    private String hashPassword(String password) {
        if (password == null) return null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not found", e);
        }
    }

    // ================= REGISTER =================
    public boolean register(User user) throws SQLException {

        if (emailExists(user.getEmail())) return false;

        String sql = "INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashPassword(user.getPassword()));

            // ✅ FIX: use role from user object (not hardcoded)
            String role = user.getRole();

            // fallback safety
            if (role == null || role.isEmpty()) {
                role = "user";
            }

            ps.setString(4, role.toLowerCase());

            return ps.executeUpdate() > 0;
        }
    }

    // ================= LOGIN =================
    public User login(String email, String password) throws SQLException {

        String sql = "SELECT id, name, email, password, role, created_at FROM users WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String storedHash = rs.getString("password");
                    String inputHash  = hashPassword(password);

                    if (MessageDigest.isEqual(inputHash.getBytes(), storedHash.getBytes())) {
                        return mapUser(rs);
                    }
                }
            }
        }

        return null;
    }

    // ================= EMAIL CHECK =================
    public boolean emailExists(String email) throws SQLException {

        String sql = "SELECT 1 FROM users WHERE email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ================= USER BY ID =================
    public User getUserById(int id) throws SQLException {

        String sql = "SELECT id, name, email, role, created_at FROM users WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }

        return null;
    }

    // ================= UPDATE PROFILE =================
    public boolean updateProfile(User user) throws SQLException {

        String sql = "UPDATE users SET name=?, email=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ================= CHANGE PASSWORD =================
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {

        String sql = "SELECT password FROM users WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    String currentHash = rs.getString("password");
                    String oldHash     = hashPassword(oldPassword);

                    if (MessageDigest.isEqual(oldHash.getBytes(), currentHash.getBytes())) {

                        String updateSql = "UPDATE users SET password=? WHERE id=?";

                        try (PreparedStatement ups = con.prepareStatement(updateSql)) {
                            ups.setString(1, hashPassword(newPassword));
                            ups.setInt(2, userId);

                            return ups.executeUpdate() > 0;
                        }
                    }
                }
            }
        }

        return false;
    }

    // ================= MAPPER =================
    private User mapUser(ResultSet rs) throws SQLException {

        User u = new User();

        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getString("role"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts);

        return u;
    }
}