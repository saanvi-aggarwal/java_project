import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Event Management screen: View existing events in a responsive JTable,
 * search/filter events, and create new events with complete input validation.
 */
public class EventPanel extends JPanel {

    private final EventService service;

    // Table
    private DefaultTableModel tableModel;
    private JTable tblEvents;
    private JTextField txtSearch;

    // Form Fields
    private JTextField txtName;
    private JTextField txtDate;
    private JTextField txtVenue;
    private JTextField txtCapacity;
    private JLabel lblNextEventId;

    public EventPanel() {
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
        add(UITheme.createHeader("Event Management", "View scheduled events, check real-time seat availability, and create new events"), BorderLayout.NORTH);

        // Split Layout: Left Table (65%) | Right Creation Form (35%)
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(UITheme.BG_MAIN);

        contentPanel.add(createTableSection(), BorderLayout.CENTER);
        contentPanel.add(createFormSection(), BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTableSection() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 12));

        // Top Toolbar: Search + Count + Refresh
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(UITheme.CARD_BG);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setBackground(UITheme.CARD_BG);

        JLabel lblSearch = new JLabel("Filter Events:");
        lblSearch.setFont(UITheme.FONT_BOLD);
        lblSearch.setForeground(UITheme.TEXT_PRIMARY);
        searchPanel.add(lblSearch);

        txtSearch = UITheme.createTextField(16);
        txtSearch.setToolTipText("Filter by name, venue, or event ID");
        searchPanel.add(txtSearch);

        JButton btnSearch = UITheme.createPrimaryButton("Search");
        btnSearch.addActionListener(e -> applyFilter());
        searchPanel.add(btnSearch);

        JButton btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            applyFilter();
        });
        searchPanel.add(btnReset);

        toolbar.add(searchPanel, BorderLayout.WEST);

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> refreshData());
        toolbar.add(btnRefresh, BorderLayout.EAST);

        card.add(toolbar, BorderLayout.NORTH);

        // Events Table
        String[] columns = {"ID", "Event Name", "Date", "Venue", "Capacity", "Registered", "Available", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblEvents = new JTable(tableModel);
        UITheme.styleTable(tblEvents);
        tblEvents.getColumnModel().getColumn(7).setCellRenderer(new UITheme.StatusBadgeRenderer());

        tblEvents.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblEvents.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblEvents.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblEvents.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblEvents.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblEvents.getColumnModel().getColumn(5).setPreferredWidth(75);
        tblEvents.getColumnModel().getColumn(6).setPreferredWidth(75);
        tblEvents.getColumnModel().getColumn(7).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tblEvents);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFormSection() {
        JPanel card = UITheme.createCard();
        card.setPreferredSize(new Dimension(340, 0));
        card.setLayout(new BorderLayout(0, 16));

        // Form Title & Next ID preview
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UITheme.CARD_BG);

        JLabel lblFormTitle = new JLabel("Create New Event");
        lblFormTitle.setFont(UITheme.FONT_SECTION);
        lblFormTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblNextEventId = new JLabel("Next Assigned Event ID: #" + service.generateEventId());
        lblNextEventId.setFont(UITheme.FONT_SMALL);
        lblNextEventId.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(lblFormTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblNextEventId);
        card.add(headerPanel, BorderLayout.NORTH);

        // Inputs Panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(UITheme.CARD_BG);

        txtName = UITheme.createTextField(20);
        txtDate = UITheme.createTextField(20);
        txtVenue = UITheme.createTextField(20);
        txtCapacity = UITheme.createTextField(20);

        addFormField(fieldsPanel, "Event Name *", txtName, "e.g. Cloud Computing Bootcamp");
        addFormField(fieldsPanel, "Date (DD-MM-YYYY) *", txtDate, "e.g. 15-01-2027");
        addFormField(fieldsPanel, "Venue / Location *", txtVenue, "e.g. Auditorium Hall B");
        addFormField(fieldsPanel, "Maximum Capacity *", txtCapacity, "e.g. 100");

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel actionsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        actionsPanel.setBackground(UITheme.CARD_BG);

        JButton btnSubmit = UITheme.createPrimaryButton("+ Save & Add Event");
        btnSubmit.addActionListener(e -> handleAddEvent());

        JButton btnClear = UITheme.createSecondaryButton("Clear Inputs");
        btnClear.addActionListener(e -> clearForm());

        actionsPanel.add(btnSubmit);
        actionsPanel.add(btnClear);

        card.add(actionsPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addFormField(JPanel container, String labelText, JTextField field, String placeholder) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_LABEL);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setToolTipText(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        container.add(lbl);
        container.add(Box.createVerticalStrut(4));
        container.add(field);
        container.add(Box.createVerticalStrut(12));
    }

    private void handleAddEvent() {
        String name = txtName.getText().trim();
        String date = txtDate.getText().trim();
        String venue = txtVenue.getText().trim();
        String capacityStr = txtCapacity.getText().trim();

        // 1. Empty field checks
        if (name.isEmpty() || date.isEmpty() || venue.isEmpty() || capacityStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "All fields are mandatory. Please fill in all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. Pipe delimiter check
        if (name.contains("|") || date.contains("|") || venue.contains("|") || capacityStr.contains("|")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Input cannot contain the pipe character ('|').",
                    "Invalid Character",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 3. Capacity parsing check
        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Capacity must be a positive integer greater than zero.",
                        "Invalid Capacity",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Capacity must be a valid whole number (e.g., 50).",
                    "Invalid Number",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 4. Save through EventService
        try {
            Event newEvent = service.addEvent(name, date, venue, capacity);
            JOptionPane.showMessageDialog(
                    this,
                    "Event added successfully!\n\n" +
                            "Assigned Event ID: " + newEvent.getEventId() + "\n" +
                            "Event Name: " + newEvent.getEventName() + "\n" +
                            "Date: " + newEvent.getDate() + "\n" +
                            "Venue: " + newEvent.getVenue() + "\n" +
                            "Capacity: " + newEvent.getCapacity() + " seats\n\n" +
                            "Data saved to data/events.txt.",
                    "Event Created",
                    JOptionPane.INFORMATION_MESSAGE
            );
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to create event: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtDate.setText("");
        txtVenue.setText("");
        txtCapacity.setText("");
        lblNextEventId.setText("Next Assigned Event ID: #" + service.generateEventId());
    }

    private void applyFilter() {
        String filter = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        List<Event> events = service.getEvents();

        for (Event ev : events) {
            if (filter.isEmpty() ||
                    ev.getEventName().toLowerCase().contains(filter) ||
                    ev.getVenue().toLowerCase().contains(filter) ||
                    String.valueOf(ev.getEventId()).contains(filter) ||
                    ev.getDate().contains(filter)) {

                int booked = service.getRegistrationCount(ev.getEventId());
                int avail = service.getAvailableSeats(ev.getEventId());
                String status = (avail > 0) ? "Open" : "Sold Out";

                tableModel.addRow(new Object[]{
                        ev.getEventId(),
                        ev.getEventName(),
                        ev.getDate(),
                        ev.getVenue(),
                        ev.getCapacity(),
                        booked,
                        avail,
                        status
                });
            }
        }
    }

    public void refreshData() {
        applyFilter();
        lblNextEventId.setText("Next Assigned Event ID: #" + service.generateEventId());
    }
}
