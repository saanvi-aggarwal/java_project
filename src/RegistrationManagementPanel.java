import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Registration Management screen: View all registrations in a JTable,
 * search by ID or name, filter by status, view participant details, and cancel registrations.
 */
public class RegistrationManagementPanel extends JPanel {

    private final EventService service;

    private DefaultTableModel tableModel;
    private JTable tblRegistrations;
    private JTextField txtSearch;
    private JComboBox<String> cmbStatusFilter;
    private JLabel lblCountStatus;

    public RegistrationManagementPanel() {
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
        add(UITheme.createHeader("Registration Management", "Search, filter, view participant information, and cancel event registrations"), BorderLayout.NORTH);

        // Center: Card containing Toolbar + Table + Action Buttons
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 14));

        // 1. Top Toolbar (Search, Filter, Actions)
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(UITheme.CARD_BG);

        // Left controls: Search + Filter
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftControls.setBackground(UITheme.CARD_BG);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_PRIMARY);
        leftControls.add(lblSearch);

        txtSearch = UITheme.createTextField(18);
        txtSearch.setToolTipText("Enter Registration ID, Name, or Email");
        leftControls.add(txtSearch);

        JLabel lblFilter = new JLabel("Status:");
        lblFilter.setFont(UITheme.FONT_BOLD);
        lblFilter.setForeground(UITheme.TEXT_PRIMARY);
        leftControls.add(lblFilter);

        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Confirmed", "Cancelled"});
        cmbStatusFilter.setFont(UITheme.FONT_REGULAR);
        cmbStatusFilter.setPreferredSize(new Dimension(120, 36));
        cmbStatusFilter.addActionListener(e -> applyFilter());
        leftControls.add(cmbStatusFilter);

        JButton btnSearch = UITheme.createPrimaryButton("Search");
        btnSearch.addActionListener(e -> applyFilter());
        leftControls.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatusFilter.setSelectedIndex(0);
            applyFilter();
        });
        leftControls.add(btnReset);

        toolbar.add(leftControls, BorderLayout.WEST);

        // Right controls: Action buttons (View Details, Cancel Registration)
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setBackground(UITheme.CARD_BG);

        JButton btnViewDetails = UITheme.createSecondaryButton("View Details");
        btnViewDetails.addActionListener(e -> handleViewDetails());
        rightControls.add(btnViewDetails);

        JButton btnCancel = UITheme.createDangerButton("✕ Cancel Registration");
        btnCancel.addActionListener(e -> handleCancelRegistration());
        rightControls.add(btnCancel);

        toolbar.add(rightControls, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        // 2. Table
        String[] columns = {"Reg ID", "Participant Name", "Email Address", "Phone", "Event ID", "Event Name", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblRegistrations = new JTable(tableModel);
        UITheme.styleTable(tblRegistrations);
        tblRegistrations.getColumnModel().getColumn(6).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblRegistrations.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblRegistrations.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblRegistrations.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblRegistrations.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblRegistrations.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblRegistrations.getColumnModel().getColumn(5).setPreferredWidth(170);
        tblRegistrations.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Double click to view details
        tblRegistrations.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleViewDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblRegistrations);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        // 3. Footer Bar: Record count info
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.CARD_BG);
        footer.setBorder(new EmptyBorder(6, 4, 0, 4));

        lblCountStatus = new JLabel("Showing 0 registrations");
        lblCountStatus.setFont(UITheme.FONT_SMALL);
        lblCountStatus.setForeground(UITheme.TEXT_MUTED);

        JLabel lblHint = new JLabel("Tip: Double-click a row to view full details | Select a confirmed registration and click Cancel to revoke");
        lblHint.setFont(UITheme.FONT_SMALL);
        lblHint.setForeground(UITheme.TEXT_MUTED);

        footer.add(lblCountStatus, BorderLayout.WEST);
        footer.add(lblHint, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    private void handleViewDetails() {
        int selectedRow = tblRegistrations.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a registration from the table to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = tableModel.getValueAt(selectedRow, 0).toString();
        Registration reg = service.searchRegistration(regId);
        if (reg == null) {
            JOptionPane.showMessageDialog(this, "Registration record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Event ev = service.findEvent(reg.getEventId());

        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("REGISTRATION DETAILS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: " + reg.getRegistrationId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        idLabel.setForeground(UITheme.BLUE_ACCENT);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        detailsCard.add(title);
        detailsCard.add(Box.createVerticalStrut(4));
        detailsCard.add(idLabel);
        detailsCard.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        detailsCard.add(sep);
        detailsCard.add(Box.createVerticalStrut(10));

        addDetailLine(detailsCard, "Participant Name:", reg.getParticipant().getName());
        addDetailLine(detailsCard, "Email Address:", reg.getParticipant().getEmail());
        addDetailLine(detailsCard, "Phone Number:", reg.getParticipant().getPhone());
        addDetailLine(detailsCard, "Event Name:", (ev != null) ? ev.getEventName() : "Unknown");
        addDetailLine(detailsCard, "Event ID:", String.valueOf(reg.getEventId()));
        if (ev != null) {
            addDetailLine(detailsCard, "Event Date:", ev.getDate());
            addDetailLine(detailsCard, "Event Venue:", ev.getVenue());
        }
        addDetailLine(detailsCard, "Registration Status:", reg.getStatus());

        JOptionPane.showMessageDialog(this, detailsCard, "Registration Details - " + regId, JOptionPane.PLAIN_MESSAGE);
    }

    private void addDetailLine(JPanel container, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_MUTED);

        JLabel val = new JLabel(value);
        val.setFont(UITheme.FONT_REGULAR);
        val.setForeground(UITheme.TEXT_PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);

        container.add(row);
        container.add(Box.createVerticalStrut(4));
    }

    private void handleCancelRegistration() {
        int selectedRow = tblRegistrations.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a registration from the table to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String regId = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();

        if ("Cancelled".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Registration " + regId + " is already cancelled.", "Already Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Registration reg = service.searchRegistration(regId);
        String participantName = (reg != null) ? reg.getParticipant().getName() : "Unknown";
        Event ev = (reg != null) ? service.findEvent(reg.getEventId()) : null;
        String eventName = (ev != null) ? ev.getEventName() : "Event #" + (reg != null ? reg.getEventId() : "");

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this registration?\n\n" +
                        "Registration ID: " + regId + "\n" +
                        "Participant: " + participantName + "\n" +
                        "Event: " + eventName + "\n\n" +
                        "This will free up 1 seat for the event and update records permanently.",
                "Confirm Registration Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                service.cancelRegistration(regId);
                JOptionPane.showMessageDialog(
                        this,
                        "Registration " + regId + " has been cancelled successfully.\n1 seat has been returned to " + eventName + ".",
                        "Cancellation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cancelling registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        String status = (String) cmbStatusFilter.getSelectedItem();

        tableModel.setRowCount(0);
        List<Registration> list = service.filterRegistrations(keyword, status);

        for (Registration reg : list) {
            Event ev = service.findEvent(reg.getEventId());
            String eventName = (ev != null) ? ev.getEventName() : ("Event #" + reg.getEventId());

            tableModel.addRow(new Object[]{
                    reg.getRegistrationId(),
                    reg.getParticipant().getName(),
                    reg.getParticipant().getEmail(),
                    reg.getParticipant().getPhone(),
                    reg.getEventId(),
                    eventName,
                    reg.getStatus()
            });
        }

        lblCountStatus.setText("Showing " + list.size() + " of " + service.getRegistrations().size() + " registrations");
    }

    public void refreshData() {
        applyFilter();
    }
}
