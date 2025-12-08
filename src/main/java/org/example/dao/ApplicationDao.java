package org.example.dao;

import org.example.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDao {
    public static class Application {
    private int id;
    private String applicationName;
    private String groupHeadName;
    private String groupHeadEmail;
    private String groupHeadContact;
    private String relationshipManagerName;
    private String relationshipManagerEmail;
    private String relationshipManagerContact;
    private int createdBy;

    public Application() {}

    public Application(String applicationName, String groupHeadName, String groupHeadEmail,
                       String groupHeadContact, String relationshipManagerName,
                       String relationshipManagerEmail, String relationshipManagerContact, int createdBy) {
        this.applicationName = applicationName;
        this.groupHeadName = groupHeadName;
        this.groupHeadEmail = groupHeadEmail;
        this.groupHeadContact = groupHeadContact;
        this.relationshipManagerName = relationshipManagerName;
        this.relationshipManagerEmail = relationshipManagerEmail;
        this.relationshipManagerContact = relationshipManagerContact;
        this.createdBy = createdBy;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getGroupHeadName() { return groupHeadName; }
    public void setGroupHeadName(String groupHeadName) { this.groupHeadName = groupHeadName; }

    public String getGroupHeadEmail() { return groupHeadEmail; }
    public void setGroupHeadEmail(String groupHeadEmail) { this.groupHeadEmail = groupHeadEmail; }

    public String getGroupHeadContact() { return groupHeadContact; }
    public void setGroupHeadContact(String groupHeadContact) { this.groupHeadContact = groupHeadContact; }

    public String getRelationshipManagerName() { return relationshipManagerName; }
    public void setRelationshipManagerName(String relationshipManagerName) { this.relationshipManagerName = relationshipManagerName; }

    public String getRelationshipManagerEmail() { return relationshipManagerEmail; }
    public void setRelationshipManagerEmail(String relationshipManagerEmail) { this.relationshipManagerEmail = relationshipManagerEmail; }

    public String getRelationshipManagerContact() { return relationshipManagerContact; }
    public void setRelationshipManagerContact(String relationshipManagerContact) { this.relationshipManagerContact = relationshipManagerContact; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}

// Add new application
public boolean addApplication(Application application) {
    String sql = "INSERT INTO applications(application_name, group_head_name, group_head_email, " +
            "group_head_contact, relationship_manager_name, relationship_manager_email, " +
            "relationship_manager_contact, created_by) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, application.getApplicationName());
        pstmt.setString(2, application.getGroupHeadName());
        pstmt.setString(3, application.getGroupHeadEmail());
        pstmt.setString(4, application.getGroupHeadContact());
        pstmt.setString(5, application.getRelationshipManagerName());
        pstmt.setString(6, application.getRelationshipManagerEmail());
        pstmt.setString(7, application.getRelationshipManagerContact());
        pstmt.setInt(8, application.getCreatedBy());

        int affectedRows = pstmt.executeUpdate();
        System.out.println("Application added successfully. ID: " + application.getApplicationName());
        return affectedRows > 0;

    } catch (SQLException e) {
        System.out.println("Error adding application: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Get all applications
public List<Application> getAllApplications() {
    String sql = "SELECT * FROM applications ORDER BY application_name";
    List<Application> applicationList = new ArrayList<>();

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Application application = new Application();
            application.setId(rs.getInt("id"));
            application.setApplicationName(rs.getString("application_name"));
            application.setGroupHeadName(rs.getString("group_head_name"));
            application.setGroupHeadEmail(rs.getString("group_head_email"));
            application.setGroupHeadContact(rs.getString("group_head_contact"));
            application.setRelationshipManagerName(rs.getString("relationship_manager_name"));
            application.setRelationshipManagerEmail(rs.getString("relationship_manager_email"));
            application.setRelationshipManagerContact(rs.getString("relationship_manager_contact"));
            application.setCreatedBy(rs.getInt("created_by"));
            applicationList.add(application);
        }
        System.out.println("Retrieved " + applicationList.size() + " applications.");
    } catch (SQLException e) {
        System.out.println("Error getting applications: " + e.getMessage());
        e.printStackTrace();
    }
    return applicationList;
}

// Delete application by ID
public boolean deleteApplication(int applicationId) {
    String sql = "DELETE FROM applications WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, applicationId);
        int affectedRows = pstmt.executeUpdate();
        System.out.println("Application deleted. ID: " + applicationId);
        return affectedRows > 0;

    } catch (SQLException e) {
        System.out.println("Error deleting application: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Check if application name exists
public boolean applicationNameExists(String applicationName) {
    String sql = "SELECT COUNT(*) FROM applications WHERE application_name = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, applicationName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.out.println("Error checking application name: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

// Get application by ID
public Application getApplicationById(int id) {
    String sql = "SELECT * FROM applications WHERE id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            Application application = new Application();
            application.setId(rs.getInt("id"));
            application.setApplicationName(rs.getString("application_name"));
            application.setGroupHeadName(rs.getString("group_head_name"));
            application.setGroupHeadEmail(rs.getString("group_head_email"));
            application.setGroupHeadContact(rs.getString("group_head_contact"));
            application.setRelationshipManagerName(rs.getString("relationship_manager_name"));
            application.setRelationshipManagerEmail(rs.getString("relationship_manager_email"));
            application.setRelationshipManagerContact(rs.getString("relationship_manager_contact"));
            application.setCreatedBy(rs.getInt("created_by"));
            return application;
        }
    } catch (SQLException e) {
        System.out.println("Error getting application: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}

// Get application by name
public Application getApplicationByName(String appName) {
    String sql = "SELECT * FROM applications WHERE application_name = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, appName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            Application application = new Application();
            application.setId(rs.getInt("id"));
            application.setApplicationName(rs.getString("application_name"));
            application.setGroupHeadName(rs.getString("group_head_name"));
            application.setGroupHeadEmail(rs.getString("group_head_email"));
            application.setGroupHeadContact(rs.getString("group_head_contact"));
            application.setRelationshipManagerName(rs.getString("relationship_manager_name"));
            application.setRelationshipManagerEmail(rs.getString("relationship_manager_email"));
            application.setRelationshipManagerContact(rs.getString("relationship_manager_contact"));
            application.setCreatedBy(rs.getInt("created_by"));
            return application;
        }
    } catch (SQLException e) {
        System.out.println("Error getting application by name: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
    }
}
