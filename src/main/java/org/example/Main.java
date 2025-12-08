package org.example;

import org.example.database.DatabaseConnection;
import org.example.ui.LoginUI;

import javax.swing.*;
import java.awt.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        DatabaseConnection.recreateTables();
        System.out.println("Incident Management System initialized!");
    }
}

