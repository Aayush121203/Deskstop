package org.example.ui;

import org.example.dao.IncidentDao;
import org.example.dao.ApplicationDao;
import org.example.dao.UserDao;
import org.example.model.Incident;
import org.example.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.*;
import java.util.List;

public class L3SupportDashboard extends JFrame {
    private User currentUser;
    private IncidentDao incidentDao;
    private ApplicationDao applicationDao;
    private UserDao userDao;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JTable assignedIncidentsTable;
    private DefaultTableModel assignedIncidentsTableModel;
    private JTable resolvedIncidentsTable;
    private DefaultTableModel resolvedIncidentsTableModel;

    // Burgundy color scheme
    private final Color BURGUNDY_PRIMARY = new Color(151, 20, 77);
    private final Color BURGUNDY_DARK = new Color(120, 16, 61);
    private final Color BURGUNDY_LIGHT = new Color(180, 40, 100);

    // Components for dashboard
    private JLabel totalAssignedLabel;
    private JLabel totalResolvedLabel;
    private JLabel averageTatLabel;
    private JLabel pendingRcaLabel;

    public L3SupportDashboard(User user) {
        this.currentUser = user;
        this.incidentDao = new IncidentDao();
        this.applicationDao = new ApplicationDao();
        this.userDao = new UserDao();

        initComponents();
        setTitle("L3 Support Dashboard - Incident Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
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
        headerPanel.setPreferredSize(new Dimension(1400, 100));

        // Left side - Welcome message
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        welcomePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("🛠️ L3 SUPPORT DASHBOARD");
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
        logoutButton.setBackground(BURGUNDY_DARK);
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
        JButton dashboardBtn = createNavButton("🏠 Dashboard", BURGUNDY_LIGHT);
        dashboardBtn.addActionListener(e -> {
            refreshDashboardStats();
            showDashboard();
        });

        JButton assignedIncidentsBtn = createNavButton("📋 Assigned Incidents", new Color(151, 40, 90));
        assignedIncidentsBtn.addActionListener(e -> showAssignedIncidents());

        JButton resolveIncidentBtn = createNavButton("✅ Resolve Incident", new Color(151, 30, 85));
        resolveIncidentBtn.addActionListener(e -> showResolveIncident());

        JButton rcaManagementBtn = createNavButton("🔍 RCA Management", new Color(151, 20, 100));
        rcaManagementBtn.addActionListener(e -> showRcaManagement());

        JButton resolvedIncidentsBtn = createNavButton("📊 Resolved Incidents", BURGUNDY_ACCENT);
        resolvedIncidentsBtn.addActionListener(e -> showResolvedIncidents());

        JButton reportsBtn = createNavButton("📈 Performance Reports", new Color(151, 50, 95));
        reportsBtn.addActionListener(e -> showReports());

        buttonPanel.add(dashboardBtn);
        buttonPanel.add(assignedIncidentsBtn);
        buttonPanel.add(resolveIncidentBtn);
        buttonPanel.add(rcaManagementBtn);
        buttonPanel.add(resolvedIncidentsBtn);
        buttonPanel.add(reportsBtn);

        navPanel.add(navTitle, BorderLayout.NORTH);
        navPanel.add(buttonPanel, BorderLayout.CENTER);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        // Create different cards
        JPanel dashboardCard = createDashboardCard();
        JPanel assignedIncidentsCard = createAssignedIncidentsCard();
        JPanel resolveIncidentCard = createResolveIncidentCard();
        JPanel rcaManagementCard = createRcaManagementCard();
        JPanel resolvedIncidentsCard = createResolvedIncidentsCard();
        JPanel reportsCard = createReportsCard();

        contentPanel.add(dashboardCard, "DASHBOARD");
        contentPanel.add(assignedIncidentsCard, "ASSIGNED_INCIDENTS");
        contentPanel.add(resolveIncidentCard, "RESOLVE_INCIDENT");
        contentPanel.add(rcaManagementCard, "RCA_MANAGEMENT");
        contentPanel.add(resolvedIncidentsCard, "RESOLVED_INCIDENTS");
        contentPanel.add(reportsCard, "REPORTS");

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(navPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Show dashboard by default
        refreshDashboardStats();
        showDashboard();
    }

    private JPanel createDashboardCard() {
        JPanel dashboardCard = new JPanel(new BorderLayout(20, 20));
        dashboardCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dashboardCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🛠️ L3 SUPPORT OVERVIEW");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BURGUNDY_PRIMARY);

        JButton refreshButton = createActionButton("🔄 Refresh", BURGUNDY_PRIMARY);
        refreshButton.addActionListener(e -> refreshDashboardStats());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        totalAssignedLabel = new JLabel("0");
        totalResolvedLabel = new JLabel("0");
        averageTatLabel = new JLabel("0 min");
        pendingRcaLabel = new JLabel("0");

        statsPanel.add(createStatCard("📋 Assigned Incidents", totalAssignedLabel, new Color(255, 140, 0)));
        statsPanel.add(createStatCard("✅ Resolved Incidents", totalResolvedLabel, new Color(60, 179, 113)));
        statsPanel.add(createStatCard("⏱️ Average TAT", averageTatLabel, new Color(30, 144, 255)));
        statsPanel.add(createStatCard("🔍 Pending RCA", pendingRcaLabel, new Color(138, 43, 226)));

        // Quick actions
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton quickViewAssigned = createQuickActionButton("👁️ View Assigned", new Color(255, 140, 0));
        quickViewAssigned.addActionListener(e -> showAssignedIncidents());

        JButton quickResolve = createQuickActionButton("✅ Resolve Incident", new Color(60, 179, 113));
        quickResolve.addActionListener(e -> showResolveIncident());

        JButton quickRCA = createQuickActionButton("🔍 Add RCA", new Color(138, 43, 226));
        quickRCA.addActionListener(e -> showRcaManagement());

        JButton quickReports = createQuickActionButton("📈 View Reports", new Color(30, 144, 255));
        quickReports.addActionListener(e -> showReports());

        quickActionsPanel.add(quickViewAssigned);
        quickActionsPanel.add(quickResolve);
        quickActionsPanel.add(quickRCA);
        quickActionsPanel.add(quickReports);

        dashboardCard.add(titlePanel, BorderLayout.NORTH);
        dashboardCard.add(statsPanel, BorderLayout.CENTER);
        dashboardCard.add(quickActionsPanel, BorderLayout.SOUTH);

        return dashboardCard;
    }

    private JPanel createAssignedIncidentsCard() {
        JPanel assignedCard = new JPanel(new BorderLayout(20, 20));
        assignedCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        assignedCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📋 ASSIGNED INCIDENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BURGUNDY_PRIMARY);

        JButton refreshButton = createActionButton("🔄 Refresh", BURGUNDY_PRIMARY);
        refreshButton.addActionListener(e -> refreshAssignedIncidentsTable());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // Table for assigned incidents
        String[] columns = {"ID", "Application", "Start Time", "Problem Statement", "Business Impact", "Temporary Solution"};
        assignedIncidentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        assignedIncidentsTable = new JTable(assignedIncidentsTableModel);
        assignedIncidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        assignedIncidentsTable.setRowHeight(30);
        assignedIncidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignedIncidentsTable.getTableHeader().setBackground(new Color(240, 248, 255));
        assignedIncidentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(assignedIncidentsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableScrollPane.setPreferredSize(new Dimension(900, 400));

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton viewDetailsButton = createActionButton("👁️ View Details", new Color(30, 144, 255));
        viewDetailsButton.addActionListener(e -> viewAssignedIncidentDetails());

        JButton resolveButton = createActionButton("✅ Resolve", new Color(60, 179, 113));
        resolveButton.addActionListener(e -> resolveSelectedIncident());

        actionPanel.add(viewDetailsButton);
        actionPanel.add(resolveButton);

        assignedCard.add(titlePanel, BorderLayout.NORTH);
        assignedCard.add(tableScrollPane, BorderLayout.CENTER);
        assignedCard.add(actionPanel, BorderLayout.SOUTH);

        refreshAssignedIncidentsTable();

        return assignedCard;
    }

    private JPanel createResolveIncidentCard() {
        JPanel resolveCard = new JPanel(new BorderLayout(20, 20));
        resolveCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        resolveCard.setOpaque(false);

        JLabel titleLabel = new JLabel("✅ RESOLVE INCIDENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BURGUNDY_PRIMARY);
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
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Incident ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel incidentIdLabel = new JLabel("Incident ID:");
        incidentIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(incidentIdLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField incidentIdField = new JTextField(20);
        incidentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(incidentIdField, gbc);

        // Find button
        gbc.gridx = 2;
        JButton findButton = createActionButton("🔍 Find", new Color(30, 144, 255));
        formPanel.add(findButton, gbc);

        // Multiline fields (defined here so both find and save can use them)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;

        JTextArea problemArea = new JTextArea(4, 40);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        problemArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        problemArea.setBorder(BorderFactory.createTitledBorder("Problem Statement"));
        problemArea.setEditable(false);
        formPanel.add(new JScrollPane(problemArea), gbc);

        gbc.gridy = 2;
        JTextArea impactArea = new JTextArea(4, 40);
        impactArea.setLineWrap(true);
        impactArea.setWrapStyleWord(true);
        impactArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        impactArea.setBorder(BorderFactory.createTitledBorder("Business Impact"));
        impactArea.setEditable(false);
        formPanel.add(new JScrollPane(impactArea), gbc);

        gbc.gridy = 3;
        JTextArea tempSolArea = new JTextArea(4, 40);
        tempSolArea.setLineWrap(true);
        tempSolArea.setWrapStyleWord(true);
        tempSolArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tempSolArea.setBorder(BorderFactory.createTitledBorder("Temporary Solution"));
        tempSolArea.setEditable(false);
        formPanel.add(new JScrollPane(tempSolArea), gbc);

        gbc.gridy = 4;
        JTextArea rootCauseArea = new JTextArea(4, 40);
        rootCauseArea.setLineWrap(true);
        rootCauseArea.setWrapStyleWord(true);
        rootCauseArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rootCauseArea.setBorder(BorderFactory.createTitledBorder("Root Cause (L3 to fill)"));
        formPanel.add(new JScrollPane(rootCauseArea), gbc);

// Row 5 - Permanent Solution
        gbc.gridy = 5;
        JTextArea permSolArea = new JTextArea(4, 40);
        permSolArea.setLineWrap(true);
        permSolArea.setWrapStyleWord(true);
        permSolArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        permSolArea.setBorder(BorderFactory.createTitledBorder("Permanent Solution (L3 to fill)"));
        formPanel.add(new JScrollPane(permSolArea), gbc);

// Row 6 - Preventive Measures
        gbc.gridy = 6;
        JTextArea preventiveArea = new JTextArea(4, 40);
        preventiveArea.setLineWrap(true);
        preventiveArea.setWrapStyleWord(true);
        preventiveArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        preventiveArea.setBorder(BorderFactory.createTitledBorder("Preventive Measures"));
        formPanel.add(new JScrollPane(preventiveArea), gbc);

        // Find button logic: load incident details into fields
        findButton.addActionListener(e -> {
            String incidentIdText = incidentIdField.getText().trim();
            if (incidentIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter Incident ID.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int incidentId = Integer.parseInt(incidentIdText);
                Incident incident = incidentDao.getIncidentById(incidentId);
                if (incident != null) {
                    // Fill fields
                    problemArea.setText(incident.getProblemStatement());
                    impactArea.setText(incident.getBusinessImpact());
                    tempSolArea.setText(
                            incident.getTemporarySolution() != null
                                    ? incident.getTemporarySolution()
                                    : "No temporary solution recorded."
                    );
                    permSolArea.setText(
                            incident.getPermanentSolution() != null
                                    ? incident.getPermanentSolution()
                                    : ""
                    );
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Incident not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Incident ID!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Buttons at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton saveButton = createActionButton("Save Solution", new Color(60, 179, 113));
        saveButton.addActionListener(e -> {
            String idText = incidentIdField.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter Incident ID and click Find first.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int incidentId;
            try {
                incidentId = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Incident ID.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String root = rootCauseArea.getText().trim();
            String perm = permSolArea.getText().trim();
            String prev = preventiveArea.getText().trim();

            if (root.isEmpty() || perm.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Root Cause and Permanent Solution are mandatory.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            saveL3Solution(incidentId, root, perm, prev);
        });

        JButton cancelButton = createActionButton("Cancel", new Color(220, 20, 60));
        cancelButton.addActionListener(e -> showAssignedIncidents());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setPreferredSize(new Dimension(800, 500));

        resolveCard.add(titleLabel, BorderLayout.NORTH);
        resolveCard.add(formScrollPane, BorderLayout.CENTER);
        resolveCard.add(buttonPanel, BorderLayout.SOUTH);

        return resolveCard;
    }

    private JPanel createRcaManagementCard() {
        JPanel rcaCard = new JPanel(new BorderLayout(20, 20));
        rcaCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rcaCard.setOpaque(false);

        JLabel titleLabel = new JLabel("🔍 RCA MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BURGUNDY_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        rcaCard.add(titleLabel, BorderLayout.NORTH);

        // Get incidents where this L3 has provided RCA and status is still ASSIGNED
        List<Incident> assignedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");
        List<Incident> myRcaIncidents = assignedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .filter(Incident::isRcaProvided)   // RCA already given
                .toList();

        if (myRcaIncidents.isEmpty()) {
            JLabel noRcaLabel = new JLabel("No incidents where RCA is provided and pending closure.");
            noRcaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noRcaLabel.setHorizontalAlignment(SwingConstants.CENTER);
            rcaCard.add(noRcaLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"ID", "Application", "Start Time", "Problem", "RCA Provided", "Status"};
            DefaultTableModel rcaTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Incident incident : myRcaIncidents) {
                String problem = incident.getProblemStatement();
                if (problem != null && problem.length() > 50) {
                    problem = problem.substring(0, 47) + "...";
                }

                rcaTableModel.addRow(new Object[]{
                        incident.getId(),
                        incident.getApplicationName(),
                        incident.getIssueStartTime(),
                        problem,
                        incident.isRcaProvided() ? "Yes" : "No",
                        incident.getStatus()
                });
            }

            JTable rcaTable = new JTable(rcaTableModel);
            rcaTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rcaTable.setRowHeight(25);
            rcaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
                    rcaTable.getTableHeader().setBackground(new Color(240, 248, 255));
            rcaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scrollPane = new JScrollPane(rcaTable);
            rcaCard.add(scrollPane, BorderLayout.CENTER);
        }

        return rcaCard;
    }



    private JPanel createResolvedIncidentsCard() {
        JPanel resolvedCard = new JPanel(new BorderLayout(20, 20));
        resolvedCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        resolvedCard.setOpaque(false);

        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📊 RESOLVED INCIDENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BURGUNDY_PRIMARY);

        JButton refreshButton = createActionButton("🔄 Refresh", BURGUNDY_PRIMARY);
        refreshButton.addActionListener(e -> refreshResolvedIncidentsTable());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        // Table for resolved incidents
        String[] columns = {"ID", "Application", "Start Time", "Closed Time", "TAT (min)", "RCA Provided", "Status"};
        resolvedIncidentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resolvedIncidentsTable = new JTable(resolvedIncidentsTableModel);
        resolvedIncidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resolvedIncidentsTable.setRowHeight(30);
        resolvedIncidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resolvedIncidentsTable.getTableHeader().setBackground(new Color(240, 248, 255));
        resolvedIncidentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(resolvedIncidentsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tableScrollPane.setPreferredSize(new Dimension(900, 400));

        resolvedCard.add(titlePanel, BorderLayout.NORTH);
        resolvedCard.add(tableScrollPane, BorderLayout.CENTER);

        refreshResolvedIncidentsTable();

        return resolvedCard;
    }

    private JPanel createReportsCard() {
        JPanel reportsCard = new JPanel(new BorderLayout(20, 20));
        reportsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        reportsCard.setOpaque(false);

        JLabel titleLabel = new JLabel("📈 PERFORMANCE REPORTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(BURGUNDY_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel reportsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        reportsPanel.setOpaque(false);
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton tatReportBtn = createQuickActionButton("⏱️ TAT Analysis Report", new Color(30, 144, 255));
        tatReportBtn.addActionListener(e -> generateTatReport());

        JButton rcaReportBtn = createQuickActionButton("🔍 RCA Compliance Report", new Color(138, 43, 226));
        rcaReportBtn.addActionListener(e -> generateRcaComplianceReport());

        JButton performanceBtn = createQuickActionButton("📊 My Performance", new Color(60, 179, 113));
        performanceBtn.addActionListener(e -> generatePerformanceReport());

        JButton monthlyReportBtn = createQuickActionButton("📅 Monthly Summary", new Color(255, 140, 0));
        monthlyReportBtn.addActionListener(e -> generateMonthlySummary());

        JButton exportReportBtn = createQuickActionButton("💾 Export Reports", new Color(46, 139, 87));
        exportReportBtn.addActionListener(e -> exportReports());

        JButton comparativeBtn = createQuickActionButton("📈 Comparative Analysis", BURGUNDY_LIGHT);
        comparativeBtn.addActionListener(e -> generateComparativeAnalysis());

        reportsPanel.add(tatReportBtn);
        reportsPanel.add(rcaReportBtn);
        reportsPanel.add(performanceBtn);
        reportsPanel.add(monthlyReportBtn);
        reportsPanel.add(exportReportBtn);
        reportsPanel.add(comparativeBtn);

        reportsCard.add(titleLabel, BorderLayout.NORTH);
        reportsCard.add(reportsPanel, BorderLayout.CENTER);

        return reportsCard;
    }

    // Navigation methods
    private void showDashboard() {
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private void showAssignedIncidents() {
        cardLayout.show(contentPanel, "ASSIGNED_INCIDENTS");
    }

    private void showResolveIncident() {
        cardLayout.show(contentPanel, "RESOLVE_INCIDENT");
    }

    private void showRcaManagement() {
        cardLayout.show(contentPanel, "RCA_MANAGEMENT");
    }

    private void showResolvedIncidents() {
        cardLayout.show(contentPanel, "RESOLVED_INCIDENTS");
    }

    private void showReports() {
        cardLayout.show(contentPanel, "REPORTS");
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
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

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel statCard = new JPanel(new BorderLayout(10, 10));
        statCard.setBackground(new Color(255, 255, 255, 230));
        statCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.DARK_GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statCard.add(titleLabel, BorderLayout.NORTH);
        statCard.add(valueLabel, BorderLayout.CENTER);

        return statCard;
    }

    // Data refresh methods
    private void refreshDashboardStats() {
        List<Incident> allIncidents = incidentDao.getAllIncidents();
        List<Incident> assignedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");

        // Filter incidents assigned to current L3 user
        List<Incident> myAssignedIncidents = assignedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        List<Incident> myResolvedIncidents = closedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        // Calculate average TAT
        long totalTat = 0;
        int count = 0;
        for (Incident incident : myResolvedIncidents) {
            if (incident.getIssueEndTime() != null) {
                long tat = incidentDao.calculateTAT(incident.getIssueStartTime(), incident.getIssueEndTime());
                totalTat += tat;
                count++;
            }
        }
        long averageTat = count > 0 ? totalTat / count : 0;

        // Count incidents needing RCA
        int pendingRcaCount = (int) closedIncidents.stream()
                .filter(i -> !i.isRcaProvided())
                .count();

        // Update labels
        totalAssignedLabel.setText(String.valueOf(myAssignedIncidents.size()));
        totalResolvedLabel.setText(String.valueOf(myResolvedIncidents.size()));
        averageTatLabel.setText(averageTat + " min");
        pendingRcaLabel.setText(String.valueOf(pendingRcaCount));
    }

    private void refreshAssignedIncidentsTable() {
        assignedIncidentsTableModel.setRowCount(0);
        List<Incident> assignedIncidents = incidentDao.getIncidentsByStatus("ASSIGNED");

        // Filter incidents assigned to current L3 user
        List<Incident> myAssignedIncidents = assignedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        for (Incident incident : myAssignedIncidents) {
            Object[] row = {
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    truncateText(incident.getProblemStatement(), 50),
                    truncateText(incident.getBusinessImpact(), 50),
                    truncateText(incident.getTemporarySolution(), 50)
            };
            assignedIncidentsTableModel.addRow(row);
        }
    }

    private void refreshResolvedIncidentsTable() {
        resolvedIncidentsTableModel.setRowCount(0);
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");

        // Filter incidents resolved by current L3 user
        List<Incident> myResolvedIncidents = closedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        for (Incident incident : myResolvedIncidents) {
            long tat = incidentDao.calculateTAT(incident.getIssueStartTime(),
                    incident.getIssueEndTime() != null ? incident.getIssueEndTime() :
                            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

            Object[] row = {
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    incident.getIssueEndTime(),
                    tat,
                    incident.isRcaProvided() ? "Yes" : "No",
                    incident.getStatus()
            };
            resolvedIncidentsTableModel.addRow(row);
        }
    }

    private void viewAssignedIncidentDetails() {
        int selectedRow = assignedIncidentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an incident to view details!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incidentId = (int) assignedIncidentsTableModel.getValueAt(selectedRow, 0);
        Incident incident = incidentDao.getIncidentById(incidentId);

        if (incident != null) {
            displayIncidentDetails(incident);
        }
    }

    private void resolveSelectedIncident() {
        int selectedRow = assignedIncidentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an incident to resolve!",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int incidentId = (int) assignedIncidentsTableModel.getValueAt(selectedRow, 0);
        showResolveIncident();
        // You can pre-fill the incident ID in the resolve form here
    }

    private void displayIncidentDetails(Incident incident) {
        JDialog detailsDialog = new JDialog(this, "Incident Details - ID: " + incident.getId(), true);
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

        addDetailRow(detailsPanel, gbc, "Incident ID:", String.valueOf(incident.getId()), 0);
        addDetailRow(detailsPanel, gbc, "Application:", incident.getApplicationName(), 1);
        addDetailRow(detailsPanel, gbc, "Start Time:", incident.getIssueStartTime(), 2);
        addDetailRow(detailsPanel, gbc, "Status:", incident.getStatus(), 3);

        // Problem Statement
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        detailsPanel.add(new JLabel("Problem Statement:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JTextArea problemArea = new JTextArea(incident.getProblemStatement(), 4, 40);
        problemArea.setLineWrap(true);
        problemArea.setWrapStyleWord(true);
        problemArea.setEditable(false);
        problemArea.setBackground(new Color(240, 240, 240));
        detailsPanel.add(new JScrollPane(problemArea), gbc);

        // Business Impact
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        detailsPanel.add(new JLabel("Business Impact:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JTextArea impactArea = new JTextArea(incident.getBusinessImpact(), 4, 40);
        impactArea.setLineWrap(true);
        impactArea.setWrapStyleWord(true);
        impactArea.setEditable(false);
        impactArea.setBackground(new Color(240, 240, 240));
        detailsPanel.add(new JScrollPane(impactArea), gbc);

        // Temporary Solution
        if (incident.getTemporarySolution() != null && !incident.getTemporarySolution().isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            detailsPanel.add(new JLabel("Temporary Solution:"), gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.WEST;
            JTextArea tempSolutionArea = new JTextArea(incident.getTemporarySolution(), 3, 40);
            tempSolutionArea.setLineWrap(true);
            tempSolutionArea.setWrapStyleWord(true);
            tempSolutionArea.setEditable(false);
            tempSolutionArea.setBackground(new Color(240, 240, 240));
            detailsPanel.add(new JScrollPane(tempSolutionArea), gbc);
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

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(value), gbc);
    }

    // Report generation methods
    private void generateTatReport() {
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");

        List<Incident> myResolvedIncidents = closedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        if (myResolvedIncidents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No resolved incidents found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        StringBuilder report = new StringBuilder();
        report.append("=== TURNAROUND TIME (TAT) ANALYSIS REPORT ===\n");
        report.append("Generated on: ").append(currentTime).append("\n");
        report.append("L3 Support: ").append(currentUser.getFullName()).append("\n");
        report.append("=============================================\n\n");

        long totalTat = 0;
        long minTat = Long.MAX_VALUE;
        long maxTat = 0;

        report.append("📊 TAT DETAILS:\n");
        report.append("--------------\n");

        for (Incident incident : myResolvedIncidents) {
            if (incident.getIssueEndTime() != null) {
                long tat = incidentDao.calculateTAT(incident.getIssueStartTime(), incident.getIssueEndTime());
                totalTat += tat;
                minTat = Math.min(minTat, tat);
                maxTat = Math.max(maxTat, tat);

                report.append(String.format("• ID: %d | App: %-20s | TAT: %4d min\n",
                        incident.getId(),
                        incident.getApplicationName(),
                        tat));
            }
        }

        long averageTat = myResolvedIncidents.size() > 0 ? totalTat / myResolvedIncidents.size() : 0;

        report.append("\n📈 TAT STATISTICS:\n");
        report.append("-----------------\n");
        report.append(String.format("Total Incidents: %d\n", myResolvedIncidents.size()));
        report.append(String.format("Average TAT:     %d minutes\n", averageTat));
        report.append(String.format("Minimum TAT:     %d minutes\n", minTat == Long.MAX_VALUE ? 0 : minTat));
        report.append(String.format("Maximum TAT:     %d minutes\n", maxTat));

        // TAT Categories
        int withinSla = 0, nearSla = 0, exceededSla = 0;
        for (Incident incident : myResolvedIncidents) {
            if (incident.getIssueEndTime() != null) {
                long tat = incidentDao.calculateTAT(incident.getIssueStartTime(), incident.getIssueEndTime());
                if (tat <= 60) withinSla++;
                else if (tat <= 120) nearSla++;
                else exceededSla++;
            }
        }

        report.append("\n🎯 SLA COMPLIANCE:\n");
        report.append("-----------------\n");
        report.append(String.format("Within SLA (≤60 min):  %d incidents\n", withinSla));
        report.append(String.format("Near SLA (61-120 min):  %d incidents\n", nearSla));
        report.append(String.format("Exceeded SLA (>120 min): %d incidents\n", exceededSla));

        JTextArea textArea = new JTextArea(report.toString(), 20, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "TAT Analysis Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateRcaComplianceReport() {
        List<Incident> closedIncidents = incidentDao.getIncidentsByStatus("CLOSED");

        List<Incident> myResolvedIncidents = closedIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        if (myResolvedIncidents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No resolved incidents found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        StringBuilder report = new StringBuilder();
        report.append("=== RCA COMPLIANCE REPORT ===\n");
        report.append("Generated on: ").append(currentTime).append("\n");
        report.append("L3 Support: ").append(currentUser.getFullName()).append("\n");
        report.append("==============================\n\n");

        int withRca = 0;
        int withoutRca = 0;

        report.append("🔍 RCA STATUS:\n");
        report.append("-------------\n");

        for (Incident incident : myResolvedIncidents) {
            if (incident.isRcaProvided()) {
                withRca++;
                report.append(String.format("✓ ID: %d | App: %-20s | RCA: Provided\n",
                        incident.getId(), incident.getApplicationName()));
            } else {
                withoutRca++;
                report.append(String.format("✗ ID: %d | App: %-20s | RCA: Pending\n",
                        incident.getId(), incident.getApplicationName()));
            }
        }

        double complianceRate = myResolvedIncidents.size() > 0 ?
                (double) withRca / myResolvedIncidents.size() * 100 : 0;

        report.append("\n📊 RCA STATISTICS:\n");
        report.append("-----------------\n");
        report.append(String.format("Total Resolved Incidents: %d\n", myResolvedIncidents.size()));
        report.append(String.format("With RCA Provided:        %d\n", withRca));
        report.append(String.format("Without RCA:              %d\n", withoutRca));
        report.append(String.format("RCA Compliance Rate:      %.2f%%\n", complianceRate));

        if (withoutRca > 0) {
            report.append("\n⚠️  ACTION REQUIRED:\n");
            report.append("------------------\n");
            report.append("The following incidents require RCA:\n");
            for (Incident incident : myResolvedIncidents) {
                if (!incident.isRcaProvided()) {
                    report.append(String.format("• ID: %d - %s\n",
                            incident.getId(), incident.getApplicationName()));
                }
            }
        }

        JTextArea textArea = new JTextArea(report.toString(), 20, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "RCA Compliance Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generatePerformanceReport() {
        List<Incident> allIncidents = incidentDao.getAllIncidents();

        List<Incident> myIncidents = allIncidents.stream()
                .filter(i -> i.getAssignedTo() != null && i.getAssignedTo() == currentUser.getId())
                .toList();

        if (myIncidents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No incidents assigned to you.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        StringBuilder report = new StringBuilder();
        report.append("=== L3 SUPPORT PERFORMANCE REPORT ===\n");
        report.append("Generated on: ").append(currentTime).append("\n");
        report.append("Support Engineer: ").append(currentUser.getFullName()).append("\n");
        report.append("=====================================\n\n");

        // Categorize incidents
        long openCount = myIncidents.stream().filter(i -> "OPEN".equals(i.getStatus())).count();
        long assignedCount = myIncidents.stream().filter(i -> "ASSIGNED".equals(i.getStatus())).count();
        long closedCount = myIncidents.stream().filter(i -> "CLOSED".equals(i.getStatus())).count();

        List<Incident> closedIncidents = myIncidents.stream()
                .filter(i -> "CLOSED".equals(i.getStatus()))
                .toList();

        // Calculate metrics
        long totalTat = 0;
        int rcaProvided = 0;

        for (Incident incident : closedIncidents) {
            if (incident.getIssueEndTime() != null) {
                long tat = incidentDao.calculateTAT(incident.getIssueStartTime(), incident.getIssueEndTime());
                totalTat += tat;
            }
            if (incident.isRcaProvided()) {
                rcaProvided++;
            }
        }

        long averageTat = closedIncidents.size() > 0 ? totalTat / closedIncidents.size() : 0;
        double rcaCompliance = closedIncidents.size() > 0 ? (double) rcaProvided / closedIncidents.size() * 100 : 0;
        double closureRate = myIncidents.size() > 0 ? (double) closedCount / myIncidents.size() * 100 : 0;

        report.append("📊 OVERALL PERFORMANCE:\n");
        report.append("---------------------\n");
        report.append(String.format("Total Incidents Handled:  %d\n", myIncidents.size()));
        report.append(String.format("• Open:                   %d\n", openCount));
        report.append(String.format("• Assigned:               %d\n", assignedCount));
        report.append(String.format("• Closed:                 %d\n", closedCount));
        report.append(String.format("Closure Rate:             %.2f%%\n", closureRate));
        report.append(String.format("Average TAT:              %d minutes\n", averageTat));
        report.append(String.format("RCA Compliance:           %.2f%%\n", rcaCompliance));

        // Application-wise performance
        report.append("\n💻 APPLICATION-WISE PERFORMANCE:\n");
        report.append("------------------------------\n");

        // Group by application
        Map<String, List<Incident>> appGroups = new HashMap<>();
        for (Incident incident : myIncidents) {
            appGroups.computeIfAbsent(incident.getApplicationName(), k -> new ArrayList<>()).add(incident);
        }

        for (Map.Entry<String, List<Incident>> entry : appGroups.entrySet()) {
            String appName = entry.getKey();
            List<Incident> appIncidents = entry.getValue();

            long appClosed = appIncidents.stream().filter(i -> "CLOSED".equals(i.getStatus())).count();
            long appTotal = appIncidents.size();
            double appClosureRate = appTotal > 0 ? (double) appClosed / appTotal * 100 : 0;

            report.append(String.format("• %-25s: %d/%d (%.1f%%)\n",
                    appName, appClosed, appTotal, appClosureRate));
        }

        // Recent activity
        report.append("\n🔄 RECENT ACTIVITY (Last 10 incidents):\n");
        report.append("--------------------------------------\n");

        List<Incident> recentIncidents = myIncidents.stream()
                .sorted((i1, i2) -> i2.getIssueStartTime().compareTo(i1.getIssueStartTime()))
                .limit(10)
                .toList();

        for (Incident incident : recentIncidents) {
            String statusIcon = "CLOSED".equals(incident.getStatus()) ? "✅" :
                    "ASSIGNED".equals(incident.getStatus()) ? "🟡" : "🔴";
            report.append(String.format("%s ID: %d | %-20s | %s | %s\n",
                    statusIcon,
                    incident.getId(),
                    incident.getApplicationName(),
                    incident.getIssueStartTime(),
                    incident.getStatus()));
        }

        // Recommendations
        report.append("\n💡 RECOMMENDATIONS:\n");
        report.append("------------------\n");

        if (closureRate < 80) {
            report.append("• Focus on improving closure rate\n");
        }
        if (averageTat > 120) {
            report.append("• Work on reducing average TAT\n");
        }
        if (rcaCompliance < 100) {
            report.append("• Ensure RCA is provided for all closed incidents\n");
        }
        if (assignedCount > 5) {
            report.append("• Consider delegating some assigned incidents\n");
        }

        JTextArea textArea = new JTextArea(report.toString(), 25, 70);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JOptionPane.showMessageDialog(this, scrollPane, "Performance Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateMonthlySummary() {
        // Implementation for monthly summary
        JOptionPane.showMessageDialog(this,
                "Monthly summary report will show trends and patterns.\n" +
                        "This feature requires additional data tracking.",
                "Monthly Summary Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReports() {
        JOptionPane.showMessageDialog(this,
                "Reports can be exported to PDF/Excel format.\n" +
                        "This feature requires additional libraries.",
                "Export Reports",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateComparativeAnalysis() {
        JOptionPane.showMessageDialog(this,
                "Comparative analysis with other L3 support members.\n" +
                        "This feature requires team-wide data.",
                "Comparative Analysis",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    // Additional color for accent
    private final Color BURGUNDY_ACCENT = new Color(200, 50, 120);

    // === L3: Save permanent solution and mark RCA provided ===
    private void saveL3Solution(int incidentId,
                                String rootCause,
                                String permanentFix,
                                String preventive) {

        if (rootCause == null || rootCause.trim().isEmpty()
                || permanentFix == null || permanentFix.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Root Cause and Permanent Solution are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String root = rootCause.trim();
        String perm = permanentFix.trim();
        String prev = preventive != null ? preventive.trim() : "";

        // 1) Update incident's permanent solution column
        boolean ok1 = incidentDao.updatePermanentSolution(incidentId, perm);   // incidents.permanent_solution[file:25]

        // 2) Save / update full RCA row (root, perm, preventive) in rca table
        boolean okRca = incidentDao.saveOrUpdateRca(
                incidentId, root, perm, prev, currentUser.getId()
        );

        // 3) Only mark rca_provided flag here. DO NOT set CLOSED.
        boolean ok2 = incidentDao.markRcaProvided(incidentId);                // updates incidents.rca_provided[file:25]

        if (ok1 && okRca && ok2) {
            JOptionPane.showMessageDialog(this,
                    "Solution and RCA saved. Incident Manager will review and publish the RCA.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshAssignedIncidentsTable();
            if (resolvedIncidentsTableModel != null) {
                refreshResolvedIncidentsTable();
            }
            showDashboard();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save solution. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


}