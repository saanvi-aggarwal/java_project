import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Reports screen: Comprehensive event-wise registration statistics,
 * capacity analysis, visual occupancy rate indicators, and text report export.
 */
public class ReportPanel extends JPanel {

    private final EventService service;

    private DefaultTableModel tableModel;
    private JTable tblReports;

    // Summary Labels
    private JLabel lblTotalEvents;
    private JLabel lblTotalCapacity;
    private JLabel lblTotalConfirmed;
    private JLabel lblTotalAvailable;
    private JLabel lblOverallOccupancy;
    private JProgressBar progressOverall;

    public ReportPanel() {
        this.service = EventService.getInstance();

        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        service.addChangeListener(this::refreshData);
        refreshData();
    }

    private void initUI() {
        // Header
        add(UITheme.createHeader("System Reports & Analytics", "Event-wise capacity utilization, confirmed registrations, and overall occupancy distribution"), BorderLayout.NORTH);

        // Center: Summary Banner + Analytics Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.BG_MAIN);

        centerPanel.add(createSummaryBanner(), BorderLayout.NORTH);
        centerPanel.add(createTableCard(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createSummaryBanner() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(16, 12));

        // Top Title
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel("Aggregate System Summary");
        lblTitle.setFont(UITheme.FONT_SECTION);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UITheme.CARD_BG);

        JButton btnExport = UITheme.createPrimaryButton("Export Report (.txt)");
        btnExport.addActionListener(e -> handleExportReport());

        JButton btnViewText = UITheme.createSecondaryButton("View Full Report");
        btnViewText.addActionListener(e -> handleViewReportDialog());

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> refreshData());

        btnPanel.add(btnExport);
        btnPanel.add(btnViewText);
        btnPanel.add(btnRefresh);

        headerRow.add(lblTitle, BorderLayout.WEST);
        headerRow.add(btnPanel, BorderLayout.EAST);
        card.add(headerRow, BorderLayout.NORTH);

        // Metrics Grid (5 Columns)
        JPanel grid = new JPanel(new GridLayout(1, 5, 12, 0));
        grid.setBackground(UITheme.CARD_BG);

        lblTotalEvents = new JLabel("0");
        lblTotalCapacity = new JLabel("0");
        lblTotalConfirmed = new JLabel("0");
        lblTotalAvailable = new JLabel("0");
        lblOverallOccupancy = new JLabel("0.0%");

        grid.add(createMiniMetric("TOTAL EVENTS", lblTotalEvents, UITheme.BLUE_ACCENT));
        grid.add(createMiniMetric("TOTAL CAPACITY", lblTotalCapacity, UITheme.TEXT_PRIMARY));
        grid.add(createMiniMetric("CONFIRMED", lblTotalConfirmed, new Color(16, 185, 129)));
        grid.add(createMiniMetric("AVAILABLE SEATS", lblTotalAvailable, new Color(139, 92, 246)));

        // Occupancy Mini Card with progress bar
        JPanel pnlOcc = new JPanel();
        pnlOcc.setLayout(new BoxLayout(pnlOcc, BoxLayout.Y_AXIS));
        pnlOcc.setBackground(new Color(248, 250, 252));
        pnlOcc.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblOccTitle = new JLabel("OVERALL OCCUPANCY");
        lblOccTitle.setFont(UITheme.FONT_LABEL);
        lblOccTitle.setForeground(UITheme.TEXT_MUTED);

        lblOverallOccupancy.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblOverallOccupancy.setForeground(UITheme.TEXT_PRIMARY);

        progressOverall = new JProgressBar(0, 100);
        progressOverall.setStringPainted(false);
        progressOverall.setPreferredSize(new Dimension(80, 6));
        progressOverall.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        progressOverall.setForeground(UITheme.BLUE_ACCENT);

        pnlOcc.add(lblOccTitle);
        pnlOcc.add(Box.createVerticalStrut(4));
        pnlOcc.add(lblOverallOccupancy);
        pnlOcc.add(Box.createVerticalStrut(6));
        pnlOcc.add(progressOverall);

