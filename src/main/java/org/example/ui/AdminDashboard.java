package org.example.ui;

import org.example.dao.ApplicationDao;
import org.example.dao.IncidentDao;
import org.example.dao.UserDao;
import org.example.model.Incident;
import org.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboard extends JFrame {

    private UserDao userDao;
    private ApplicationDao applicationDao;
    private IncidentDao incidentDao;
    private JTable userTable;
    private JTable applicationTable;
    private JTable incidentsTable;
    private DefaultTableModel userTableModel;
    private DefaultTableModel applicationTableModel;
    private DefaultTableModel incidentsTableModel;
    private User currentAdmin;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AdminDashboard(User adminUser) {
        userDao = new UserDao();
        applicationDao = new ApplicationDao();
        incidentDao = new IncidentDao();
        currentAdmin = adminUser;
        initComponents();
    }

    private void initComponents() {
        setTitle("Admin Dashboard - Incident Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(248, 248, 255);
                Color color2 = new Color(240, 248, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(1300, 100));

        // Left side - Welcome message
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("👑 ADMINISTRATOR DASHBOARD");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Welcome, " + currentAdmin.getUsername() + " (" + currentAdmin.getFullName() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(200, 200, 255));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(userLabel);

        // Right side - User info and logout
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        infoPanel.setOpaque(false);

        JLabel emailLabel = new JLabel("📧 " + currentAdmin.getEmail());
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(Color.WHITE);

        JLabel contactLabel = new JLabel("📞 " + currentAdmin.getContactNumber());
        contactLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contactLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI().setVisible(true);
        });

        infoPanel.add(emailLabel);
        infoPanel.add(contactLabel);
        infoPanel.add(logoutButton);

        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.EAST);

        // Navigation panel - CORRECTED: 6 buttons in original order
        JPanel navPanel = new JPanel(new BorderLayout(0, 0));
        navPanel.setBackground(new Color(30, 30, 70));
        navPanel.setPreferredSize(new Dimension(220, 0));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navTitle.setForeground(Color.WHITE);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        navTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // CORRECTED: Back to 6 buttons with proper order
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 0, 15));
        buttonPanel.setBackground(new Color(30, 30, 70));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Navigation buttons in original order
        JButton userManagementBtn = createNavButton("👥 User Management", new Color(30, 144, 255));
        userManagementBtn.addActionListener(e -> showUserManagement());

        JButton applicationManagementBtn = createNavButton("💻 Application Management", new Color(60, 179, 113));
        applicationManagementBtn.addActionListener(e -> showApplicationManagement());

        JButton dashboardBtn = createNavButton("🏠 Dashboard", new Color(70, 130, 180));
        dashboardBtn.addActionListener(e -> showDashboard());

        // CORRECTED: Changed from disabled buttons to functional ones
        JButton viewIncidentsBtn = createNavButton("📊 View Incidents", new Color(138, 43, 226));
        viewIncidentsBtn.addActionListener(e -> showAllIncidents());

        JButton reportsBtn = createNavButton("📈 Reports", new Color(46, 139, 87));
        reportsBtn.addActionListener(e -> showReports());

        // CORRECTED: Added back Add Application button
        JButton addApplicationBtn = createNavButton("➕ Add Application", new Color(255, 140, 0));
        addApplicationBtn.addActionListener(e -> openAddApplicationForm());

        buttonPanel.add(userManagementBtn);
        buttonPanel.add(applicationManagementBtn);
        buttonPanel.add(dashboardBtn);
        buttonPanel.add(viewIncidentsBtn);
        buttonPanel.add(reportsBtn);
        buttonPanel.add(addApplicationBtn);

        navPanel.add(navTitle, BorderLayout.NORTH);
        navPanel.add(buttonPanel, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Create all cards
        JPanel dashboardCard = createDashboardCard();
        JPanel userManagementCard = createUserManagementCard();
        JPanel applicationManagementCard = createApplicationManagementCard();
        JPanel allIncidentsCard = createAllIncidentsCard();
        JPanel reportsCard = createReportsCard();

        contentPanel.add(dashboardCard, "DASHBOARD");
        contentPanel.add(userManagementCard, "USER_MANAGEMENT");
        contentPanel.add(applicationManagementCard, "APPLICATION_MANAGEMENT");
        contentPanel.add(allIncidentsCard, "ALL_INCIDENTS");
        contentPanel.add(reportsCard, "REPORTS");

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        showDashboard();
    }

    private JPanel createDashboardCard() {
        JPanel dashboardCard = new JPanel(new BorderLayout(20, 20));
        dashboardCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardCard.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 ADMIN DASHBOARD OVERVIEW");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        List<User> users = userDao.getAllUsers();
        java.util.List<ApplicationDao.Application> applications = applicationDao.getAllApplications();
        List<Incident> allIncidents = incidentDao.getAllIncidents();
        List<Incident> openIncidents = incidentDao.getIncidentsByStatus("OPEN");
        List<Incident> assignedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");

        int totalUsers = users.size();
        int incidentManagers = (int) users.stream().filter(u -> u.getRole().equals("INCIDENT_MANAGER")).count();
        int supportStaff = (int) users.stream().filter(u -> u.getRole().equals("L3_SUPPORT")).count();
        int activeUsers = totalUsers;
        int totalApplications = applications.size();
        int totalIncidents = allIncidents.size();

        statsPanel.add(createStatCard("👥 Total Users", String.valueOf(totalUsers), new Color(30, 144, 255)));
        statsPanel.add(createStatCard("👔 Incident Managers", String.valueOf(incidentManagers), new Color(60, 179, 113)));
        statsPanel.add(createStatCard("🛠️ Support Staff", String.valueOf(supportStaff), new Color(255, 140, 0)));
        statsPanel.add(createStatCard("✅ Active Users", String.valueOf(activeUsers), new Color(138, 43, 226)));
        statsPanel.add(createStatCard("💻 Total Applications", String.valueOf(totalApplications), new Color(46, 139, 87)));
        statsPanel.add(createStatCard("📋 Total Incidents", String.valueOf(totalIncidents), new Color(70, 130, 180)));

        // Quick actions panel - CORRECTED: Added Add User and Add Application buttons
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton quickAddUser = createQuickActionButton("➕ Add New User", new Color(60, 179, 113));
        quickAddUser.addActionListener(e -> openAddUserForm());

        JButton quickAddApp = createQuickActionButton("➕ Add Application", new Color(255, 140, 0));
        quickAddApp.addActionListener(e -> openAddApplicationForm());

        JButton quickViewUsers = createQuickActionButton("👁️ View All Users", new Color(30, 144, 255));
        quickViewUsers.addActionListener(e -> showUserManagement());

        JButton quickViewApps = createQuickActionButton("👁️ View Applications", new Color(138, 43, 226));
        quickViewApps.addActionListener(e -> showApplicationManagement());

        JButton quickViewIncidents = createQuickActionButton("📊 View Incidents", new Color(46, 139, 87));
        quickViewIncidents.addActionListener(e -> showAllIncidents());

        JButton quickGenerateReport = createQuickActionButton("📈 Generate Report", new Color(70, 130, 180));
        quickGenerateReport.addActionListener(e -> generateQuickReport());

        quickActionsPanel.add(quickAddUser);
        quickActionsPanel.add(quickAddApp);
        quickActionsPanel.add(quickViewUsers);
        quickActionsPanel.add(quickViewApps);
        quickActionsPanel.add(quickViewIncidents);
        quickActionsPanel.add(quickGenerateReport);

        dashboardCard.add(titleLabel, BorderLayout.NORTH);
        dashboardCard.add(statsPanel, BorderLayout.CENTER);
        dashboardCard.add(quickActionsPanel, BorderLayout.SOUTH);

        return dashboardCard;
    }

    private JPanel createUserManagementCard() {
        JPanel userManagementCard = new JPanel(new BorderLayout(20, 20));
        userManagementCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        userManagementCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("👥 USER MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));

        JButton refreshButton = createActionButton("🔄 Refresh", new Color(30, 144, 255));
        refreshButton.addActionListener(e -> refreshUserTable());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton addUserButton = createActionButton("➕ Add User", new Color(60, 179, 113));
        addUserButton.addActionListener(e -> openAddUserForm());

        JButton deleteUserButton = createActionButton("🗑️ Delete User", new Color(220, 20, 60));
        deleteUserButton.addActionListener(e -> deleteSelectedUser());

        actionPanel.add(addUserButton);
        actionPanel.add(deleteUserButton);

        // User table
        String[] columns = {"ID", "Username", "Full Name", "Role", "Email", "Contact"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(240, 248, 255));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableScrollPane.setPreferredSize(new Dimension(800, 400));

        userManagementCard.add(titlePanel, BorderLayout.NORTH);
        userManagementCard.add(actionPanel, BorderLayout.CENTER);
        userManagementCard.add(tableScrollPane, BorderLayout.SOUTH);

        refreshUserTable();

        return userManagementCard;
    }

    private JPanel createApplicationManagementCard() {
        JPanel applicationManagementCard = new JPanel(new BorderLayout(20, 20));
        applicationManagementCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        applicationManagementCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("💻 APPLICATION MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));

        JButton refreshButton = createActionButton("🔄 Refresh", new Color(30, 144, 255));
        refreshButton.addActionListener(e -> refreshApplicationTable());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton addAppButton = createActionButton("➕ Add Application", new Color(60, 179, 113));
        addAppButton.addActionListener(e -> openAddApplicationForm());

        JButton deleteAppButton = createActionButton("🗑️ Delete Application", new Color(220, 20, 60));
        deleteAppButton.addActionListener(e -> deleteSelectedApplication());

        actionPanel.add(addAppButton);
        actionPanel.add(deleteAppButton);

        // Application table
        String[] columns = {"ID", "Application Name", "Group Head", "Group Head Email",
                "Group Head Contact", "Relationship Manager", "Relationship Manager Email",
                "Relationship Manager Contact"};
        applicationTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        applicationTable = new JTable(applicationTableModel);
        applicationTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        applicationTable.setRowHeight(25);
        applicationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        applicationTable.getTableHeader().setBackground(new Color(240, 248, 255));
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(applicationTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableScrollPane.setPreferredSize(new Dimension(800, 400));

        applicationManagementCard.add(titlePanel, BorderLayout.NORTH);
        applicationManagementCard.add(actionPanel, BorderLayout.CENTER);
        applicationManagementCard.add(tableScrollPane, BorderLayout.SOUTH);

        refreshApplicationTable();

        return applicationManagementCard;
    }

    private JPanel createAllIncidentsCard() {
        JPanel allIncidentsCard = new JPanel(new BorderLayout(20, 20));
        allIncidentsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        allIncidentsCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 ALL INCIDENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));

        JButton refreshButton = createActionButton("🔄 Refresh", new Color(30, 144, 255));
        refreshButton.addActionListener(e -> refreshIncidentsTable());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String[] statuses = {"ALL", "OPEN", "ASSIGNED", "CLOSED"};
        JComboBox<String> statusFilter = new JComboBox<>(statuses);
        statusFilter.addActionListener(e -> filterIncidentsByStatus((String) statusFilter.getSelectedItem()));

        JLabel appFilterLabel = new JLabel("Filter by Application:");
        appFilterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();
        String[] appNames = new String[]{"ALL"};
        if (applications != null && !applications.isEmpty()) {
            appNames = new String[applications.size() + 1];
            appNames[0] = "ALL";
            for (int i = 0; i < applications.size(); i++) {
                appNames[i + 1] = applications.get(i).getApplicationName();
            }
        }
        JComboBox<String> appFilter = new JComboBox<>(appNames);
        appFilter.addActionListener(e -> filterIncidentsByApplication((String) appFilter.getSelectedItem()));

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(appFilterLabel);
        filterPanel.add(appFilter);

        // Incidents table
        String[] columns = {"ID", "Application", "Start Time", "Status", "Created By", "Assigned To", "RCA Provided"};
        incidentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        incidentsTable = new JTable(incidentsTableModel);
        incidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        incidentsTable.setRowHeight(30);
        incidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        incidentsTable.getTableHeader().setBackground(new Color(240, 248, 255));
        incidentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color code status column
        incidentsTable.setDefaultRenderer(Object.class, new StatusTableCellRenderer());

        JScrollPane tableScrollPane = new JScrollPane(incidentsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableScrollPane.setPreferredSize(new Dimension(900, 400));

        allIncidentsCard.add(titlePanel, BorderLayout.NORTH);
        allIncidentsCard.add(filterPanel, BorderLayout.CENTER);
        allIncidentsCard.add(tableScrollPane, BorderLayout.SOUTH);

        refreshIncidentsTable();

        return allIncidentsCard;
    }

    private JPanel createReportsCard() {
        JPanel reportsCard = new JPanel(new BorderLayout(20, 20));
        reportsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        reportsCard.setOpaque(false);

        JLabel titleLabel = new JLabel("📈 REPORTS & ANALYTICS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel reportsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        reportsPanel.setOpaque(false);
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton incidentReportBtn = createQuickActionButton("📋 Incident Summary Report", new Color(30, 144, 255));
        incidentReportBtn.addActionListener(e -> generateIncidentSummaryReport());

        JButton applicationReportBtn = createQuickActionButton("💻 Application-wise Report", new Color(60, 179, 113));
        applicationReportBtn.addActionListener(e -> generateApplicationWiseReport());

        JButton userReportBtn = createQuickActionButton("👥 User Activity Report", new Color(138, 43, 226));
        userReportBtn.addActionListener(e -> generateUserActivityReport());

        JButton rcaReportBtn = createQuickActionButton("🔍 RCA Analysis Report", new Color(46, 139, 87));
        rcaReportBtn.addActionListener(e -> generateRcaAnalysisReport());

        JButton monthlyReportBtn = createQuickActionButton("📅 Monthly Performance Report", new Color(255, 140, 0));
        monthlyReportBtn.addActionListener(e -> generateMonthlyReport());

        JButton exportReportBtn = createQuickActionButton("💾 Export All Reports", new Color(70, 130, 180));
        exportReportBtn.addActionListener(e -> exportAllReports());

        reportsPanel.add(incidentReportBtn);
        reportsPanel.add(applicationReportBtn);
        reportsPanel.add(userReportBtn);
        reportsPanel.add(rcaReportBtn);
        reportsPanel.add(monthlyReportBtn);
        reportsPanel.add(exportReportBtn);

        reportsCard.add(titleLabel, BorderLayout.NORTH);
        reportsCard.add(reportsPanel, BorderLayout.CENTER);

        return reportsCard;
    }
    private void showDashboard() {
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private void showUserManagement() {
        cardLayout.show(contentPanel, "USER_MANAGEMENT");
    }

    private void showApplicationManagement() {
        cardLayout.show(contentPanel, "APPLICATION_MANAGEMENT");
    }

    private void showAllIncidents() {
        cardLayout.show(contentPanel, "ALL_INCIDENTS");
    }

    private void showReports() {
        cardLayout.show(contentPanel, "REPORTS");
    }

    // UI Component Creation Methods (keep these as they were)
    private JButton createNavButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        return button;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        return button;
    }

    private JButton createQuickActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        return button;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel statCard = new JPanel(new BorderLayout(10, 10));
        statCard.setBackground(new Color(255, 255, 255, 230));
        statCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statCard.add(titleLabel, BorderLayout.NORTH);
        statCard.add(valueLabel, BorderLayout.CENTER);

        return statCard;
    }

    // Data Refresh Methods
    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        List<User> users = userDao.getAllUsers();

        for (User user : users) {
            Object[] row = {
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole(),
                    user.getEmail(),
                    user.getContactNumber()
            };
            userTableModel.addRow(row);
        }

        JOptionPane.showMessageDialog(this,
                "User list refreshed! Total users: " + users.size(),
                "Refresh Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshApplicationTable() {
        applicationTableModel.setRowCount(0);
        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();

        for (ApplicationDao.Application app : applications) {
            Object[] row = {
                    app.getId(),
                    app.getApplicationName(),
                    app.getGroupHeadName(),
                    app.getGroupHeadEmail(),
                    app.getGroupHeadContact(),
                    app.getRelationshipManagerName(),
                    app.getRelationshipManagerEmail(),
                    app.getRelationshipManagerContact()
            };
            applicationTableModel.addRow(row);
        }
    }

    private void refreshIncidentsTable() {
        incidentsTableModel.setRowCount(0);
        List<Incident> incidents = incidentDao.getAllIncidents();

        for (Incident incident : incidents) {
            Object[] row = {
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    incident.getStatus(),
                    incident.getCreatedByName() != null ? incident.getCreatedByName() : "Unknown",
                    incident.getAssignedToName() != null ? incident.getAssignedToName() : "Not Assigned",
                    incident.isRcaProvided() ? "Yes" : "No"
            };
            incidentsTableModel.addRow(row);
        }
    }

    private void filterIncidentsByStatus(String status) {
        incidentsTableModel.setRowCount(0);
        List<Incident> incidents;

        if (status.equals("ALL")) {
            incidents = incidentDao.getAllIncidents();
        } else {
            incidents = incidentDao.getIncidentsByStatus(status);
        }

        for (Incident incident : incidents) {
            Object[] row = {
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    incident.getStatus(),
                    incident.getCreatedByName() != null ? incident.getCreatedByName() : "Unknown",
                    incident.getAssignedToName() != null ? incident.getAssignedToName() : "Not Assigned",
                    incident.isRcaProvided() ? "Yes" : "No"
            };
            incidentsTableModel.addRow(row);
        }
    }

    private void filterIncidentsByApplication(String appName) {
        incidentsTableModel.setRowCount(0);
        List<Incident> incidents = incidentDao.getAllIncidents();

        for (Incident incident : incidents) {
            if (appName.equals("ALL") || incident.getApplicationName().equals(appName)) {
                Object[] row = {
                        incident.getId(),
                        incident.getApplicationName(),
                        incident.getIssueStartTime(),
                        incident.getStatus(),
                        incident.getCreatedByName() != null ? incident.getCreatedByName() : "Unknown",
                        incident.getAssignedToName() != null ? incident.getAssignedToName() : "Not Assigned",
                        incident.isRcaProvided() ? "Yes" : "No"
                };
                incidentsTableModel.addRow(row);
            }
        }
    }

    // Add User Form Methods (keep these as they were)
    private void openAddUserForm() {
        JDialog addUserDialog = new JDialog(this, "Add New User", true);
        addUserDialog.setSize(500, 550);
        addUserDialog.setLocationRelativeTo(this);
        addUserDialog.setLayout(new BorderLayout(10, 10));
        addUserDialog.getContentPane().setBackground(Color.WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("➕ ADD NEW USER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Full Name
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(fullNameField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(confirmPasswordField, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        String[] roles = {"INCIDENT_MANAGER", "L3_SUPPORT"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(roleComboBox, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(emailField, gbc);

        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(contactLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField contactField = new JTextField(20);
        contactField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(contactField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = createActionButton("💾 Save User", new Color(60, 179, 113));
        saveButton.addActionListener(e -> {
            saveNewUser(fullNameField.getText().trim(),
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword()),
                    new String(confirmPasswordField.getPassword()),
                    (String) roleComboBox.getSelectedItem(),
                    emailField.getText().trim(),
                    contactField.getText().trim(),
                    addUserDialog);
        });

        JButton cancelButton = createActionButton("❌ Cancel", new Color(220, 20, 60));
        cancelButton.addActionListener(e -> addUserDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addUserDialog.add(formPanel, BorderLayout.CENTER);
        addUserDialog.add(buttonPanel, BorderLayout.SOUTH);
        addUserDialog.setVisible(true);
    }

    private void saveNewUser(String fullName, String username, String password,
                             String confirmPassword, String role, String email,
                             String contact, JDialog dialog) {

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() ||
                email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "All fields are required!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(dialog,
                    "Passwords do not match!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(dialog,
                    "Password must be at least 6 characters!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username already exists
        if (userDao.usernameExists(username)) {
            JOptionPane.showMessageDialog(dialog,
                    "Username already exists! Please choose another username.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new user object
        User newUser = new User(username, password, fullName, role, email, contact);

        // Save to database
        if (userDao.addUser(newUser)) {
            JOptionPane.showMessageDialog(dialog,
                    "User added successfully!\n" +
                            "Username: " + username + "\n" +
                            "Role: " + role + "\n" +
                            "The user can now login with these credentials.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refreshUserTable();
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "Failed to add user. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        String username = (String) userTableModel.getValueAt(selectedRow, 1);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user:\n" +
                        "Username: " + username + "\n" +
                        "ID: " + userId + "\n\n" +
                        "This action cannot be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDao.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this,
                        "User deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete user.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Add Application Form Methods (keep these as they were)
    private void openAddApplicationForm() {
        JDialog addAppDialog = new JDialog(this, "Add New Application", true);
        addAppDialog.setSize(600, 650);
        addAppDialog.setLocationRelativeTo(this);
        addAppDialog.setLayout(new BorderLayout(10, 10));
        addAppDialog.getContentPane().setBackground(Color.WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("➕ ADD NEW APPLICATION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Application Name
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel appNameLabel = new JLabel("Application Name:");
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(appNameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField appNameField = new JTextField(25);
        appNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(appNameField, gbc);

        // Group Head Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel groupHeadNameLabel = new JLabel("Group Head Name:");
        groupHeadNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(groupHeadNameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField groupHeadNameField = new JTextField(25);
        groupHeadNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(groupHeadNameField, gbc);

        // Group Head Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel groupHeadEmailLabel = new JLabel("Group Head Email:");
        groupHeadEmailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(groupHeadEmailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField groupHeadEmailField = new JTextField(25);
        groupHeadEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(groupHeadEmailField, gbc);

        // Group Head Contact
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel groupHeadContactLabel = new JLabel("Group Head Contact:");
        groupHeadContactLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(groupHeadContactLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField groupHeadContactField = new JTextField(25);
        groupHeadContactField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(groupHeadContactField, gbc);

        // Relationship Manager Name
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel rmNameLabel = new JLabel("Relationship Manager Name:");
        rmNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(rmNameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField rmNameField = new JTextField(25);
        rmNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(rmNameField, gbc);

        // Relationship Manager Email
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel rmEmailLabel = new JLabel("Relationship Manager Email:");
        rmEmailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(rmEmailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField rmEmailField = new JTextField(25);
        rmEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(rmEmailField, gbc);

        // Relationship Manager Contact
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel rmContactLabel = new JLabel("Relationship Manager Contact:");
        rmContactLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(rmContactLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField rmContactField = new JTextField(25);
        rmContactField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(rmContactField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = createActionButton("💾 Save Application", new Color(60, 179, 113));
        saveButton.addActionListener(e -> {
            saveNewApplication(appNameField.getText().trim(),
                    groupHeadNameField.getText().trim(),
                    groupHeadEmailField.getText().trim(),
                    groupHeadContactField.getText().trim(),
                    rmNameField.getText().trim(),
                    rmEmailField.getText().trim(),
                    rmContactField.getText().trim(),
                    addAppDialog);
        });

        JButton cancelButton = createActionButton("❌ Cancel", new Color(220, 20, 60));
        cancelButton.addActionListener(e -> addAppDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addAppDialog.add(formPanel, BorderLayout.CENTER);
        addAppDialog.add(buttonPanel, BorderLayout.SOUTH);
        addAppDialog.setVisible(true);
    }

    private void saveNewApplication(String appName, String groupHeadName, String groupHeadEmail,
                                    String groupHeadContact, String rmName, String rmEmail,
                                    String rmContact, JDialog dialog) {

        // Validation
        if (appName.isEmpty() || groupHeadName.isEmpty() || groupHeadEmail.isEmpty() ||
                groupHeadContact.isEmpty() || rmName.isEmpty() || rmEmail.isEmpty() || rmContact.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "All fields are required!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if application name already exists
        if (applicationDao.applicationNameExists(appName)) {
            JOptionPane.showMessageDialog(dialog,
                    "Application name already exists! Please choose another name.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create new application object
        ApplicationDao.Application newApp = new ApplicationDao.Application(
                appName, groupHeadName, groupHeadEmail, groupHeadContact,
                rmName, rmEmail, rmContact, currentAdmin.getId()
        );

        // Save to database
        if (applicationDao.addApplication(newApp)) {
            JOptionPane.showMessageDialog(dialog,
                    "Application added successfully!\n" +
                            "Application: " + appName + "\n" +
                            "The application can now be used for incident management.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refreshApplicationTable();
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "Failed to add application. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedApplication() {
        int selectedRow = applicationTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an application to delete!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int appId = (int) applicationTableModel.getValueAt(selectedRow, 0);
        String appName = (String) applicationTableModel.getValueAt(selectedRow, 1);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete application:\n" +
                        "Application: " + appName + "\n" +
                        "ID: " + appId + "\n\n" +
                        "This action cannot be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (applicationDao.deleteApplication(appId)) {
                JOptionPane.showMessageDialog(this,
                        "Application deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshApplicationTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete application.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Report Generation Methods (these can stay as they are or be simplified)
    private void generateQuickReport() {
        List<Incident> allIncidents = incidentDao.getAllIncidents();
        List<Incident> openIncidents = incidentDao.getIncidentsByStatus("OPEN");
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");
        List<User> users = userDao.getAllUsers();
        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());

        String report = "=== QUICK SYSTEM REPORT ===\n" +
                "Generated on: " + currentTime + "\n" +
                "================================\n\n" +
                "📊 SYSTEM OVERVIEW:\n" +
                "-------------------\n" +
                "• Total Users: " + users.size() + "\n" +
                "• Total Applications: " + applications.size() + "\n" +
                "• Total Incidents: " + allIncidents.size() + "\n" +
                "• Open Incidents: " + openIncidents.size() + "\n" +
                "• Closed Incidents: " + closedIncidents.size() + "\n\n" +
                "👥 USER DISTRIBUTION:\n" +
                "---------------------\n";

        long adminCount = users.stream().filter(u -> u.getRole().equals("ADMIN")).count();
        long imCount = users.stream().filter(u -> u.getRole().equals("INCIDENT_MANAGER")).count();
        long l3Count = users.stream().filter(u -> u.getRole().equals("L3_SUPPORT")).count();

        report += "• Administrators: " + adminCount + "\n" +
                "• Incident Managers: " + imCount + "\n" +
                "• L3 Support: " + l3Count + "\n\n" +
                "💻 APPLICATIONS WITH INCIDENTS:\n" +
                "-------------------------------\n";

        for (ApplicationDao.Application app : applications) {
            long appIncidentCount = allIncidents.stream()
                    .filter(i -> i.getApplicationName().equals(app.getApplicationName()))
                    .count();
            if (appIncidentCount > 0) {
                report += "• " + app.getApplicationName() + ": " + appIncidentCount + " incidents\n";
            }
        }

        report += "\n🔴 OPEN INCIDENTS DETAILS:\n" +
                "--------------------------\n";

        if (openIncidents.isEmpty()) {
            report += "No open incidents. Excellent!\n";
        } else {
            for (Incident incident : openIncidents) {
                report += "• ID: " + incident.getId() +
                        ", Application: " + incident.getApplicationName() +
                        ", Created: " + incident.getIssueStartTime() + "\n";
            }
        }

        JTextArea textArea = new JTextArea(report, 20, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Quick System Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // Other report methods can be simplified similarly
    private void generateIncidentSummaryReport() {
        List<Incident> incidents = incidentDao.getAllIncidents();

        if (incidents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No incidents found in the system.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());

        StringBuilder report = new StringBuilder();
        report.append("=== INCIDENT SUMMARY REPORT ===\n");
        report.append("Generated on: ").append(currentTime).append("\n");
        report.append("Total Incidents: ").append(incidents.size()).append("\n");
        report.append("================================\n\n");

        // Group by status
        long openCount = incidents.stream().filter(i -> "OPEN".equals(i.getStatus())).count();
        long assignedCount = incidents.stream().filter(i -> "ASSIGNED".equals(i.getStatus())).count();
        long closedCount = incidents.stream().filter(i -> "CLOSED".equals(i.getStatus())).count();

        report.append("📊 STATUS DISTRIBUTION:\n");
        report.append("----------------------\n");
        report.append("• OPEN: ").append(openCount).append("\n");
        report.append("• ASSIGNED: ").append(assignedCount).append("\n");
        report.append("• CLOSED: ").append(closedCount).append("\n\n");

        JTextArea textArea = new JTextArea(report.toString(), 15, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Incident Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // Simplified other report methods
    private void generateApplicationWiseReport() {
        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();
        List<Incident> incidents = incidentDao.getAllIncidents();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());

        StringBuilder report = new StringBuilder();
        report.append("=== APPLICATION-WISE INCIDENT REPORT ===\n");
        report.append("Generated on: ").append(currentTime).append("\n");
        report.append("========================================\n\n");

        for (ApplicationDao.Application app : applications) {
            long appIncidentCount = incidents.stream()
                    .filter(i -> i.getApplicationName().equals(app.getApplicationName()))
                    .count();
            report.append("📱 ").append(app.getApplicationName()).append(": ").append(appIncidentCount).append(" incidents\n");
        }

        JTextArea textArea = new JTextArea(report.toString(), 15, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Application-wise Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateUserActivityReport() {
        // Simple version
        List<User> users = userDao.getAllUsers();
        List<Incident> incidents = incidentDao.getAllIncidents();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());

        StringBuilder report = new StringBuilder();
        report.append("=== USER ACTIVITY REPORT ===\n");
        report.append("Generated on: ").append(currentTime).append("\n");
        report.append("============================\n\n");

        for (User user : users) {
            long userIncidentCount = incidents.stream()
                    .filter(i -> i.getCreatedBy() == user.getId())
                    .count();
            report.append("👤 ").append(user.getUsername()).append(" (").append(user.getRole()).append("): ")
                    .append(userIncidentCount).append(" incidents\n");
        }

        JTextArea textArea = new JTextArea(report.toString(), 15, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "User Activity Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateRcaAnalysisReport() {
        List<Incident> incidents = incidentDao.getAllIncidents();

        long incidentsWithRca = incidents.stream().filter(Incident::isRcaProvided).count();
        long incidentsWithoutRca = incidents.size() - incidentsWithRca;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());

        String report = "=== RCA ANALYSIS REPORT ===\n" +
                "Generated on: " + currentTime + "\n" +
                "Total Incidents: " + incidents.size() + "\n" +
                "================================\n\n" +
                "📊 RCA STATISTICS:\n" +
                "------------------\n" +
                "• Incidents with RCA: " + incidentsWithRca + "\n" +
                "• Incidents without RCA: " + incidentsWithoutRca + "\n" +
                "• RCA Coverage: " + String.format("%.2f", (double) incidentsWithRca / incidents.size() * 100) + "%\n";

        JTextArea textArea = new JTextArea(report, 15, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "RCA Analysis Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateMonthlyReport() {
        List<Incident> incidents = incidentDao.getAllIncidents();

        if (incidents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No incidents found in the system.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());

        long openCount = incidents.stream().filter(i -> "OPEN".equals(i.getStatus())).count();
        long closedCount = incidents.stream().filter(i -> "CLOSED".equals(i.getStatus())).count();

        String report = "=== MONTHLY PERFORMANCE REPORT ===\n" +
                "Generated on: " + currentTime + "\n" +
                "Total Incidents: " + incidents.size() + "\n" +
                "=====================================\n\n" +
                "📊 MONTHLY PERFORMANCE:\n" +
                "-----------------------\n" +
                "• Total Incidents: " + incidents.size() + "\n" +
                "• Open Incidents: " + openCount + "\n" +
                "• Closed Incidents: " + closedCount + "\n" +
                "• Closure Rate: " + (incidents.size() > 0 ?
                String.format("%.2f", (double) closedCount / incidents.size() * 100) : "0.00") + "%\n";

        JTextArea textArea = new JTextArea(report, 15, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Monthly Performance Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportAllReports() {
        JOptionPane.showMessageDialog(this,
                "Export feature would save all reports to a file.\n" +
                        "This is a placeholder implementation.",
                "Export Reports",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom cell renderer for status column
    class StatusTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 3) { // Status column
                String status = (String) value;
                if (status != null) {
                    c.setForeground(getStatusColor(status));
                    ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
                }
            }

            return c;
        }
    }

    private Color getStatusColor(String status) {
        if (status == null) return Color.BLACK;

        switch (status.toUpperCase()) {
            case "OPEN":
                return Color.RED;
            case "ASSIGNED":
                return Color.ORANGE;
            case "CLOSED":
                return Color.GREEN;
            default:
                return Color.BLACK;
        }
    }
}
