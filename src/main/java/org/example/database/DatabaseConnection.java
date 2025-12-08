package org.example.database;

import org.example.model.User;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:D:/IncidentManagementApp/IncidentManagementApp/incident_management.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        System.out.println("Initializing database...");
        try (Connection conn = getConnection()) {
            createTablesIfNotExist(conn);
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist(Connection conn) throws SQLException {
        System.out.println("Creating tables if they don't exist...");

        // Users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                full_name TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('ADMIN', 'INCIDENT_MANAGER', 'L3_SUPPORT')),
                email TEXT,
                contact_number TEXT,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """;

        // Applications table - CHANGED: 'name' to 'application_name' to match ApplicationDao
        String createApplicationsTable = """
            CREATE TABLE IF NOT EXISTS applications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                application_name TEXT UNIQUE NOT NULL,
                group_head_name TEXT NOT NULL,
                group_head_email TEXT NOT NULL,
                group_head_contact TEXT NOT NULL,
                relationship_manager_name TEXT NOT NULL,
                relationship_manager_email TEXT NOT NULL,
                relationship_manager_contact TEXT NOT NULL,
                created_by INTEGER,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (created_by) REFERENCES users(id)
            )
        """;

        // Incidents table
        String createIncidentsTable = """
            CREATE TABLE IF NOT EXISTS incidents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                application_id INTEGER NOT NULL,
                application_name TEXT NOT NULL,
                issue_start_time TEXT NOT NULL,
                issue_end_time TEXT,
                problem_statement TEXT NOT NULL,
                business_impact TEXT NOT NULL,
                temporary_solution TEXT,
                permanent_solution TEXT,
                status TEXT DEFAULT 'OPEN',
                created_by INTEGER NOT NULL,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                assigned_to INTEGER,
                rca_provided BOOLEAN DEFAULT 0,
                FOREIGN KEY (application_id) REFERENCES applications(id),
                FOREIGN KEY (created_by) REFERENCES users(id),
                FOREIGN KEY (assigned_to) REFERENCES users(id)
            )
        """;

        // RCA table
        String createRCATable = """
            CREATE TABLE IF NOT EXISTS rca (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                incident_id INTEGER UNIQUE NOT NULL,
                root_cause TEXT NOT NULL,
                permanent_fix TEXT NOT NULL,
                preventive_measures TEXT,
                created_by INTEGER,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                published BOOLEAN DEFAULT FALSE,
                published_date TEXT,
                FOREIGN KEY (incident_id) REFERENCES incidents(id),
                FOREIGN KEY (created_by) REFERENCES users(id)
            )
        """;

        Statement stmt = conn.createStatement();

        // Execute table creation
        stmt.execute(createUsersTable);
        System.out.println("Users table checked/created.");

        stmt.execute(createApplicationsTable);
        System.out.println("Applications table checked/created (with application_name column).");

        stmt.execute(createIncidentsTable);
        System.out.println("Incidents table checked/created.");

        stmt.execute(createRCATable);
        System.out.println("RCA table checked/created.");

        // Insert default data
        insertDefaultData(stmt);
    }

    private static void insertDefaultData(Statement stmt) throws SQLException {
        System.out.println("Inserting default data...");

        // Insert default users
        String insertUsers = """
            INSERT OR IGNORE INTO users (username, password, full_name, role, email, contact_number) 
            VALUES 
            ('admin', 'admin123', 'System Administrator', 'ADMIN', 'admin@company.com', '9876543210'),
            ('im', 'im123', 'Incident Manager', 'INCIDENT_MANAGER', 'im@company.com', '9876543211'),
            ('l3', 'l3123', 'L3 Support Engineer', 'L3_SUPPORT', 'l3@company.com', '9876543212')
        """;

        try {
            int userCount = stmt.executeUpdate(insertUsers);
            if (userCount > 0) {
                System.out.println("Default users inserted: " + userCount);
            } else {
                System.out.println("Default users already exist.");
            }
        } catch (SQLException e) {
            System.out.println("Note inserting users: " + e.getMessage());
        }

        // Insert default applications - CHANGED: now uses application_name column
        String insertApplications = """
            INSERT OR IGNORE INTO applications (application_name, group_head_name, group_head_email, group_head_contact, 
                                         relationship_manager_name, relationship_manager_email, relationship_manager_contact) 
            VALUES 
            ('ERP System', 'John Smith', 'john.smith@company.com', '9876543201', 'Jane Doe', 'jane.doe@company.com', '9876543202'),
            ('CRM Platform', 'Robert Johnson', 'robert.j@company.com', '9876543203', 'Sarah Williams', 'sarah.w@company.com', '9876543204'),
            ('HRMS Portal', 'Michael Brown', 'michael.b@company.com', '9876543205', 'Emily Davis', 'emily.d@company.com', '9876543206'),
            ('E-Commerce Website', 'David Wilson', 'david.w@company.com', '9876543207', 'Lisa Miller', 'lisa.m@company.com', '9876543208')
        """;

        try {
            int appCount = stmt.executeUpdate(insertApplications);
            if (appCount > 0) {
                System.out.println("Default applications inserted: " + appCount);
            } else {
                System.out.println("Default applications already exist.");
            }
        } catch (SQLException e) {
            System.out.println("Note inserting applications: " + e.getMessage());
        }
    }

    public static void recreateTables() {
        System.out.println("=== RECREATING DATABASE TABLES ===");

        try {
            // First, delete the database file if it exists
            File dbFile = new File("incident_management.db");
            if (dbFile.exists()) {
                // Close any open connections first
                try {
                    DriverManager.getConnection(DB_URL).close();
                } catch (Exception e) {
                    // Ignore
                }

                // Wait a bit
                Thread.sleep(100);

                // Delete the file
                if (dbFile.delete()) {
                    System.out.println("Old database file deleted.");
                } else {
                    System.out.println("Could not delete database file. It might be in use.");
                    System.out.println("Trying to drop tables instead...");
                    dropAllTables();
                }
            } else {
                System.out.println("No existing database file found.");
            }

            // Wait for file system
            Thread.sleep(100);

            // Create fresh database
            initializeDatabase();

            System.out.println("Database recreated successfully!");

        } catch (Exception e) {
            System.out.println("Error recreating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void dropAllTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Disable foreign keys
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Drop tables in correct order
            String[] tables = {"rca", "incidents", "applications", "users"};
            for (String table : tables) {
                try {
                    stmt.execute("DROP TABLE IF EXISTS " + table);
                    System.out.println("Dropped table: " + table);
                } catch (SQLException e) {
                    // Table might not exist, that's OK
                }
            }

            // Re-enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {
            System.out.println("Error dropping tables: " + e.getMessage());
        }
    }

    public static void verifySchema() {
        System.out.println("\n=== VERIFYING DATABASE SCHEMA ===");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if applications table exists and has correct columns
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(applications)");

            System.out.println("Applications table columns:");
            boolean hasApplicationName = false;
            boolean tableExists = false;

            while (rs.next()) {
                tableExists = true;
                String columnName = rs.getString("name");
                System.out.println("  Column: " + columnName);

                if ("application_name".equalsIgnoreCase(columnName)) {
                    hasApplicationName = true;
                }
            }

            if (!tableExists) {
                System.out.println("ERROR: Applications table does not exist!");
            } else {
                System.out.println("\nApplications table exists.");
                System.out.println("Has 'application_name' column: " + (hasApplicationName ? "✓ YES" : "✗ NO"));

                if (hasApplicationName) {
                    System.out.println("✅ Applications table schema is CORRECT!");
                } else {
                    System.out.println("❌ Applications table schema is INCORRECT!");
                    System.out.println("Please run DatabaseConnection.recreateTables()");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error verifying schema: " + e.getMessage());
            System.out.println("Database might not exist. Run DatabaseConnection.recreateTables()");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== DATABASE SETUP TOOL ===");
        System.out.println("This will recreate the database with correct schema.\n");

        recreateTables();

        System.out.println("\n=== VERIFYING SCHEMA ===");
        verifySchema();

        System.out.println("\n=== SETUP COMPLETE ===");
        System.out.println("You can now run LoginUI.java");
    }
}
