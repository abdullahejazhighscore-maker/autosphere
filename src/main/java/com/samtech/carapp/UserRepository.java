package com.samtech.carapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public User authenticate(String username, String password, String role) {
        String sql = "SELECT id, username, role, password, email, phone FROM users WHERE username = ? AND role = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && PasswordUtil.matches(password, rs.getString("password"))) {
                return mapUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT id, username, role, email, phone FROM users ORDER BY id";
        List<User> users = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching users", e);
        }
    }

    public void deleteById(int userId) {
        String sql = "DELETE FROM users WHERE id = ? AND role != 'ADMIN'";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed deleting user", e);
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed checking username", e);
        }
    }

    public void createBuyer(String username, String password) {
        createUser(username, password, "BUYER", "", "");
    }

    public void createSeller(String username, String password, String email, String phone) {
        createUser(username, password, "SELLER", email, phone);
    }

    public User findById(int id) {
        String sql = "SELECT id, username, role, email, phone FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed fetching user by id", e);
        }
    }

    private void createUser(String username, String password, String role, String email, String phone) {
        String sql = "INSERT INTO users(username, password, role, email, phone) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(password));
            ps.setString(3, role);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed creating user", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("role"),
                nullToEmpty(rs.getString("email")),
                nullToEmpty(rs.getString("phone"))
        );
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
