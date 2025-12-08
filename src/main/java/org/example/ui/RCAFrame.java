package org.example.ui;

import org.example.dao.IncidentDao;
import org.example.database.DatabaseConnection;
import org.example.model.Incident;
import org.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RCAFrame extends JFrame {

    private final User currentIM;
    private final IncidentDao incidentDao = new IncidentDao();

    private JTable incidentsTable;
    private DefaultTableModel tableModel;

    private JTextArea txtRootCause;
    private JTextArea txtPermanentFix;
    private JTextArea txtPreventive;

    private Incident selectedIncident;

    public RCAFrame (User imUser) {
        this.currentIM = imUser;

        setTitle("Publish RCA - Incident Manager");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadIncidentsNeedingPublish();

        setVisible(true);
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 112));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lbl = new JLabel("RCA Review & Publish");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);

        JLabel userLbl = new JLabel("Incident Manager: " + currentIM.getFullName());
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLbl.setForeground(Color.WHITE);
        userLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(lbl, BorderLayout.WEST);
        header.add(userLbl, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Main center: list + detail
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4);

        splitPane.setTopComponent(createIncidentsPanel());
        splitPane.setBottomComponent(createRcaFormPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createIncidentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Incidents with RCA Provided (Pending Publish)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(25, 25, 112));

        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
                "ID", "Application", "Start Time", "Status",
                "L3 Assigned", "RCA Provided"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        incidentsTable = new JTable(tableModel);
        incidentsTable.setRowHeight(24);
        incidentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        incidentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(incidentsTable);

        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton btnView = new JButton("Load RCA Details");
        styleActionButton(btnView, new Color(70, 130, 180));
        btnView.addActionListener(e -> loadSelectedIncident());

        JButton btnRefresh = new JButton("Refresh");
        styleActionButton(btnRefresh, new Color(173, 216, 230));
        btnRefresh.addActionListener(e -> loadIncidentsNeedingPublish());

        btnPanel.add(btnRefresh);
        btnPanel.add(btnView);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRcaFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        formPanel.setBackground(Color.WHITE);

        txtRootCause = createAreaWithTitle("Root Cause");
        txtPermanentFix = createAreaWithTitle("Permanent Fix (from L3 solution)");
        txtPreventive = createAreaWithTitle("Preventive Measures");

        formPanel.add(new JScrollPane(txtRootCause));
        formPanel.add(new JScrollPane(txtPermanentFix));
        formPanel.add(new JScrollPane(txtPreventive));

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton btnPublish = new JButton("Publish RCA");
        styleActionButton(btnPublish, new Color(60, 179, 113));
        btnPublish.addActionListener(e -> publishRca());

        JButton btnClose = new JButton("Close");
        styleActionButton(btnClose, new Color(220, 20, 60));
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnClose);
        btnPanel.add(btnPublish);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JTextArea createAreaWithTitle(String title) {
        JTextArea area = new JTextArea(6, 25);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        title
                )
        );
        return area;
    }

    private void styleActionButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1),
                        BorderFactory.createEmptyBorder(8, 18, 8, 18)
                )
        );
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
    }

    private void loadIncidentsNeedingPublish() {
        tableModel.setRowCount(0);
        List<Incident> all = incidentDao.getAllIncidents();
        for (Incident i : all) {
            if (i.isRcaProvided()) {
                Object[] row = {
                        i.getId(),
                        i.getApplicationName(),
                        i.getIssueStartTime(),
                        i.getStatus(),
                        i.getAssignedToName() != null ? i.getAssignedToName() : "Not Assigned",
                        "Yes"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void loadSelectedIncident() {
        int row = incidentsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an incident from the table.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int incidentId = (int) tableModel.getValueAt(row, 0);
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

        // Prefill fields: permanent fix from incident, root cause empty for IM to summarise
        txtRootCause.setText("");
        txtPermanentFix.setText(
                selectedIncident.getPermanentSolution() != null
                        ? selectedIncident.getPermanentSolution()
                        : ""
        );
        txtPreventive.setText("");
    }

    private void publishRca() {
        if (selectedIncident == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No incident selected. Load one first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String root = txtRootCause.getText().trim();
        String perm = txtPermanentFix.getText().trim();
        String prev = txtPreventive.getText().trim();

        if (root.isEmpty() || perm.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Root Cause and Permanent Fix are mandatory.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Insert into rca table
        String sql = """
            INSERT OR REPLACE INTO rca
            (incident_id, root_cause, permanent_fix, preventive_measures,
             created_by, created_at, published, published_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 1, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedIncident.getId());
            pstmt.setString(2, root);
            pstmt.setString(3, perm);
            pstmt.setString(4, prev);
            pstmt.setInt(5, currentIM.getId());

            String publishedDate = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(6, publishedDate);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Optionally close incident when RCA published
                incidentDao.updateIncidentStatus(
                        selectedIncident.getId(),
                        "CLOSED",
                        publishedDate
                );

                JOptionPane.showMessageDialog(
                        this,
                        "RCA published successfully for Incident #" + selectedIncident.getId(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                selectedIncident = null;
                txtRootCause.setText("");
                txtPermanentFix.setText("");
                txtPreventive.setText("");

                loadIncidentsNeedingPublish();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to publish RCA.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while publishing RCA: " + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
