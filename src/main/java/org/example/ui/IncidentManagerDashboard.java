package org.example.ui;

// src/IncidentFrame.java
import org.example.dao.ApplicationDao;
import org.example.dao.IncidentDao;
import org.example.dao.RcaDetails;
import org.example.dao.UserDao;
import org.example.database.DatabaseConnection;
import org.example.model.Incident;
import org.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.awt.*;

public class  IncidentManagerDashboard extends JFrame {
    private User currentUser;
    private IncidentDao incidentDao;
    private ApplicationDao applicationDao;
    private UserDao userDao;

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private JTable incidentsTable;
    private DefaultTableModel incidentsTableModel;
    private JTable myIncidentsTable;
    private DefaultTableModel myIncidentsTableModel;
    private final Color BURGUNDY_PRIMARY = new Color(151, 20, 77);
    private final Color BURGUNDY_DARK    = new Color(120, 16, 61);
    private final Color BURGUNDY_LIGHT   = new Color(180, 40, 100);

    public IncidentManagerDashboard(User user) {
        this.currentUser = user;
        this.incidentDao = new IncidentDao();
        this.applicationDao = new ApplicationDao();
        this.userDao = new UserDao();

        initComponents();
        setTitle("Incident Manager Dashboard - Incident Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(255, 255, 255);
                Color color2 = new Color(245, 230, 235);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BURGUNDY_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(1300, 100));

        // Left side - Welcome message
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("📊 INCIDENT MANAGER DASHBOARD");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (" + currentUser.getFullName() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(245, 235, 240));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(userLabel);

        // Right side - User info and logout
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        infoPanel.setOpaque(false);

        JLabel emailLabel = new JLabel("📧 " + currentUser.getEmail());
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailLabel.setForeground(Color.WHITE);

        JLabel contactLabel = new JLabel("📞 " + currentUser.getContactNumber());
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

        // Navigation panel
        JPanel navPanel = new JPanel(new BorderLayout(0, 0));
        navPanel.setBackground(BURGUNDY_DARK);
        navPanel.setPreferredSize(new Dimension(220, 0));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        navTitle.setForeground(Color.WHITE);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        navTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 0, 15));
        buttonPanel.setBackground(BURGUNDY_DARK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Navigation buttons
        JButton dashboardBtn = createNavButton("🏠 Dashboard", new Color(70, 130, 180));
        dashboardBtn.addActionListener(e -> showDashboard());

        JButton createIncidentBtn = createNavButton("➕ Create Incident", BURGUNDY_PRIMARY);
        createIncidentBtn.addActionListener(e -> showCreateIncidentForm());

        JButton allIncidentsBtn = createNavButton("📋 All Incidents", new Color(30, 144, 255));
        allIncidentsBtn.addActionListener(e -> showAllIncidents());

        JButton myIncidentsBtn = createNavButton("📝 My Incidents", new Color(138, 43, 226));
        myIncidentsBtn.addActionListener(e -> showMyIncidents());

        JButton assignedIncidentsBtn = createNavButton("👥 Assigned Incidents", new Color(255, 140, 0));
        assignedIncidentsBtn.addActionListener(e -> showAssignedIncidents());

        JButton rcaManagementBtn = createNavButton("🔍 RCA Management",BURGUNDY_PRIMARY);
        rcaManagementBtn.addActionListener(e -> showRcaManagement());

        buttonPanel.add(dashboardBtn);
        buttonPanel.add(createIncidentBtn);
        buttonPanel.add(allIncidentsBtn);
        buttonPanel.add(myIncidentsBtn);
        buttonPanel.add(assignedIncidentsBtn);
        buttonPanel.add(rcaManagementBtn);

        navPanel.add(navTitle, BorderLayout.NORTH);
        navPanel.add(buttonPanel, BorderLayout.CENTER);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Create different cards
        JPanel dashboardCard = createDashboardCard();
        JPanel createIncidentCard = createCreateIncidentCard();
        JPanel allIncidentsCard = createAllIncidentsCard();
        JPanel myIncidentsCard = createMyIncidentsCard();
        JPanel assignedIncidentsCard = createAssignedIncidentsCard();
        JPanel rcaManagementCard = createRcaManagementCard();

        contentPanel.add(dashboardCard, "DASHBOARD");
        contentPanel.add(createIncidentCard, "CREATE_INCIDENT");
        contentPanel.add(allIncidentsCard, "ALL_INCIDENTS");
        contentPanel.add(myIncidentsCard, "MY_INCIDENTS");
        contentPanel.add(assignedIncidentsCard, "ASSIGNED_INCIDENTS");
        contentPanel.add(rcaManagementCard, "RCA_MANAGEMENT");

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Show dashboard by default
        showDashboard();
    }

