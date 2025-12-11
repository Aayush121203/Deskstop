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
    private UserDao userDAO;
    private static final Color BURGUNDY = new Color(151, 20, 77); // #97144D
    private static final Color DARK_BURGUNDY = new Color(120, 16, 61); // Darker burgundy for hover

    public LoginUI() {
        userDAO = new UserDao();
        initComponents();
    }

    private void initComponents() {
        setTitle("INCIDENT MANAGEMENT SYSTEM");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with white background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top panel with burgundy header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BURGUNDY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 70));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Left side: Axis Bank
        JLabel axisBankLabel = new JLabel("AXIS BANK");
        axisBankLabel.setFont(new Font("Arial", Font.BOLD, 24));
        axisBankLabel.setForeground(Color.WHITE);
        axisBankLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Right side: Incident Management System
        JLabel incidentSystemLabel = new JLabel("INCIDENT MANAGEMENT SYSTEM");
        incidentSystemLabel.setFont(new Font("Arial", Font.BOLD, 20));
        incidentSystemLabel.setForeground(Color.WHITE);
        incidentSystemLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(axisBankLabel, BorderLayout.WEST);
        topPanel.add(incidentSystemLabel, BorderLayout.EAST);

        // Notice panel
        JPanel noticePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        noticePanel.setBackground(new Color(255, 230, 240));
        noticePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        noticePanel.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel noticeLabel = new JLabel("Important: All system activities are logged and monitored for security purposes");
        noticeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        noticeLabel.setForeground(BURGUNDY);
        noticeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        noticePanel.add(noticeLabel);

        // Center panel for login form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 150, 30, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);

        // HIGH SECURED LOGIN title
        JLabel secureLoginLabel = new JLabel("HIGH SECURED LOGIN");
        secureLoginLabel.setFont(new Font("Arial", Font.BOLD, 28));
        secureLoginLabel.setForeground(BURGUNDY);
        secureLoginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(0, 0, 30, 0);
        centerPanel.add(secureLoginLabel, gbc);

        // Login form panel - VERY SMALL
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30) // Very little padding
        ));
        loginPanel.setPreferredSize(new Dimension(400, 220)); // VERY SMALL panel (400x220)

        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.fill = GridBagConstraints.HORIZONTAL;
        loginGbc.gridwidth = GridBagConstraints.REMAINDER;
        loginGbc.insets = new Insets(3, 0, 3, 0); // Very little spacing

        // Username field
        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Smaller font
        usernameLabel.setForeground(Color.BLACK); // Changed to black
        usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        loginGbc.insets = new Insets(0, 0, 2, 0);
        loginPanel.add(usernameLabel, loginGbc);

        usernameField = createVerySmallTextField();
        loginGbc.insets = new Insets(0, 0, 8, 0);
        loginPanel.add(usernameField, loginGbc);

        // Password field
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Smaller font
        passwordLabel.setForeground(Color.BLACK); // Changed to black
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        loginGbc.insets = new Insets(0, 0, 2, 0);
        loginPanel.add(passwordLabel, loginGbc);

        passwordField = createVerySmallPasswordField();
        loginGbc.insets = new Insets(0, 0, 12, 0);
        loginPanel.add(passwordField, loginGbc);

        // Login Button - VERY SMALL
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 13)); // Smaller font
        loginButton.setBackground(BURGUNDY);
        loginButton.setForeground(Color.BLACK); // Changed to black
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(120, 30)); // VERY SMALL button (120x30)
        loginButton.setBorder(BorderFactory.createEmptyBorder(4, 25, 4, 25)); // Very little padding

        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(DARK_BURGUNDY);
                loginButton.setForeground(Color.BLACK); // Keep black on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(BURGUNDY);
                loginButton.setForeground(Color.BLACK); // Keep black
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(100, 13, 51));
                loginButton.setForeground(Color.BLACK); // Keep black when pressed
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(DARK_BURGUNDY);
                loginButton.setForeground(Color.BLACK); // Keep black
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        loginGbc.insets = new Insets(6, 0, 0, 0);
        loginPanel.add(buttonPanel, loginGbc);

        // Add login panel to center
        gbc.insets = new Insets(10, 0, 0, 0);
        centerPanel.add(loginPanel, gbc);

        // Help links panel
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        helpPanel.setBackground(Color.WHITE);
        helpPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));

        JLabel forgotLabel = createSmallHelpLink("Forgot Credentials?");
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginUI.this,
                        "Contact IT Support: support@axisbank.com",
                        "Forgot Credentials",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JLabel supportLabel = createSmallHelpLink("Need Help?");
        supportLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginUI.this,
                        "IT Support Desk:\nPhone: 1800-419-5959\nHours: 24/7",
                        "Support",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        helpPanel.add(forgotLabel);
        helpPanel.add(supportLabel);

        centerPanel.add(helpPanel, gbc);

        // Footer panel
        JPanel footerPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        footerPanel.setPreferredSize(new Dimension(getWidth(), 50)); // Smaller footer

        String[] footerItems = {
                "System Version: 2.4.1",
                "Last Updated: November",
                "© Axis Bank Ltd."
        };

        for (String item : footerItems) {
            JLabel footerLabel = new JLabel(item);
            footerLabel.setFont(new Font("Arial", Font.PLAIN, 10)); // Smaller font
            footerLabel.setForeground(Color.BLACK); // Changed to black
            footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            footerPanel.add(footerLabel);
        }

        // Security info panel
        JPanel securityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        securityPanel.setBackground(Color.WHITE);
        securityPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 15, 0));

        JLabel securityLabel = new JLabel("🔒 Secure Access | All Activities Monitored");
        securityLabel.setFont(new Font("Arial", Font.PLAIN, 10)); // Smaller font
        securityLabel.setForeground(Color.BLACK); // Changed to black
        securityPanel.add(securityLabel);

        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(noticePanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(footerPanel, BorderLayout.NORTH);
        bottomContainer.add(securityPanel, BorderLayout.CENTER);
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);

        add(mainPanel);
        getRootPane().setDefaultButton(loginButton);
    }

    private JTextField createVerySmallTextField() {
        JTextField textField = new JTextField(20); // Fewer columns
        textField.setFont(new Font("Arial", Font.PLAIN, 13)); // Smaller font
        textField.setPreferredSize(new Dimension(300, 32)); // VERY SMALL (300x32)
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK); // Already black
        textField.setCaretColor(BURGUNDY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10) // Very little padding
        ));

        // Add focus effect
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BURGUNDY, 2),
                        BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });

        return textField;
    }

    private JPasswordField createVerySmallPasswordField() {
        JPasswordField passwordField = new JPasswordField(20); // Fewer columns
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13)); // Smaller font
        passwordField.setPreferredSize(new Dimension(300, 32)); // VERY SMALL (300x32)
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK); // Already black
        passwordField.setCaretColor(BURGUNDY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10) // Very little padding
        ));

        // Add focus effect
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.MouseEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BURGUNDY, 2),
                        BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }
            public void focusLost(java.awt.event.MouseEvent evt) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });

        return passwordField;
    }

    private JLabel createSmallHelpLink(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12)); // Smaller font
        label.setForeground(Color.BLACK); // Changed to black
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label.setForeground(BURGUNDY); // Change to burgundy on hover
                label.setFont(new Font("Arial", Font.BOLD, 12));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setForeground(Color.BLACK); // Back to black
                label.setFont(new Font("Arial", Font.PLAIN, 12));
            }
        });

        return label;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validation
        if (username.isEmpty()) {
            showBurgundyMessage("Please enter username", "Validation Error", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showBurgundyMessage("Please enter password", "Validation Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // Try actual database authentication
        User user = userDAO.authenticate(username, password);

        if (user != null) {
            showBurgundyMessage("Login Successful!\nWelcome, " + user.getUsername() + "!\nRole: " + user.getRole(),
                    "Authentication Successful", JOptionPane.INFORMATION_MESSAGE);

            // Open appropriate dashboard based on user role
            openDashboard(user);

        } else {
            // For demo purposes
            if ((username.equals("admin") && password.equals("admin123")) ||
                    (username.equals("incident") && password.equals("incident123")) ||
                    (username.equals("l3") && password.equals("l3123"))) {

                // Create a dummy user for demo
                User demoUser = new User();
                demoUser.setUsername(username);

                // Set role based on username
                if (username.equals("admin")) {
                    demoUser.setRole("ADMIN");
                } else if (username.equals("incident")) {
                    demoUser.setRole("INCIDENT_MANAGER");
                } else if (username.equals("l3")) {
                    demoUser.setRole("L3_SUPPORT");
                }

                showBurgundyMessage("Login Successful!\nWelcome to Axis Bank Incident Management System",
                        "Authentication Successful", JOptionPane.INFORMATION_MESSAGE);

                openDashboard(demoUser);

            } else {
                showBurgundyMessage("Invalid username or password\nPlease check your credentials and try again.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBurgundyMessage(String message, String title, int messageType) {
        // Customize JOptionPane
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageForeground", Color.BLACK); // Changed to black
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 12)); // Smaller
        UIManager.put("Button.background", BURGUNDY);
        UIManager.put("Button.foreground", Color.BLACK); // Changed to black
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 11)); // Smaller

        JOptionPane pane = new JOptionPane(
                "<html><body style='width: 250px; text-align: center; padding: 8px;'>" + // Smaller
                        message.replace("\n", "<br>") +
                        "</body></html>",
                messageType
        );

        JDialog dialog = pane.createDialog(this, title);

        // Style the buttons
        for (Component comp : pane.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(BURGUNDY);
                button.setForeground(Color.BLACK); // Changed to black
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setFont(new Font("Arial", Font.BOLD, 11)); // Smaller
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setPreferredSize(new Dimension(80, 25)); // Smaller buttons

                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        button.setBackground(DARK_BURGUNDY);
                        button.setForeground(Color.BLACK); // Keep black on hover
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        button.setBackground(BURGUNDY);
                        button.setForeground(Color.BLACK); // Keep black
                    }
                });
            }
        }

        dialog.setVisible(true);

        // Reset UI Manager
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("OptionPane.messageFont", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
        UIManager.put("Button.font", null);
    }

    private void openDashboard(User user) {
        dispose();

        String role = user.getRole();

        // Use your existing dashboard logic
        switch (role) {
            case "ADMIN":
                new AdminDashboard(user).setVisible(true);
                break;

            case "INCIDENT_MANAGER":
                new IncidentManagerDashboard(user).setVisible(true);
                break;

            case "L3_SUPPORT":
                new L3SupportDashboard(user).setVisible(true);
                break;

            default:
                // Show a simple dashboard
                JFrame dashboard = new JFrame("Axis Bank - Incident Management System");
                dashboard.setSize(1024, 768);
                dashboard.setLocationRelativeTo(null);
                dashboard.getContentPane().setBackground(Color.WHITE);

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

                JLabel welcomeLabel = new JLabel("Welcome to Axis Bank Incident Management System", SwingConstants.CENTER);
                welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
                welcomeLabel.setForeground(BURGUNDY);

                JLabel userLabel = new JLabel("User: " + user.getUsername() + " | Role: " + user.getRole(), SwingConstants.CENTER);
                userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                userLabel.setForeground(Color.BLACK); // Changed to black
                userLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setForeground(BURGUNDY);
                separator.setPreferredSize(new Dimension(400, 2));

                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
                centerPanel.setBackground(Color.WHITE);
                centerPanel.add(welcomeLabel);
                centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                centerPanel.add(separator);
                centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                centerPanel.add(userLabel);

                panel.add(centerPanel, BorderLayout.CENTER);
                dashboard.add(panel);
                dashboard.setVisible(true);
                break;
        }
    }

    public static void main(String[] args) {
        // Force database recreation with correct schema
        DatabaseConnection.recreateTables();

        // Then initialize database
        DatabaseConnection.initializeDatabase();

        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Customize UI properties
            UIManager.put("Button.select", BURGUNDY);
            UIManager.put("nimbusSelection", BURGUNDY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginUI loginUI = new LoginUI();
            loginUI.setVisible(true);
        });
    }
}