        grid.add(pnlOcc);

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMiniMetric(String label, JLabel valueLabel, Color accentColor) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(248, 250, 252));
        box.setBorder(new CompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_LABEL);
        lbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        box.add(lbl);
        box.add(Box.createVerticalStrut(4));
        box.add(valueLabel);
        return box;
    }

    private JPanel createTableCard() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 12));

        JLabel lblTitle = new JLabel("Event-Wise Capacity & Occupancy Breakdown");
        lblTitle.setFont(UITheme.FONT_SECTION);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        card.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {
                "Event ID", "Event Name", "Date", "Venue", "Total Seats", "Confirmed", "Available Seats", "Occupancy (%)", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblReports = new JTable(tableModel);
        UITheme.styleTable(tblReports);

        tblReports.getColumnModel().getColumn(7).setCellRenderer(new UITheme.ProgressBarRenderer());
        tblReports.getColumnModel().getColumn(8).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblReports.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblReports.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblReports.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblReports.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblReports.getColumnModel().getColumn(4).setPreferredWidth(75);
        tblReports.getColumnModel().getColumn(5).setPreferredWidth(75);
        tblReports.getColumnModel().getColumn(6).setPreferredWidth(95);
        tblReports.getColumnModel().getColumn(7).setPreferredWidth(140);
        tblReports.getColumnModel().getColumn(8).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tblReports);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        int totalEvents = service.getTotalEvents();
        int totalCapacity = service.getTotalCapacity();
        int totalConfirmed = service.getTotalConfirmedRegistrations();
        int totalAvailable = service.getTotalAvailableSeats();
        double occupancy = service.getOverallOccupancyPercentage();

        lblTotalEvents.setText(String.valueOf(totalEvents));
        lblTotalCapacity.setText(String.valueOf(totalCapacity));
        lblTotalConfirmed.setText(String.valueOf(totalConfirmed));
        lblTotalAvailable.setText(String.valueOf(totalAvailable));
        lblOverallOccupancy.setText(String.format("%.2f%%", occupancy));

        int occInt = (int) Math.round(occupancy);
        progressOverall.setValue(Math.min(100, Math.max(0, occInt)));

        tableModel.setRowCount(0);
        List<Event> events = service.getEvents();

        for (Event ev : events) {
            int confirmed = service.getRegistrationCount(ev.getEventId());
            int available = service.getAvailableSeats(ev.getEventId());
            double occ = service.getEventOccupancyPercentage(ev.getEventId());

            String status;
            if (available == 0) {
                status = "Sold Out";
            } else if (occ >= 75) {
                status = "High Demand";
            } else {
                status = "Open";
            }

            tableModel.addRow(new Object[]{
                    ev.getEventId(),
                    ev.getEventName(),
                    ev.getDate(),
                    ev.getVenue(),
                    ev.getCapacity(),
                    confirmed,
                    available,
                    occ,
                    status
            });
        }
    }

    private String buildReportText() {
        StringBuilder sb = new StringBuilder();
        String dateStr = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        sb.append("========================================================================================\n");
        sb.append("                       EVENT REGISTRATION SYSTEM - OFFICIAL REPORT                       \n");
        sb.append("========================================================================================\n");
        sb.append("Generated On: ").append(dateStr).append("\n\n");

        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-8s | %-26s | %-10s | %-6s | %-9s | %-9s | %-10s\n",
                "Event ID", "Event Name", "Date", "Seats", "Confirmed", "Available", "Occupancy"));
        sb.append("----------------------------------------------------------------------------------------\n");

        List<Event> events = service.getEvents();
        for (Event ev : events) {
            int confirmed = service.getRegistrationCount(ev.getEventId());
            int available = service.getAvailableSeats(ev.getEventId());
            double occ = service.getEventOccupancyPercentage(ev.getEventId());

            sb.append(String.format("%-8d | %-26s | %-10s | %-6d | %-9d | %-9d | %6.2f%%\n",
                    ev.getEventId(),
                    truncate(ev.getEventName(), 26),
                    ev.getDate(),
                    ev.getCapacity(),
                    confirmed,
                    available,
                    occ
            ));
        }

        sb.append("----------------------------------------------------------------------------------------\n\n");
        sb.append("SYSTEM-WIDE SUMMARY:\n");
        sb.append("  Total Events Scheduled     : ").append(service.getTotalEvents()).append("\n");
        sb.append("  Total Seating Capacity     : ").append(service.getTotalCapacity()).append("\n");
        sb.append("  Total Confirmed Bookings   : ").append(service.getTotalConfirmedRegistrations()).append("\n");
        sb.append("  Total Available Seats      : ").append(service.getTotalAvailableSeats()).append("\n");
        sb.append(String.format("  Overall System Occupancy   : %.2f%%\n", service.getOverallOccupancyPercentage()));
        sb.append("========================================================================================\n");

        return sb.toString();
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    private void handleViewReportDialog() {
        String report = buildReportText();
        JTextArea textArea = new JTextArea(report);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(750, 420));

        JOptionPane.showMessageDialog(this, scrollPane, "Event Registration System Report", JOptionPane.PLAIN_MESSAGE);
    }

    private void handleExportReport() {
        String report = buildReportText();
        File exportDir = new File("reports");
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        String fileName = "reports/Event_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
        File file = new File(fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(report);
            JOptionPane.showMessageDialog(
                    this,
                    "Report exported successfully to:\n" + file.getAbsolutePath(),
                    "Report Exported",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to export report: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
