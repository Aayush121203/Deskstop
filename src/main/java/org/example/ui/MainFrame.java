//package org.example.ui;
//
//// src/MainFrame.java
//import org.example.database.DatabaseConnection;
//import org.example.model.User;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class MainFrame extends JFrame {
//    private User user;
//    private DatabaseConnection db = new DatabaseConnection();
//
//    public MainFrame(User user) {
//        this.user = user;
//        initUI();
//
//    }
//
//    private void initUI() {
//        if (user.role.equals("ADMIN")) {
//            JMenu menuAdmin = new JMenu("3. Admin Panel");
//            JMenuItem itemAdminPanel = new JMenuItem("User & APP Management");
//            itemAdminPanel.addActionListener(e -> showAdminPanel());
//            menuAdmin.add(itemAdminPanel);
//            JMenuBar menuBar = new JMenuBar();
//            menuBar.add(menuAdmin);
//        }
//    }
//
//    private void showAppOnboarding() {
//        JTextField txtAppName = new JTextField(20);
//        JTextField txtGHName = new JTextField(20);
//        JTextField txtGHEmail = new JTextField(20);
//        JTextField txtGHContact = new JTextField(20);
//        JTextField txtRMName = new JTextField(20);
//        JTextField txtRMEmail = new JTextField(20);
//        JTextField txtRMContact = new JTextField(20);
//
//        Object[] fields = {
//                "Application Name:", txtAppName,
//                "Group Head Name:", txtGHName,
//                "Group Head Email:", txtGHEmail,
//                "Group Head Contact:", txtGHContact,
//                "RM Name:", txtRMName,
//                "RM Email:", txtRMEmail,
//                "RM Contact:", txtRMContact
//        };
//
//        int result = JOptionPane.showConfirmDialog(this, fields, "APP Onboarding", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {
//            db.addApp(txtAppName.getText(), txtGHName.getText(), txtGHEmail.getText(),
//                    txtGHContact.getText(), txtRMName.getText(), txtRMEmail.getText(), txtRMContact.getText());
//            JOptionPane.showMessageDialog(this, "App added successfully!");
//        }
//    }
//
//    private void showAdminPanel() {
//        JTabbedPane tabbedPane = new JTabbedPane();
//
//        // App Onboarding Tab (existing code)
//        JPanel appPanel = createAppOnboardingPanel();
//        tabbedPane.addTab("APP Onboarding", appPanel);
//
//        // User Management Tab (new)
//        JPanel userPanel = createUserManagementPanel();
//        tabbedPane.addTab("User Management", userPanel);
//
//        JOptionPane.showMessageDialog(this, tabbedPane, "Admin Panel", JOptionPane.PLAIN_MESSAGE);
//    }
//
//    private JPanel createAppOnboardingPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5,5,5,5);
//
//        // Copy your existing App Onboarding form components here...
//        // For brevity, reuse your previous showAppOnboarding() form code inside this panel
//
//        // Implement form in panel here similar to your current showAppOnboarding()
//
//        return panel;
//    }
//
//    private JPanel createUserManagementPanel() {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//
//        JTextField txtUsername = new JTextField(15);
//        JPasswordField txtPassword = new JPasswordField(15);
//        JComboBox<String> cmbRole = new JComboBox<>(new String[] {"ADMIN", "INCIDENT_MANAGER", "L3_SUPPORT"});
//
//        gbc.gridx = 0; gbc.gridy = 0;
//        panel.add(new JLabel("Username:"), gbc);
//        gbc.gridx = 1;
//        panel.add(txtUsername, gbc);
//        gbc.gridx = 0; gbc.gridy = 1;
//        panel.add(new JLabel("Password:"), gbc);
//        gbc.gridx = 1;
//        panel.add(txtPassword, gbc);
//        gbc.gridx = 0; gbc.gridy = 2;
//        panel.add(new JLabel("Role:"), gbc);
//        gbc.gridx = 1;
//        panel.add(cmbRole, gbc);
//
//        JButton btnAddUser = new JButton("Add User");
//        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
//        panel.add(btnAddUser, gbc);
//
//        JTextArea txtUsersList = new JTextArea(10, 30);
//        txtUsersList.setEditable(false);
//        loadUsers(txtUsersList);
//        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
//        panel.add(new JScrollPane(txtUsersList), gbc);
//
//        btnAddUser.addActionListener(e -> {
//            String username = txtUsername.getText().trim();
//            String password = new String(txtPassword.getPassword());
//            String role = (String) cmbRole.getSelectedItem();
//
//            if (username.isEmpty() || password.isEmpty()) {
//                JOptionPane.showMessageDialog(panel, "Please fill all fields!");
//                return;
//            }
//
//            db.addUser(username, password, role);
//            JOptionPane.showMessageDialog(panel, "User '" + username + "' added as " + role);
//            txtUsername.setText("");
//            txtPassword.setText("");
//            loadUsers(txtUsersList);
//        });
//
//        return panel;
//    }
//
//    private void loadUsers(JTextArea txtUsersList) {
//        try (Connection c = db.getConnection();
//             Statement stmt = c.createStatement();
//             ResultSet rs = stmt.executeQuery("SELECT username, role FROM users")) {
//            StringBuilder sb = new StringBuilder("Existing Users:\n\n");
//            while (rs.next()) {
//                sb.append(rs.getString("username")).append(" - ").append(rs.getString("role")).append("\n");
//            }
//            txtUsersList.setText(sb.toString());
//        } catch (SQLException e) {
//            txtUsersList.setText("Failed to load users.");
//        }
//    }
//
//}
//
