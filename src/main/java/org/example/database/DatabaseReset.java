package org.example.database;

import java.io.File;

public class DatabaseReset {
    public static void main(String[] args) {
        System.out.println("=== COMPLETE DATABASE RESET ===");
        System.out.println("This will delete and recreate the database with correct schema.\n");

        // First, delete any existing database file
        File dbFile = new File("incident_management.db");
        File journalFile = new File("incident_management.db-journal");

        if (dbFile.exists()) {
            if (dbFile.delete()) {
                System.out.println("✓ Database file deleted");
            } else {
                System.out.println("✗ Could not delete database file");
            }
        }

        if (journalFile.exists()) {
            journalFile.delete();
        }

        // Wait for file operations
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Now recreate the database
        DatabaseConnection.recreateTables();

        System.out.println("\n=== VERIFYING SCHEMA ===");
        DatabaseConnection.verifySchema();

        System.out.println("\n=== RESET COMPLETE ===");
        System.out.println("Now run LoginUI.java to start the application.");
    }
}
