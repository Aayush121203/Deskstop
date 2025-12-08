package org.example.ui;

// src/LoginFrame.java
import org.example.dao.UserDao;
import org.example.database.DatabaseConnection;
import org.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private UserDao userDAO;

    public LoginUI() {
        userDAO = new UserDao();
        initComponents();
    }

    private void initComponents() {
        setTitle("Incident Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(240, 248, 255);
                Color color2 = new Color(230, 240, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("INCIDENT MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel subtitleLabel = new JLabel("Secure Login Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(70, 70, 70));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(50, 50, 50));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(25);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(50, 50, 50));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Role dropdown
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(new Color(50, 50, 50));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(roleLabel, gbc);

        String[] roles = {"Select Role", "ADMIN", "INCIDENT_MANAGER", "L3_SUPPORT"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setPreferredSize(new Dimension(250, 40));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 220)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(roleComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonPanel.setOpaque(false);

        // Create buttons with black text and visible borders
        JButton loginButton = createLoginButton("Login", new Color(30, 144, 255));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        JButton clearButton = createLoginButton("Clear", new Color(119, 136, 153));
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel infoTitle = new JLabel("Test Credentials");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(new Color(25, 25, 112));
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel credentialsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        credentialsPanel.setOpaque(false);

        String[] testUsers = {
                "👑 admin / admin123 (ADMIN)",
                "📊 im / im123 (INCIDENT_MANAGER)",
                "🔧 l3 / l3123 (L3_SUPPORT)"
        };

        for (String user : testUsers) {
            JLabel credential = new JLabel(user);
            credential.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            credential.setForeground(new Color(70, 70, 70));
            credential.setHorizontalAlignment(SwingConstants.CENTER);
            credentialsPanel.add(credential);
        }

        infoPanel.add(infoTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(credentialsPanel);


        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create wrapper panel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(infoPanel, BorderLayout.SOUTH);

        add(wrapperPanel);
        getRootPane().setDefaultButton(loginButton);
    }

    private JButton createLoginButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // Changed to BLACK for clear visibility
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1), // Dark border
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Remove any content area filling issues
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        return button;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();

        // Validation
        if (username.isEmpty()) {
            showMessage("Please enter username", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showMessage("Please enter password", "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if ("Select Role".equals(selectedRole)) {
            showMessage("Please select a role", "Validation Error", JOptionPane.WARNING_MESSAGE);
            roleComboBox.requestFocus();
            return;
        }

        // Authenticate user
        User user = userDAO.authenticate(username, password);

        if (user != null) {
            // Check if role matches
            if (!user.getRole().equals(selectedRole)) {
                showMessage("Selected role doesn't match your account role.\n" +
                                "Your actual role is: " + user.getRole(),
                        "Role Mismatch", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Successful login
            showMessage("Login Successful!\nWelcome, " + user.getUsername() + "!\nRole: " + user.getRole(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Open appropriate dashboard
            openDashboard(user);

        } else {
            showMessage("Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void openDashboard(User user) {
        dispose();

        String role = user.getRole();

        switch (role) {
            case "ADMIN":
                new AdminDashboard(user).setVisible(true);
                break;

            case "INCIDENT_MANAGER":
                new IncidentManagerDashboard(user).setVisible(true);
                break;

            case "L3_SUPPORT":
                JOptionPane.showMessageDialog(null,
                        "L3 Support Dashboard - Coming Soon!",
                        "Access Granted",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginUI().setVisible(true);
                break;
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        usernameField.requestFocus();
    }
//
//    public static void main(String[] args) {
//        // Uncomment the line below if you need to recreate the database tables
//        // DatabaseConnection.recreateTables();
//
//        DatabaseConnection.initializeDatabase();
//
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        SwingUtilities.invokeLater(() -> {
//            LoginUI loginUI = new LoginUI();
//            loginUI.setVisible(true);
//        });
//    }

    public static void main(String[] args) {
        // Force database recreation with correct schema
        DatabaseConnection.recreateTables();

        // Then initialize database
        DatabaseConnection.initializeDatabase();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}

