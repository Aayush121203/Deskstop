package org.example.ui;

import org.example.dao.IncidentDao;
import org.example.database.DatabaseConnection;
import org.example.model.Incident;
import org.example.model.User;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import java.awt.*;

public class L3SupportFrame extends JFrame {
    private final User currentL3;
    private final IncidentDao incidentDao = new IncidentDao();

    private CardLayout cardLayout;
    private JPanel contentPanel;

    private JTable incidentsTable;
    private DefaultTableModel incidentsTableModel;

    private JTextArea txtProblem;
    private JTextArea txtBusinessImpact;
    private JTextArea txtTempSolution;
    private JTextArea txtPermSolution;
    private JLabel lblIncidentHeader;
    private JButton btnSaveSolution;

    private Incident selectedIncident;

    private static final String ASSIGNED_INCIDENTS = "ASSIGNED_INCIDENTS";
    private static final String INCIDENT_DETAIL = "INCIDENT_DETAIL";

    public L3SupportFrame(User l3User) {
        this.currentL3 = l3User;
        setTitle("Incident Management - L3 Support Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();

        loadAssignedIncidents();

        setVisible(true);
    }

    private void initUI() {
        // Top header bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("L3 Support Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(
                "Logged in as: " + currentL3.getFullName() + " (" + currentL3.getRole() + ")"
        );
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Left navigation (simple for L3)
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(30, 30, 60));
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JButton btnAssigned = createNavButton("📋 Assigned Incidents", new Color(70, 130, 180));
        btnAssigned.addActionListener(e -> showAssignedIncidentsView());

        JButton btnRefresh = createNavButton("🔄 Refresh", new Color(173, 216, 230));
        btnRefresh.addActionListener(e -> loadAssignedIncidents());

        JButton btnLogout = createNavButton("⏻ Logout", new Color(220, 20, 60));
        btnLogout.addActionListener(e -> {
            dispose();
            // here you can show LoginUI again if you want
        });

        navPanel.add(btnAssigned);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(btnRefresh);
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(btnLogout);

        add(navPanel, BorderLayout.WEST);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createAssignedIncidentsPanel(), ASSIGNED_INCIDENTS);
        contentPanel.add(createIncidentDetailPanel(), INCIDENT_DETAIL);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 2),
                        BorderFactory.createEmptyBorder(12, 15, 12, 15)
                )
        );
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private JPanel createAssignedIncidentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Assigned Incidents");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(25, 25, 112));

        panel.add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "ID", "Application", "Start Time", "Status",
                "Created By", "RCA Provided"
        };
        incidentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        incidentsTable = new JTable(incidentsTableModel);
        incidentsTable.setRowHeight(24);
        incidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        incidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(incidentsTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnOpen = createActionButton("Open Incident", new Color(60, 179, 113));
        btnOpen.addActionListener(e -> openSelectedIncident());

        bottomPanel.add(btnOpen);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIncidentDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblIncidentHeader = new JLabel("Incident Details");
        lblIncidentHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblIncidentHeader.setForeground(new Color(25, 25, 112));

        panel.add(lblIncidentHeader, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        txtProblem = createReadOnlyArea();
        txtBusinessImpact = createReadOnlyArea();
        txtTempSolution = createReadOnlyArea();
        txtPermSolution = new JTextArea(5, 40);
        txtPermSolution.setLineWrap(true);
        txtPermSolution.setWrapStyleWord(true);
        txtPermSolution.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPermSolution.setBorder(createTitledBorder("Permanent Solution (L3 to fill)"));

        centerPanel.add(wrapWithScroll(txtProblem, "Problem Statement"));
        centerPanel.add(wrapWithScroll(txtBusinessImpact, "Business Impact"));
        centerPanel.add(wrapWithScroll(txtTempSolution, "Temporary Solution"));
        centerPanel.add(new JScrollPane(txtPermSolution));

        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        btnSaveSolution = createActionButton("Save Solution & Mark RCA Provided", new Color(60, 179, 113));
        btnSaveSolution.addActionListener(e -> saveSolution());

        JButton btnBack = createActionButton("Back to List", new Color(173, 216, 230));
        btnBack.addActionListener(e -> showAssignedIncidentsView());

        bottomPanel.add(btnBack);
        bottomPanel.add(btnSaveSolution);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JTextArea createReadOnlyArea() {
        JTextArea area = new JTextArea(5, 40);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return area;
    }

    private JScrollPane wrapWithScroll(JTextArea area, String title) {
        area.setBorder(createTitledBorder(title));
        return new JScrollPane(area);
    }

    private Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title
        );
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                )
        );
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private void showAssignedIncidentsView() {
        cardLayout.show(contentPanel, ASSIGNED_INCIDENTS);
    }

    private void showIncidentDetailView() {
        cardLayout.show(contentPanel, INCIDENT_DETAIL);
    }

    private void loadAssignedIncidents() {
        incidentsTableModel.setRowCount(0);

        // reuse your incidentDao, but filter manually for assigned_to = current L3
        List<Incident> all = incidentDao.getAllIncidents();
        for (Incident incident : all) {
            if (incident.getAssignedTo() != null &&
                    incident.getAssignedTo() == currentL3.getId()) {

                Object[] row = {
                        incident.getId(),
                        incident.getApplicationName(),
                        incident.getIssueStartTime(),
                        incident.getStatus(),
                        incident.getCreatedByName() != null ? incident.getCreatedByName() : "Unknown",
                        incident.isRcaProvided() ? "Yes" : "No"
                };
                incidentsTableModel.addRow(row);
            }
        }
    }

    private void openSelectedIncident() {
        int row = incidentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an incident.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int incidentId = (int) incidentsTableModel.getValueAt(row, 0);
        selectedIncident = incidentDao.getIncidentById(incidentId);
        if (selectedIncident == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load incident details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        lblIncidentHeader.setText(
                "Incident #" + selectedIncident.getId() +
                        " - " + selectedIncident.getApplicationName()
        );

        txtProblem.setText(selectedIncident.getProblemStatement());
        txtBusinessImpact.setText(selectedIncident.getBusinessImpact());
        txtTempSolution.setText(
                selectedIncident.getTemporarySolution() != null
                        ? selectedIncident.getTemporarySolution()
                        : "No temporary solution recorded."
        );
        txtPermSolution.setText(
                selectedIncident.getPermanentSolution() != null
                        ? selectedIncident.getPermanentSolution()
                        : ""
        );

        showIncidentDetailView();
    }

    private void saveSolution() {
        if (selectedIncident == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No incident selected.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String perm = txtPermSolution.getText().trim();
        if (perm.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please write the permanent solution.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean ok1 = incidentDao.updatePermanentSolution(selectedIncident.getId(), perm);
        boolean ok2 = incidentDao.markRcaProvided(selectedIncident.getId());

        if (ok1 && ok2) {
            JOptionPane.showMessageDialog(
                    this,
                    "Solution saved and RCA marked as provided.\nIncident Manager can now review and publish RCA.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            selectedIncident.setPermanentSolution(perm);
            selectedIncident.setRcaProvided(true);

            showAssignedIncidentsView();
            loadAssignedIncidents();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save solution. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