    private JPanel createDashboardCard() {
        JPanel dashboardCard = new JPanel(new BorderLayout(20, 20));
        dashboardCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardCard.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("📊 INCIDENT MANAGEMENT OVERVIEW");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Get statistics
        List<Incident> allIncidents = incidentDao.getAllIncidents();
        List<Incident> openIncidents = incidentDao.getIncidentsByStatus("OPEN");
        List<Incident> assignedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");
        List<Incident> myIncidents = incidentDao.getIncidentsByCreatedBy(currentUser.getId());
        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();
        int totalApplications = applications != null ? applications.size() : 0;

        statsPanel.add(createStatCard("📋 Total Incidents", String.valueOf(allIncidents.size()), new Color(30, 144, 255)));
        statsPanel.add(createStatCard("🔴 Open Incidents", String.valueOf(openIncidents.size()), new Color(220, 20, 60)));
        statsPanel.add(createStatCard("🟡 Assigned Incidents", String.valueOf(assignedIncidents.size()), new Color(255, 140, 0)));
        statsPanel.add(createStatCard("🟢 Closed Incidents", String.valueOf(closedIncidents.size()), new Color(60, 179, 113)));
        statsPanel.add(createStatCard("📝 My Incidents", String.valueOf(myIncidents.size()), new Color(138, 43, 226)));
        statsPanel.add(createStatCard("💻 Total Applications", String.valueOf(totalApplications), new Color(46, 139, 87)));

        // Quick actions
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton quickCreateIncident = createQuickActionButton("➕ Create New Incident", BURGUNDY_PRIMARY);
        quickCreateIncident.addActionListener(e -> showCreateIncidentForm());

        JButton quickViewAll = createQuickActionButton("👁️ View All Incidents", new Color(30, 144, 255));
        quickViewAll.addActionListener(e -> showAllIncidents());

        JButton quickViewMy = createQuickActionButton("📝 View My Incidents", new Color(138, 43, 226));
        quickViewMy.addActionListener(e -> showMyIncidents());

        JButton quickRCA = createQuickActionButton("🔍 Manage RCA", BURGUNDY_PRIMARY);
        quickRCA.addActionListener(e -> showRcaManagement());

        quickActionsPanel.add(quickCreateIncident);
        quickActionsPanel.add(quickViewAll);
        quickActionsPanel.add(quickViewMy);
        quickActionsPanel.add(quickRCA);

        dashboardCard.add(titleLabel, BorderLayout.NORTH);
        dashboardCard.add(statsPanel, BorderLayout.CENTER);
        dashboardCard.add(quickActionsPanel, BorderLayout.SOUTH);

        return dashboardCard;
    }

