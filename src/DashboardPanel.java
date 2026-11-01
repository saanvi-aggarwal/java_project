import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard screen: Displays key performance indicator (KPI) metric cards,
 * quick navigation buttons, and recent activity snapshots.
 */
public class DashboardPanel extends JPanel {

    private final MainGUI mainGUI;
    private final EventService service;

    // KPI Metric labels
    private JLabel lblTotalEventsValue;
    private JLabel lblTotalRegistrationsValue;
    private JLabel lblAvailableSeatsValue;
    private JLabel lblOccupancyValue;
    private JProgressBar progressOccupancy;

    // Tables
    private DefaultTableModel eventsTableModel;
    private JTable tblEvents;
    private DefaultTableModel recentRegTableModel;
    private JTable tblRecentReg;

    public DashboardPanel(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
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
        add(UITheme.createHeader("Dashboard Overview", "Real-time summary of events, registrations, and capacity distribution"), BorderLayout.NORTH);

        // Center Content: KPI Cards + Quick Navigation + Summary Tables
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UITheme.BG_MAIN);

        // 1. KPI Cards Row (4 Cards)
        centerPanel.add(createKpiGrid());
        centerPanel.add(Box.createVerticalStrut(18));

        // 2. Quick Action Toolbar
        centerPanel.add(createQuickActionsBar());
        centerPanel.add(Box.createVerticalStrut(18));

