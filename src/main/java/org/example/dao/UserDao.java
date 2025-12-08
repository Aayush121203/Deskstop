package org.example.dao;

import org.example.database.DatabaseConnection;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));
                System.out.println("User authenticated: " + username);
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Authentication failed for user: " + username);
        return null;
    }

    // Add new user
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(username, password, full_name, role, email, contact_number) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getContactNumber());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("User added: " + user.getUsername());
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get all users
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> userList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));
                userList.add(user);
            }
            System.out.println("Retrieved " + userList.size() + " users.");
        } catch (SQLException e) {
            System.out.println("Error getting users: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    // Get users by role
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));

                users.add(user);
            }
            System.out.println("Retrieved " + users.size() + " users with role: " + role);
        } catch (SQLException e) {
            System.out.println("Error getting users by role: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    // Delete user by ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("User deleted. ID: " + userId);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean validateUserRole(String username, String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, role);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Role validation error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Get user by ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
