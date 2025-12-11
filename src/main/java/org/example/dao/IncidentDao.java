package org.example.dao;

import org.example.database.DatabaseConnection;
import org.example.model.Incident;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IncidentDao {

    public IncidentDao() {
        // Optional: You could check and create table here if needed
    }

    public boolean addIncident(Incident incident) {
        String sql = "INSERT INTO incidents (application_id, application_name, issue_start_time, " +
                "problem_statement, business_impact, temporary_solution, status, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incident.getApplicationId());
            pstmt.setString(2, incident.getApplicationName());
            pstmt.setString(3, incident.getIssueStartTime());
            pstmt.setString(4, incident.getProblemStatement());
            pstmt.setString(5, incident.getBusinessImpact());
            pstmt.setString(6, incident.getTemporarySolution());
            pstmt.setString(7, incident.getStatus());
            pstmt.setInt(8, incident.getCreatedBy());

            int result = pstmt.executeUpdate();
            System.out.println("Incident added successfully. Application: " + incident.getApplicationName());
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error adding incident: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();

            // Check if it's a table doesn't exist error
            if (e.getMessage().contains("no such table")) {
                System.out.println("Incidents table doesn't exist. Please run DatabaseConnection.recreateTables()");
            }
            return false;
        }
    }

    public List<Incident> getAllIncidents() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "ORDER BY i.issue_start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents.");
        } catch (SQLException e) {
            System.out.println("Error getting all incidents: " + e.getMessage());
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    public List<Incident> getIncidentsByStatus(String status) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.status = ? " +
                "ORDER BY i.issue_start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents with status: " + status);
        } catch (SQLException e) {
            System.out.println("Error getting incidents by status: " + e.getMessage());
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    private Incident extractIncidentFromResultSet(ResultSet rs) throws SQLException {
        Incident incident = new Incident();
        incident.setId(rs.getInt("id"));
        incident.setApplicationId(rs.getInt("application_id"));
        incident.setApplicationName(rs.getString("application_name"));
        incident.setIssueStartTime(rs.getString("issue_start_time"));
        incident.setIssueEndTime(rs.getString("issue_end_time"));
        incident.setProblemStatement(rs.getString("problem_statement"));
        incident.setBusinessImpact(rs.getString("business_impact"));
        incident.setTemporarySolution(rs.getString("temporary_solution"));
        incident.setPermanentSolution(rs.getString("permanent_solution"));
        incident.setStatus(rs.getString("status"));
        incident.setCreatedBy(rs.getInt("created_by"));
        incident.setCreatedByName(rs.getString("created_by_name"));

        // Handle nullable assigned_to field
        int assignedTo = rs.getInt("assigned_to");
        if (!rs.wasNull()) {
            incident.setAssignedTo(assignedTo);
        }
        incident.setAssignedToName(rs.getString("assigned_to_name"));
        incident.setRcaProvided(rs.getBoolean("rca_provided"));

        return incident;
    }

    public boolean updateIncidentStatus(int incidentId, String status, String endTime) {
        String sql = "UPDATE incidents SET status = ?, issue_end_time = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, endTime);
            pstmt.setInt(3, incidentId);

            int result = pstmt.executeUpdate();
            System.out.println("Updated incident status. ID: " + incidentId + ", Status: " + status);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error updating incident status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignIncidentToL3(int incidentId, int l3UserId) {
        String sql = "UPDATE incidents SET assigned_to = ?, status = 'ASSIGNED' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, l3UserId);
            pstmt.setInt(2, incidentId);

            int result = pstmt.executeUpdate();
            System.out.println("Assigned incident to L3. Incident ID: " + incidentId + ", L3 User ID: " + l3UserId);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error assigning incident to L3: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePermanentSolution(int incidentId, String permanentSolution) {
        String sql = "UPDATE incidents SET permanent_solution = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, permanentSolution);
            pstmt.setInt(2, incidentId);

            int result = pstmt.executeUpdate();
            System.out.println("Updated permanent solution for incident ID: " + incidentId);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Error updating permanent solution: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean markRcaProvided(int incidentId) {
        String sql = "UPDATE incidents SET rca_provided = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Incident getIncidentById(int incidentId) {
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, incidentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractIncidentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting incident by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public int getOpenIncidentsCount() {
        String sql = "SELECT COUNT(*) as count FROM incidents WHERE status IN ('OPEN', 'ASSIGNED')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error getting open incidents count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int getClosedIncidentsCount() {
        String sql = "SELECT COUNT(*) as count FROM incidents WHERE status = 'CLOSED'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error getting closed incidents count: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Incident> getIncidentsByCreatedBy(int userId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.created_by = ? " +
                "ORDER BY i.issue_start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents created by user ID: " + userId);
        } catch (SQLException e) {
            System.out.println("Error getting incidents by created_by: " + e.getMessage());
            System.out.println("SQL: " + sql);
            System.out.println("User ID: " + userId);
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    // NEW METHOD: Calculate Turnaround Time (TAT)
    public long calculateTAT(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);

            long diffInMillis = end.getTime() - start.getTime();
            return diffInMillis / (1000 * 60); // Return minutes
        } catch (Exception e) {
            System.out.println("Error calculating TAT: " + e.getMessage());
            return 0;
        }
    }

    // NEW METHOD: Get incidents assigned to specific L3 user
    public List<Incident> getIncidentsAssignedToUser(int userId) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.assigned_to = ? " +
                "ORDER BY i.issue_start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents assigned to user ID: " + userId);
        } catch (SQLException e) {
            System.out.println("Error getting incidents assigned to user: " + e.getMessage());
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    // NEW METHOD: Get incidents by assigned user and status
    public List<Incident> getIncidentsByAssignedUserAndStatus(int userId, String status) {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.assigned_to = ? AND i.status = ? " +
                "ORDER BY i.issue_start_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents with status " + status + " for user ID: " + userId);
        } catch (SQLException e) {
            System.out.println("Error getting incidents by assigned user and status: " + e.getMessage());
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    // NEW METHOD: Get average TAT for a specific user
    public long getAverageTATForUser(int userId) {
        String sql = "SELECT issue_start_time, issue_end_time FROM incidents " +
                "WHERE assigned_to = ? AND status = 'CLOSED' AND issue_end_time IS NOT NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            long totalTAT = 0;
            int count = 0;

            while (rs.next()) {
                String startTime = rs.getString("issue_start_time");
                String endTime = rs.getString("issue_end_time");

                if (startTime != null && endTime != null) {
                    long tat = calculateTAT(startTime, endTime);
                    totalTAT += tat;
                    count++;
                }
            }

            return count > 0 ? totalTAT / count : 0;
        } catch (SQLException e) {
            System.out.println("Error calculating average TAT for user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // NEW METHOD: Get incidents without RCA
    public List<Incident> getIncidentsWithoutRCA() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.status = 'CLOSED' AND i.rca_provided = 0 " +
                "ORDER BY i.issue_end_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents without RCA.");
        } catch (SQLException e) {
            System.out.println("Error getting incidents without RCA: " + e.getMessage());
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    // NEW METHOD: Get incidents with RCA provided
    public List<Incident> getIncidentsWithRCA() {
        List<Incident> incidents = new ArrayList<>();
        String sql = "SELECT i.*, u.username as created_by_name, u2.username as assigned_to_name " +
                "FROM incidents i " +
                "LEFT JOIN users u ON i.created_by = u.id " +
                "LEFT JOIN users u2 ON i.assigned_to = u2.id " +
                "WHERE i.rca_provided = 1 " +
                "ORDER BY i.issue_end_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Incident incident = extractIncidentFromResultSet(rs);
                incidents.add(incident);
            }
            System.out.println("Retrieved " + incidents.size() + " incidents with RCA.");
        } catch (SQLException e) {
            System.out.println("Error getting incidents with RCA: " + e.getMessage());
            e.printStackTrace();

            // Return empty list instead of null
            return new ArrayList<>();
        }

        return incidents;
    }

    public boolean saveOrUpdateRca(int incidentId,
                                   String rootCause,
                                   String permanentFix,
                                   String preventive,
                                   int createdBy) {
        String sql = """
        INSERT OR REPLACE INTO rca
          (incident_id, root_cause, permanent_fix, preventive_measures,
           created_by, created_at, published, published_date)
        VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 0, NULL)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            ps.setString(2, rootCause);
            ps.setString(3, permanentFix);
            ps.setString(4, preventive);
            ps.setInt(5, createdBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public RcaDetails getRcaForIncident(int incidentId) {
        String sql = "SELECT root_cause, permanent_fix, preventive_measures " +
                "FROM rca WHERE incident_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, incidentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RcaDetails r = new RcaDetails();
                    r.setRootCause(rs.getString("root_cause"));
                    r.setPermanentFix(rs.getString("permanent_fix"));
                    r.setPreventiveMeasures(rs.getString("preventive_measures"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}