        // 3. Two Data Overview Cards side-by-side
        centerPanel.add(createOverviewGrids());

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.BG_MAIN);
        scrollPane.getViewport().setBackground(UITheme.BG_MAIN);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createKpiGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setBackground(UITheme.BG_MAIN);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Total Events
        lblTotalEventsValue = new JLabel("0");
        grid.add(createKpiCard("TOTAL EVENTS", lblTotalEventsValue, "Scheduled events", UITheme.BLUE_ACCENT));

        // Total Confirmed Registrations
        lblTotalRegistrationsValue = new JLabel("0");
        grid.add(createKpiCard("REGISTRATIONS", lblTotalRegistrationsValue, "Confirmed attendees", new Color(16, 185, 129)));

        // Available Seats
        lblAvailableSeatsValue = new JLabel("0");
        grid.add(createKpiCard("AVAILABLE SEATS", lblAvailableSeatsValue, "Remaining capacity", new Color(139, 92, 246)));

        // Occupancy Rate Card
        lblOccupancyValue = new JLabel("0.0%");
        progressOccupancy = new JProgressBar(0, 100);
        progressOccupancy.setStringPainted(false);
        progressOccupancy.setPreferredSize(new Dimension(100, 6));
        progressOccupancy.setForeground(UITheme.BLUE_ACCENT);
        grid.add(createOccupancyKpiCard("OCCUPANCY RATE", lblOccupancyValue, progressOccupancy));

        return grid;
    }

    private JPanel createKpiCard(String labelText, JLabel valueLabel, String subtitleText, Color accentColor) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout());

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setBackground(UITheme.CARD_BG);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_LABEL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        headerRow.add(lbl, BorderLayout.WEST);

        // Accent bar on top
        card.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
                new CompoundBorder(
                        new LineBorder(UITheme.BORDER_COLOR, 1, true),
                        new EmptyBorder(14, 18, 14, 18)
                )
        ));

        valueLabel.setFont(UITheme.FONT_METRIC);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel(subtitleText);
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.CARD_BG);
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(sub);

        card.add(headerRow, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createOccupancyKpiCard(String labelText, JLabel valueLabel, JProgressBar bar) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout());

        card.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(245, 158, 11)),
                new CompoundBorder(
                        new LineBorder(UITheme.BORDER_COLOR, 1, true),
                        new EmptyBorder(14, 18, 14, 18)
                )
        ));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_LABEL);
        lbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_METRIC);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.CARD_BG);
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(bar);

        card.add(lbl, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createQuickActionsBar() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(16, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel lbl = new JLabel("Quick Actions:");
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        card.add(lbl, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UITheme.CARD_BG);

        JButton btnAddEvent = UITheme.createPrimaryButton("+ Add New Event");
        btnAddEvent.addActionListener(e -> mainGUI.showScreen(MainGUI.SCREEN_EVENTS));

        JButton btnRegister = UITheme.createPrimaryButton("Register Participant");
        btnRegister.setBackground(new Color(16, 185, 129));
        btnRegister.addActionListener(e -> mainGUI.showScreen(MainGUI.SCREEN_REGISTRATION));

        JButton btnManage = UITheme.createSecondaryButton("Manage Registrations");
        btnManage.addActionListener(e -> mainGUI.showScreen(MainGUI.SCREEN_MANAGEMENT));

        JButton btnReports = UITheme.createSecondaryButton("View Reports");
        btnReports.addActionListener(e -> mainGUI.showScreen(MainGUI.SCREEN_REPORTS));

        btnPanel.add(btnAddEvent);
        btnPanel.add(btnRegister);
        btnPanel.add(btnManage);
        btnPanel.add(btnReports);

        card.add(btnPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createOverviewGrids() {
        JPanel container = new JPanel(new GridLayout(1, 2, 16, 0));
        container.setBackground(UITheme.BG_MAIN);
        container.setPreferredSize(new Dimension(900, 340));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // 1. Left Card: Events Capacity Snapshot
        JPanel pnlEvents = UITheme.createCard();
        pnlEvents.setLayout(new BorderLayout(0, 10));

        JPanel pnlEventsHeader = new JPanel(new BorderLayout());
        pnlEventsHeader.setBackground(UITheme.CARD_BG);
        JLabel lblEventsTitle = new JLabel("Event Capacity Snapshot");
        lblEventsTitle.setFont(UITheme.FONT_SECTION);
        lblEventsTitle.setForeground(UITheme.TEXT_PRIMARY);
        JButton btnViewAllEvents = UITheme.createSecondaryButton("View All");
        btnViewAllEvents.setFont(UITheme.FONT_SMALL);
        btnViewAllEvents.addActionListener(e -> mainGUI.showScreen(MainGUI.SCREEN_EVENTS));
        pnlEventsHeader.add(lblEventsTitle, BorderLayout.WEST);
        pnlEventsHeader.add(btnViewAllEvents, BorderLayout.EAST);
        pnlEvents.add(pnlEventsHeader, BorderLayout.NORTH);

        String[] eventCols = {"Event Name", "Date", "Capacity", "Booked", "Available", "Status"};
        eventsTableModel = new DefaultTableModel(eventCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblEvents = new JTable(eventsTableModel);
        UITheme.styleTable(tblEvents);
        tblEvents.getColumnModel().getColumn(5).setCellRenderer(new UITheme.StatusBadgeRenderer());
        tblEvents.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblEvents.getColumnModel().getColumn(1).setPreferredWidth(80);

        JScrollPane scrollEvents = new JScrollPane(tblEvents);
        scrollEvents.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        pnlEvents.add(scrollEvents, BorderLayout.CENTER);

        // 2. Right Card: Recent Registrations
        JPanel pnlRegs = UITheme.createCard();
        pnlRegs.setLayout(new BorderLayout(0, 10));

        JPanel pnlRegsHeader = new JPanel(new BorderLayout());
        pnlRegsHeader.setBackground(UITheme.CARD_BG);
        JLabel lblRegsTitle = new JLabel("Recent Registrations");
        lblRegsTitle.setFont(UITheme.FONT_SECTION);
        lblRegsTitle.setForeground(UITheme.TEXT_PRIMARY);
        JButton btnViewAllRegs = UITheme.createSecondaryButton("Manage");
        btnViewAllRegs.setFont(UITheme.FONT_SMALL);
        btnViewAllRegs.addActionListener(e -> mainGUI.showScreen(MainGUI.SCREEN_MANAGEMENT));
        pnlRegsHeader.add(lblRegsTitle, BorderLayout.WEST);
        pnlRegsHeader.add(btnViewAllRegs, BorderLayout.EAST);
        pnlRegs.add(pnlRegsHeader, BorderLayout.NORTH);

        String[] regCols = {"Reg ID", "Participant", "Event", "Status"};
        recentRegTableModel = new DefaultTableModel(regCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRecentReg = new JTable(recentRegTableModel);
        UITheme.styleTable(tblRecentReg);
        tblRecentReg.getColumnModel().getColumn(3).setCellRenderer(new UITheme.StatusBadgeRenderer());
        tblRecentReg.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblRecentReg.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblRecentReg.getColumnModel().getColumn(2).setPreferredWidth(140);

        JScrollPane scrollRegs = new JScrollPane(tblRecentReg);
        scrollRegs.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        pnlRegs.add(scrollRegs, BorderLayout.CENTER);

        container.add(pnlEvents);
        container.add(pnlRegs);

        return container;
    }

    public void refreshData() {
        // Update Metrics
        int totalEvents = service.getTotalEvents();
        int totalConfirmed = service.getTotalConfirmedRegistrations();
        int availableSeats = service.getTotalAvailableSeats();
        double occupancy = service.getOverallOccupancyPercentage();

        lblTotalEventsValue.setText(String.valueOf(totalEvents));
        lblTotalRegistrationsValue.setText(String.valueOf(totalConfirmed));
        lblAvailableSeatsValue.setText(String.valueOf(availableSeats));
        lblOccupancyValue.setText(String.format("%.1f%%", occupancy));

        int occInt = (int) Math.round(occupancy);
        progressOccupancy.setValue(Math.min(100, Math.max(0, occInt)));

        // Update Events Snapshot Table
        eventsTableModel.setRowCount(0);
        List<Event> events = service.getEvents();
        for (Event ev : events) {
            int booked = service.getRegistrationCount(ev.getEventId());
            int avail = service.getAvailableSeats(ev.getEventId());
            String status = (avail > 0) ? "Open" : "Sold Out";
            eventsTableModel.addRow(new Object[]{
                    ev.getEventName(),
                    ev.getDate(),
                    ev.getCapacity(),
                    booked,
                    avail,
                    status
            });
        }

        // Update Recent Registrations Table (display latest up to 10 in reverse order)
        recentRegTableModel.setRowCount(0);
        List<Registration> registrations = service.getRegistrations();
        int count = 0;
        for (int i = registrations.size() - 1; i >= 0 && count < 10; i--) {
            Registration reg = registrations.get(i);
            Event ev = service.findEvent(reg.getEventId());
            String eventName = (ev != null) ? ev.getEventName() : ("Event #" + reg.getEventId());
            recentRegTableModel.addRow(new Object[]{
                    reg.getRegistrationId(),
                    reg.getParticipant().getName(),
                    eventName,
                    reg.getStatus()
            });
            count++;
        }
    }
}