    private JPanel createCreateIncidentCard() {
        JPanel createIncidentCard = new JPanel(new BorderLayout(20, 20));
        createIncidentCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        createIncidentCard.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("➕ CREATE NEW INCIDENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 230));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;   // allow vertical growth
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        // Application Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel appLabel = new JLabel("Application Name:");
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(appLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();
        String[] appNames;
        if (applications != null && !applications.isEmpty()) {
            appNames = new String[applications.size()];
            for (int i = 0; i < applications.size(); i++) {
                appNames[i] = applications.get(i).getApplicationName();
            }
        } else {
            appNames = new String[]{"No applications available"};
        }
        JComboBox<String> appComboBox = new JComboBox<>(appNames);
        appComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appComboBox.setPreferredSize(new Dimension(300, 35));
        formPanel.add(appComboBox, gbc);

        // Issue Start Time
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weighty = 0.0;
        JLabel startTimeLabel = new JLabel("Issue Start Time:");
        startTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startTimeLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(startTimeLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = dateFormat.format(new Date());
        JTextField startTimeField = new JTextField(currentTime);
        startTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startTimeField.setPreferredSize(new Dimension(300, 35));
        startTimeField.setEditable(false);
        formPanel.add(startTimeField, gbc);

        // Problem Statement
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.weighty = 0.0;
        JLabel problemLabel = new JLabel("Problem Statement:");
        problemLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        problemLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(problemLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.2; // give vertical space
        JTextArea problemArea = new JTextArea();
        problemArea.setRows(5);          // taller box
        problemArea.setColumns(40);
        problemArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        JScrollPane problemScroll = new JScrollPane(problemArea);
        problemScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.add(problemScroll, gbc);

        // Business Impact
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.weighty = 0.0;
        JLabel impactLabel = new JLabel("Business Impact:");
        impactLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        impactLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(impactLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.2;
        JTextArea impactArea = new JTextArea();
        impactArea.setRows(5);
        impactArea.setColumns(40);
        impactArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        impactArea.setLineWrap(true);
        impactArea.setWrapStyleWord(true);
        JScrollPane impactScroll = new JScrollPane(impactArea);
        impactScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.add(impactScroll, gbc);

        // Temporary Solution
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.weighty = 0.0;
        JLabel tempSolutionLabel = new JLabel("Temporary Solution:");
        tempSolutionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tempSolutionLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(tempSolutionLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.2;
        JTextArea tempSolutionArea = new JTextArea();
        tempSolutionArea.setRows(5);
        tempSolutionArea.setColumns(40);
        tempSolutionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tempSolutionArea.setLineWrap(true);
        tempSolutionArea.setWrapStyleWord(true);
        JScrollPane tempSolutionScroll = new JScrollPane(tempSolutionArea);
        tempSolutionScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.add(tempSolutionScroll, gbc);

        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton createButton = createActionButton("🚀 Create Incident", new Color(60, 179, 113));
        createButton.addActionListener(e -> {
            createIncident((String) appComboBox.getSelectedItem(),
                    problemArea.getText().trim(),
                    impactArea.getText().trim(),
                    tempSolutionArea.getText().trim());
        });

        JButton clearButton = createActionButton("🗑️ Clear Form", new Color(220, 20, 60));
        clearButton.addActionListener(e -> {
            if (appNames.length > 0 && !appNames[0].equals("No applications available")) {
                appComboBox.setSelectedIndex(0);
            }
            problemArea.setText("");
            impactArea.setText("");
            tempSolutionArea.setText("");
        });

        buttonPanel.add(createButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        createIncidentCard.add(titleLabel, BorderLayout.NORTH);
        createIncidentCard.add(formPanel, BorderLayout.CENTER);

        return createIncidentCard;
    }



    private JPanel createAllIncidentsCard() {
        JPanel allIncidentsCard = new JPanel(new BorderLayout(20, 20));
        allIncidentsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        allIncidentsCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📋 ALL INCIDENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));

        JButton refreshButton = createActionButton("🔄 Refresh", new Color(30, 144, 255));
        refreshButton.addActionListener(e -> refreshAllIncidentsTable());

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

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton assignButton = createActionButton("👤 Assign to L3", new Color(255, 140, 0));
        assignButton.addActionListener(e -> assignIncidentToL3());

        JButton closeButton = createActionButton("✅ Close Incident", new Color(60, 179, 113));
        closeButton.addActionListener(e -> closeIncident());

        JButton viewDetailsButton = createActionButton("👁️ View Details", new Color(30, 144, 255));
        viewDetailsButton.addActionListener(e -> viewIncidentDetails());

        actionPanel.add(assignButton);
        actionPanel.add(closeButton);
        actionPanel.add(viewDetailsButton);

        // Incidents table
        String[] columns = {"ID", "Application", "Start Time", "Status", "Created By", "Assigned To", "RCA"};
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
        allIncidentsCard.add(actionPanel, BorderLayout.SOUTH);
        allIncidentsCard.add(tableScrollPane, BorderLayout.CENTER);

        // Load initial data
        refreshAllIncidentsTable();

        return allIncidentsCard;
    }

    private JPanel createMyIncidentsCard() {
        JPanel myIncidentsCard = new JPanel(new BorderLayout(20, 20));
        myIncidentsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        myIncidentsCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📝 MY INCIDENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));

        JButton refreshButton = createActionButton("🔄 Refresh", new Color(30, 144, 255));
        refreshButton.addActionListener(e -> refreshMyIncidentsTable());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // My Incidents table
        String[] columns = {"ID", "Application", "Start Time", "Status", "Assigned To", "RCA"};
        myIncidentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        myIncidentsTable = new JTable(myIncidentsTableModel);
        myIncidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        myIncidentsTable.setRowHeight(30);
        myIncidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        myIncidentsTable.getTableHeader().setBackground(new Color(240, 248, 255));
        myIncidentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color code status column
        myIncidentsTable.setDefaultRenderer(Object.class, new StatusTableCellRenderer());

        JScrollPane tableScrollPane = new JScrollPane(myIncidentsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableScrollPane.setPreferredSize(new Dimension(900, 400));

        myIncidentsCard.add(titlePanel, BorderLayout.NORTH);
        myIncidentsCard.add(tableScrollPane, BorderLayout.CENTER);

        // Load initial data
        refreshMyIncidentsTable();

        return myIncidentsCard;
    }

    private JPanel createAssignedIncidentsCard() {
        JPanel assignedIncidentsCard = new JPanel(new BorderLayout(20, 20));
        assignedIncidentsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        assignedIncidentsCard.setOpaque(false);

        JLabel titleLabel = new JLabel("👥 ASSIGNED INCIDENTS (L3 SUPPORT)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create a table for assigned incidents
        DefaultTableModel assignedModel = new DefaultTableModel(
                new String[]{"ID", "Application", "Start Time", "Assigned To", "Status"}, 0);
        JTable assignedTable = new JTable(assignedModel);
        assignedTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        assignedTable.setRowHeight(25);

        // Load assigned incidents data
        List<Incident> assignedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");
        for (Incident incident : assignedIncidents) {
            assignedModel.addRow(new Object[]{
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    incident.getAssignedToName(),
                    incident.getStatus()
            });
        }

        JScrollPane scrollPane = new JScrollPane(assignedTable);

        assignedIncidentsCard.add(titleLabel, BorderLayout.NORTH);
        assignedIncidentsCard.add(scrollPane, BorderLayout.CENTER);

        // Add refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton refreshButton = createActionButton("🔄 Refresh", new Color(30, 144, 255));
        refreshButton.addActionListener(e -> {
            assignedModel.setRowCount(0);
            List<Incident> refreshedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");
            for (Incident incident : refreshedIncidents) {
                assignedModel.addRow(new Object[]{
                        incident.getId(),
                        incident.getApplicationName(),
                        incident.getIssueStartTime(),
                        incident.getAssignedToName(),
                        incident.getStatus()
                });
            }
        });
        buttonPanel.add(refreshButton);
        assignedIncidentsCard.add(buttonPanel, BorderLayout.SOUTH);

        return assignedIncidentsCard;
    }

    private JPanel createRcaManagementCard() {
        JPanel rcaManagementCard = new JPanel(new BorderLayout(20, 20));
        rcaManagementCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rcaManagementCard.setOpaque(false);

        JLabel titleLabel = new JLabel("🔍 RCA MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create RCA management interface
        JPanel rcaPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        rcaPanel.setOpaque(false);
        rcaPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton viewRcaButton = createQuickActionButton("📊 View RCA Reports", new Color(46, 139, 87));
        viewRcaButton.addActionListener(e -> viewRcaReports());

        JButton addRcaButton = createQuickActionButton("➕ Add RCA to Incident", new Color(60, 179, 113));
        addRcaButton.addActionListener(e -> openAddRcaDialog());   // CHANGED

        JButton publishRcaButton = createQuickActionButton("📢 Publish RCA", BURGUNDY_PRIMARY);
        publishRcaButton.addActionListener(e -> openPublishRcaDialog());  // CHANGED

        rcaPanel.add(viewRcaButton);
        rcaPanel.add(addRcaButton);
        rcaPanel.add(publishRcaButton);

        rcaManagementCard.add(titleLabel, BorderLayout.NORTH);
        rcaManagementCard.add(rcaPanel, BorderLayout.CENTER);

        return rcaManagementCard;
    }

    // Navigation methods
    private void showDashboard() {
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private void showCreateIncidentForm() {
        cardLayout.show(contentPanel, "CREATE_INCIDENT");
    }

    private void showAllIncidents() {
        cardLayout.show(contentPanel, "ALL_INCIDENTS");
    }

    private void showMyIncidents() {
        cardLayout.show(contentPanel, "MY_INCIDENTS");
    }

    private void showAssignedIncidents() {
        cardLayout.show(contentPanel, "ASSIGNED_INCIDENTS");
    }

    private void showRcaManagement() {
        cardLayout.show(contentPanel, "RCA_MANAGEMENT");
    }

    // Helper methods for UI components
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

    // Business logic methods
    private void createIncident(String appName, String problem, String impact, String tempSolution) {
        // Validation
        if (appName == null || appName.isEmpty() || appName.equals("No applications available")) {
            JOptionPane.showMessageDialog(this, "Please select an application! If none exist, create applications first in Admin Dashboard.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (problem.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter problem statement!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (impact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter business impact!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get application ID
        List<ApplicationDao.Application> applications = applicationDao.getAllApplications();
        int appId = -1;
        for (ApplicationDao.Application app : applications) {
            if (app.getApplicationName().equals(appName)) {
                appId = app.getId();
                break;
            }
        }

        if (appId == -1) {
            JOptionPane.showMessageDialog(this, "Application not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create incident
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = dateFormat.format(new Date());

        Incident incident = new Incident(appId, appName, currentTime, problem, impact, tempSolution, currentUser.getId());

        if (incidentDao.addIncident(incident)) {
            JOptionPane.showMessageDialog(this,
                    "Incident created successfully!\n" +
                            "Incident ID will be generated automatically.\n" +
                            "Please check 'My Incidents' section.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            showMyIncidents();
            refreshAllIncidentsTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to create incident. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllIncidentsTable() {
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

    private void refreshMyIncidentsTable() {
        myIncidentsTableModel.setRowCount(0);
        List<Incident> incidents = incidentDao.getIncidentsByCreatedBy(currentUser.getId());

        for (Incident incident : incidents) {
            Object[] row = {
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    incident.getStatus(),
                    incident.getAssignedToName() != null ? incident.getAssignedToName() : "Not Assigned",
                    incident.isRcaProvided() ? "Yes" : "No"
            };
            myIncidentsTableModel.addRow(row);
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

    private void assignIncidentToL3() {
        int selectedRow = incidentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an incident to assign!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incidentId = (int) incidentsTableModel.getValueAt(selectedRow, 0);
        String appName = (String) incidentsTableModel.getValueAt(selectedRow, 1);
        String status = (String) incidentsTableModel.getValueAt(selectedRow, 3);

        if (status.equals("CLOSED")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot assign a closed incident!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get all L3 support users
        List<User> l3Users = userDao.getUsersByRole("L3_SUPPORT");

        if (l3Users.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No L3 Support users found! Please add L3 Support users in Admin Dashboard.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] userOptions = new String[l3Users.size()];
        for (int i = 0; i < l3Users.size(); i++) {
            userOptions[i] = l3Users.get(i).getUsername() + " (" + l3Users.get(i).getFullName() + ")";
        }

        String selectedUser = (String) JOptionPane.showInputDialog(this,
                "Select L3 Support user to assign incident:\n" +
                        "Incident ID: " + incidentId + "\n" +
                        "Application: " + appName,
                "Assign to L3 Support",
                JOptionPane.QUESTION_MESSAGE,
                null,
                userOptions,
                userOptions[0]);

        if (selectedUser != null && !selectedUser.isEmpty()) {
            // Extract user ID
            int userId = -1;
            for (User user : l3Users) {
                String userString = user.getUsername() + " (" + user.getFullName() + ")";
                if (userString.equals(selectedUser)) {
                    userId = user.getId();
                    break;
                }
            }

            if (userId != -1) {
                if (incidentDao.assignIncidentToL3(incidentId, userId)) {
                    JOptionPane.showMessageDialog(this,
                            "Incident assigned successfully to L3 Support!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshAllIncidentsTable();
                    refreshMyIncidentsTable();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to assign incident.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void closeIncident() {
        int selectedRow = incidentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an incident to close!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incidentId = (int) incidentsTableModel.getValueAt(selectedRow, 0);
        String appName = (String) incidentsTableModel.getValueAt(selectedRow, 1);
        String status = (String) incidentsTableModel.getValueAt(selectedRow, 3);

        if (status.equals("CLOSED")) {
            JOptionPane.showMessageDialog(this,
                    "This incident is already closed!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm closure
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to close this incident?\n" +
                        "Incident ID: " + incidentId + "\n" +
                        "Application: " + appName + "\n\n" +
                        "Please ensure RCA is provided before closing.",
                "Confirm Incident Closure",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String endTime = dateFormat.format(new Date());

            if (incidentDao.updateIncidentStatus(incidentId, "CLOSED", endTime)) {
                JOptionPane.showMessageDialog(this,
                        "Incident closed successfully!\n" +
                                "End Time: " + endTime,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshAllIncidentsTable();
                refreshMyIncidentsTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to close incident.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewIncidentDetails() {
        int selectedRow = incidentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an incident to view details!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incidentId = (int) incidentsTableModel.getValueAt(selectedRow, 0);
        Incident incident = incidentDao.getIncidentById(incidentId);

        if (incident != null) {
            JDialog detailsDialog = new JDialog(this, "Incident Details - ID: " + incidentId, true);
            detailsDialog.setSize(600, 550);
            detailsDialog.setLocationRelativeTo(this);
            detailsDialog.setLayout(new BorderLayout(10, 10));

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel detailsPanel = new JPanel(new GridBagLayout());
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Incident ID:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(String.valueOf(incident.getId())), gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Application:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(incident.getApplicationName()), gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Start Time:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(incident.getIssueStartTime()), gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("End Time:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(incident.getIssueEndTime() != null ? incident.getIssueEndTime() : "Not Closed"), gbc);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Status:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel statusLabel = new JLabel(incident.getStatus());
            statusLabel.setForeground(getStatusColor(incident.getStatus()));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            detailsPanel.add(statusLabel, gbc);

            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Created By:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(incident.getCreatedByName() != null ? incident.getCreatedByName() : "Unknown"), gbc);

            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Assigned To:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(incident.getAssignedToName() != null ? incident.getAssignedToName() : "Not Assigned"), gbc);

            gbc.gridx = 0;
            gbc.gridy = 7;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("RCA Provided:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            detailsPanel.add(new JLabel(incident.isRcaProvided() ? "Yes" : "No"), gbc);

            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Problem Statement:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            JTextArea problemArea = new JTextArea(incident.getProblemStatement(), 3, 30);
            problemArea.setLineWrap(true);
            problemArea.setWrapStyleWord(true);
            problemArea.setEditable(false);
            problemArea.setBackground(new Color(240, 240, 240));
            detailsPanel.add(new JScrollPane(problemArea), gbc);

            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.anchor = GridBagConstraints.EAST;
            detailsPanel.add(new JLabel("Business Impact:"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            JTextArea impactArea = new JTextArea(incident.getBusinessImpact(), 3, 30);
            impactArea.setLineWrap(true);
            impactArea.setWrapStyleWord(true);
            impactArea.setEditable(false);
            impactArea.setBackground(new Color(240, 240, 240));
            detailsPanel.add(new JScrollPane(impactArea), gbc);

            if (incident.getTemporarySolution() != null && !incident.getTemporarySolution().isEmpty()) {
                gbc.gridx = 0;
                gbc.gridy = 10;
                gbc.anchor = GridBagConstraints.EAST;
                detailsPanel.add(new JLabel("Temporary Solution:"), gbc);

                gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                JTextArea tempSolutionArea = new JTextArea(incident.getTemporarySolution(), 3, 30);
                tempSolutionArea.setLineWrap(true);
                tempSolutionArea.setWrapStyleWord(true);
                tempSolutionArea.setEditable(false);
                tempSolutionArea.setBackground(new Color(240, 240, 240));
                detailsPanel.add(new JScrollPane(tempSolutionArea), gbc);
            }

            if (incident.getPermanentSolution() != null && !incident.getPermanentSolution().isEmpty()) {
                gbc.gridx = 0;
                gbc.gridy = 11;
                gbc.anchor = GridBagConstraints.EAST;
                detailsPanel.add(new JLabel("Permanent Solution:"), gbc);

                gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.WEST;
                JTextArea permSolutionArea = new JTextArea(incident.getPermanentSolution(), 3, 30);
                permSolutionArea.setLineWrap(true);
                permSolutionArea.setWrapStyleWord(true);
                permSolutionArea.setEditable(false);
                permSolutionArea.setBackground(new Color(240, 240, 240));
                detailsPanel.add(new JScrollPane(permSolutionArea), gbc);
            }

            mainPanel.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);

            // Close button
            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> detailsDialog.dispose());
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            detailsDialog.add(mainPanel);
            detailsDialog.setVisible(true);
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

    private void viewRcaReports() {
        JOptionPane.showMessageDialog(this,
                "RCA Reports feature will be implemented in next phase.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addRcaToIncident() {
        // Implementation for adding RCA to incident
        int selectedRow = incidentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an incident to add RCA!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incidentId = (int) incidentsTableModel.getValueAt(selectedRow, 0);
        String appName = (String) incidentsTableModel.getValueAt(selectedRow, 1);

        JDialog rcaDialog = new JDialog(this, "Add RCA for Incident ID: " + incidentId, true);
        rcaDialog.setSize(500, 400);
        rcaDialog.setLocationRelativeTo(this);
        rcaDialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Add Root Cause Analysis for: " + appName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel rootLbl = new JLabel("Root Cause:");
        rootLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(rootLbl, gbc);

        gbc.gridx = 1;
        JTextArea rootCauseArea = new JTextArea(4, 30);
        rootCauseArea.setLineWrap(true);
        rootCauseArea.setWrapStyleWord(true);
        rootCauseArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(new JScrollPane(rootCauseArea), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel preventiveLbl = new JLabel("Preventive Measures:");
        preventiveLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(preventiveLbl, gbc);

        gbc.gridx = 1;
        JTextArea preventiveArea = new JTextArea(4, 30);
        preventiveArea.setLineWrap(true);
        preventiveArea.setWrapStyleWord(true);
        preventiveArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(new JScrollPane(preventiveArea), gbc);

        rcaDialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(248, 248, 255));

        JButton cancelButton = createActionButton("Cancel", new Color(220, 20, 60));
        cancelButton.addActionListener(e -> rcaDialog.dispose());

        JButton saveButton = createActionButton("Save RCA (Draft)", new Color(46, 139, 87));
        saveButton.addActionListener(e -> {
            String root = rootCauseArea.getText().trim();
            String prev = preventiveArea.getText().trim();

            if (root.isEmpty()) {
                JOptionPane.showMessageDialog(rcaDialog,
                        "Root Cause is mandatory.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = """
            INSERT OR REPLACE INTO rca
            (incident_id, root_cause, permanent_fix, preventive_measures,
             created_by, created_at, published, published_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 0, NULL)
        """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, incidentId);
                pstmt.setString(2, root);
                pstmt.setString(3, ""); // permanent fix will come from L3 / later
                pstmt.setString(4, prev);
                pstmt.setInt(5, currentUser.getId());

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(rcaDialog,
                            "RCA draft saved for Incident #" + incidentId,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    rcaDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(rcaDialog,
                            "Failed to save RCA draft.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(rcaDialog,
                        "Database error while saving RCA: " + ex1.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        rcaDialog.add(buttonPanel, BorderLayout.SOUTH);
        rcaDialog.setVisible(true);
    }


    private void publishRca() {
        JOptionPane.showMessageDialog(this,
                "Publish RCA feature will be implemented in next phase.",
                "Coming Soon",
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

    private void openAddRcaDialog() {
        // Ask IM which incident to work on
        String idText = JOptionPane.showInputDialog(
                this,
                "Enter Incident ID to add/edit RCA:",
                "Add RCA to Incident",
                JOptionPane.QUESTION_MESSAGE
        );

        if (idText == null || idText.trim().isEmpty()) {
            return;
        }

        int incidentId;
        try {
            incidentId = Integer.parseInt(idText.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Incident ID.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Incident incident = incidentDao.getIncidentById(incidentId);
        if (incident == null) {
            JOptionPane.showMessageDialog(this,
                    "Incident not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Dialog UI
        JDialog rcaDialog = new JDialog(this, "Add RCA - Incident #" + incidentId, true);
        rcaDialog.setSize(700, 500);
        rcaDialog.setLocationRelativeTo(this);
        rcaDialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JTextArea rootCauseArea = new JTextArea(4, 40);
        rootCauseArea.setLineWrap(true);
        rootCauseArea.setWrapStyleWord(true);
        rootCauseArea.setBorder(BorderFactory.createTitledBorder("Root Cause"));

        JTextArea permanentFixArea = new JTextArea(4, 40);
        permanentFixArea.setLineWrap(true);
        permanentFixArea.setWrapStyleWord(true);
        permanentFixArea.setBorder(BorderFactory.createTitledBorder("Permanent Fix (from L3)"));
        permanentFixArea.setText(
                incident.getPermanentSolution() != null ? incident.getPermanentSolution() : ""
        );

        JTextArea preventiveArea = new JTextArea(4, 40);
        preventiveArea.setLineWrap(true);
        preventiveArea.setWrapStyleWord(true);
        preventiveArea.setBorder(BorderFactory.createTitledBorder("Preventive Measures"));

        formPanel.add(new JScrollPane(rootCauseArea), gbc);
        gbc.gridy++;
        formPanel.add(new JScrollPane(permanentFixArea), gbc);
        gbc.gridy++;
        formPanel.add(new JScrollPane(preventiveArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save RCA (Draft)");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String root = rootCauseArea.getText().trim();
            String perm = permanentFixArea.getText().trim();
            String prev = preventiveArea.getText().trim();

            if (root.isEmpty() || perm.isEmpty()) {
                JOptionPane.showMessageDialog(rcaDialog,
                        "Root Cause and Permanent Fix are mandatory.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Save into rca table as draft (published = 0)
            String sql = """
            INSERT OR REPLACE INTO rca
            (incident_id, root_cause, permanent_fix, preventive_measures,
             created_by, created_at, published, published_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 0, NULL)
        """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, incidentId);
                pstmt.setString(2, root);
                pstmt.setString(3, perm);
                pstmt.setString(4, prev);
                pstmt.setInt(5, currentUser.getId());

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(rcaDialog,
                            "RCA draft saved for Incident #" + incidentId,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    rcaDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(rcaDialog,
                            "Failed to save RCA draft.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(rcaDialog,
                        "Database error while saving RCA: " + ex1.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> rcaDialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        rcaDialog.add(formPanel, BorderLayout.CENTER);
        rcaDialog.add(buttonPanel, BorderLayout.SOUTH);
        rcaDialog.setVisible(true);
    }

    private void openPublishRcaDialog() {
        // Load incidents where L3 has provided RCA (rca_provided = 1)
        List<Incident> incidents = incidentDao.getAllIncidents();
        java.util.List<Incident> publishable = new java.util.ArrayList<>();
        for (Incident i : incidents) {
            if (i.isRcaProvided()) {
                publishable.add(i);
            }
        }

        if (publishable.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No incidents with RCA provided by L3.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Dialog UI with table + RCA fields
        JDialog dialog = new JDialog(this, "Publish RCA", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Top: table
        String[] cols = { "ID", "Application", "Start Time", "Status", "Assigned To", "RCA Provided" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);

        for (Incident i : publishable) {
            model.addRow(new Object[] {
                    i.getId(),
                    i.getApplicationName(),
                    i.getIssueStartTime(),
                    i.getStatus(),
                    i.getAssignedToName() != null ? i.getAssignedToName() : "Not Assigned",
                    "Yes"
            });
        }

        JScrollPane tableScroll = new JScrollPane(table);
        dialog.add(tableScroll, BorderLayout.NORTH);

        // Center: RCA fields
        JPanel formPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea rootCauseArea = new JTextArea(6, 25);
        rootCauseArea.setLineWrap(true);
        rootCauseArea.setWrapStyleWord(true);
        rootCauseArea.setBorder(BorderFactory.createTitledBorder("Root Cause"));
        rootCauseArea.setEditable(false);

        JTextArea permanentFixArea = new JTextArea(6, 25);
        permanentFixArea.setLineWrap(true);
        permanentFixArea.setWrapStyleWord(true);
        permanentFixArea.setBorder(BorderFactory.createTitledBorder("Permanent Fix (from L3)"));
        permanentFixArea.setEditable(false);

        JTextArea preventiveArea = new JTextArea(6, 25);
        preventiveArea.setLineWrap(true);
        preventiveArea.setWrapStyleWord(true);
        preventiveArea.setBorder(BorderFactory.createTitledBorder("Preventive Measures"));
        preventiveArea.setEditable(false);

        formPanel.add(new JScrollPane(rootCauseArea));
        formPanel.add(new JScrollPane(permanentFixArea));
        formPanel.add(new JScrollPane(preventiveArea));

        dialog.add(formPanel, BorderLayout.CENTER);

        // When user selects a row, load permanent solution from incident
        // When user selects a row, load RCA from rca table (entered by L3)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = (int) model.getValueAt(row, 0);

                    RcaDetails rca = incidentDao.getRcaForIncident(id);
                    if (rca != null) {
                        rootCauseArea.setText(
                                rca.getRootCause() != null ? rca.getRootCause() : ""
                        );
                        permanentFixArea.setText(
                                rca.getPermanentFix() != null ? rca.getPermanentFix() : ""
                        );
                        preventiveArea.setText(
                                rca.getPreventiveMeasures() != null ? rca.getPreventiveMeasures() : ""
                        );
                    } else {
                        rootCauseArea.setText("");
                        permanentFixArea.setText("");
                        preventiveArea.setText("");
                    }
                }
            }
        });

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton closeBtn = new JButton("Close");
        JButton publishBtn = new JButton("Publish RCA");

        closeBtn.addActionListener(e -> dialog.dispose());

        publishBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select an incident.",
                        "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int incidentId = (int) model.getValueAt(row, 0);
            String root = rootCauseArea.getText().trim();
            String perm = permanentFixArea.getText().trim();
            String prev = preventiveArea.getText().trim();

            if (root.isEmpty() || perm.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Root Cause and Permanent Fix are mandatory.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = """
            INSERT OR REPLACE INTO rca
            (incident_id, root_cause, permanent_fix, preventive_measures,
             created_by, created_at, published, published_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 1, ?)
        """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, incidentId);
                pstmt.setString(2, root);
                pstmt.setString(3, perm);
                pstmt.setString(4, prev);
                pstmt.setInt(5, currentUser.getId());

                String publishedDate = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                pstmt.setString(6, publishedDate);

                int rows2 = pstmt.executeUpdate();
                if (rows2 > 0) {
                    // Close incident when RCA published
                    incidentDao.updateIncidentStatus(incidentId, "CLOSED", publishedDate);

                    JOptionPane.showMessageDialog(dialog,
                            "RCA published for Incident #" + incidentId,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                    refreshAllIncidentsTable();
                    refreshMyIncidentsTable();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Failed to publish RCA.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(dialog,
                        "Database error while publishing RCA: " + ex1.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(closeBtn);
        buttonPanel.add(publishBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
