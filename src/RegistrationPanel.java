import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Participant Registration screen: Select an event, fill participant details,
 * view real-time seating availability, and register with duplicate and capacity validation.
 */
public class RegistrationPanel extends JPanel {

    private final EventService service;

    // Form inputs
    private JComboBox<EventComboItem> cmbEvents;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;

    // Live Event Info Card components
    private JLabel lblDetailName;
    private JLabel lblDetailId;
    private JLabel lblDetailDate;
    private JLabel lblDetailVenue;
    private JLabel lblDetailCapacity;
    private JLabel lblDetailBooked;
    private JLabel lblDetailAvailable;
    private JLabel lblDetailStatus;
    private JProgressBar progressDetailOccupancy;

    public RegistrationPanel() {
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
        add(UITheme.createHeader("Participant Registration", "Register attendees for upcoming events with automated capacity and duplicate validation"), BorderLayout.NORTH);

        // Content: Two-column responsive layout (Left Form | Right Live Info Card)
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        contentGrid.setBackground(UITheme.BG_MAIN);

        contentGrid.add(createRegistrationFormCard());
        contentGrid.add(createEventInfoCard());

        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createRegistrationFormCard() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));

        // Form Title
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel("Attendee Registration Form");
        lblTitle.setFont(UITheme.FONT_SECTION);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Fill in all details below to generate a confirmed registration.");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblSub);
        card.add(titlePanel, BorderLayout.NORTH);

        // Form Fields
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(UITheme.CARD_BG);

        // Event Combo Box
        JLabel lblEvent = new JLabel("Select Event *");
        lblEvent.setFont(UITheme.FONT_LABEL);
        lblEvent.setForeground(UITheme.TEXT_PRIMARY);

        cmbEvents = new JComboBox<>();
        cmbEvents.setFont(UITheme.FONT_REGULAR);
        cmbEvents.setPreferredSize(new Dimension(300, 36));
        cmbEvents.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbEvents.addActionListener(e -> updateSelectedEventInfo());

        fieldsPanel.add(lblEvent);
        fieldsPanel.add(Box.createVerticalStrut(4));
        fieldsPanel.add(cmbEvents);
        fieldsPanel.add(Box.createVerticalStrut(14));

        // Participant Name
        txtName = UITheme.createTextField(25);
        addFormField(fieldsPanel, "Participant Full Name *", txtName, "e.g. Saanvi Aggarwal");

        // Email Address
        txtEmail = UITheme.createTextField(25);
        addFormField(fieldsPanel, "Email Address *", txtEmail, "e.g. saanvi@gmail.com");

        // Phone Number
        txtPhone = UITheme.createTextField(25);
        addFormField(fieldsPanel, "Phone Number (10 digits) *", txtPhone, "e.g. 9876543210");

        card.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        actionsPanel.setBackground(UITheme.CARD_BG);

        JButton btnRegister = UITheme.createPrimaryButton("Confirm & Register");
        btnRegister.setBackground(new Color(16, 185, 129));
        btnRegister.addActionListener(e -> handleRegistration());

        JButton btnClear = UITheme.createSecondaryButton("Reset Form");
        btnClear.addActionListener(e -> clearForm());

        actionsPanel.add(btnRegister);
        actionsPanel.add(btnClear);

        card.add(actionsPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createEventInfoCard() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 14));

        // Header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(UITheme.CARD_BG);

        JLabel lblTitle = new JLabel("Selected Event Details");
        lblTitle.setFont(UITheme.FONT_SECTION);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);

        lblDetailStatus = new JLabel("  Status  ");
        lblDetailStatus.setFont(UITheme.FONT_BADGE);
        lblDetailStatus.setOpaque(true);
        lblDetailStatus.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));

        cardHeader.add(lblTitle, BorderLayout.WEST);
        cardHeader.add(lblDetailStatus, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(UITheme.CARD_BG);

        lblDetailName = new JLabel("No event selected");
        lblDetailName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDetailName.setForeground(UITheme.TEXT_PRIMARY);

        lblDetailId = new JLabel("Event ID: -");
        lblDetailId.setFont(UITheme.FONT_SMALL);
        lblDetailId.setForeground(UITheme.TEXT_MUTED);

        detailsPanel.add(lblDetailName);
        detailsPanel.add(Box.createVerticalStrut(4));
        detailsPanel.add(lblDetailId);
        detailsPanel.add(Box.createVerticalStrut(16));

        // Details Grid
        JPanel grid = new JPanel(new GridLayout(5, 2, 10, 10));
        grid.setBackground(UITheme.CARD_BG);

        lblDetailDate = new JLabel("-");
        lblDetailVenue = new JLabel("-");
        lblDetailCapacity = new JLabel("-");
        lblDetailBooked = new JLabel("-");
        lblDetailAvailable = new JLabel("-");

        addDetailRow(grid, "Date:", lblDetailDate);
        addDetailRow(grid, "Venue:", lblDetailVenue);
        addDetailRow(grid, "Total Capacity:", lblDetailCapacity);
        addDetailRow(grid, "Confirmed Bookings:", lblDetailBooked);
        addDetailRow(grid, "Seats Remaining:", lblDetailAvailable);

        detailsPanel.add(grid);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Occupancy Bar
        JLabel lblOcc = new JLabel("Event Capacity Utilization:");
        lblOcc.setFont(UITheme.FONT_LABEL);
        lblOcc.setForeground(UITheme.TEXT_MUTED);
        detailsPanel.add(lblOcc);
        detailsPanel.add(Box.createVerticalStrut(6));

        progressDetailOccupancy = new JProgressBar(0, 100);
        progressDetailOccupancy.setStringPainted(true);
        progressDetailOccupancy.setFont(UITheme.FONT_SMALL);
        progressDetailOccupancy.setPreferredSize(new Dimension(200, 18));
        progressDetailOccupancy.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        progressDetailOccupancy.setForeground(UITheme.BLUE_ACCENT);
        detailsPanel.add(progressDetailOccupancy);

        card.add(detailsPanel, BorderLayout.CENTER);

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
        container.add(Box.createVerticalStrut(14));
    }

    private void addDetailRow(JPanel grid, String label, JLabel valueLabel) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_REGULAR);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);

        grid.add(lbl);
        grid.add(valueLabel);
    }

    private void updateSelectedEventInfo() {
        EventComboItem selected = (EventComboItem) cmbEvents.getSelectedItem();
        if (selected == null) {
            lblDetailName.setText("No event selected");
            lblDetailId.setText("Event ID: -");
            lblDetailDate.setText("-");
            lblDetailVenue.setText("-");
            lblDetailCapacity.setText("-");
            lblDetailBooked.setText("-");
            lblDetailAvailable.setText("-");
            lblDetailStatus.setText("  N/A  ");
            lblDetailStatus.setBackground(new Color(241, 245, 249));
            lblDetailStatus.setForeground(UITheme.TEXT_MUTED);
            progressDetailOccupancy.setValue(0);
            return;
        }

        Event ev = selected.event;
        int booked = service.getRegistrationCount(ev.getEventId());
        int available = service.getAvailableSeats(ev.getEventId());
        double occupancy = service.getEventOccupancyPercentage(ev.getEventId());

        lblDetailName.setText(ev.getEventName());
        lblDetailId.setText("Event ID: #" + ev.getEventId());
        lblDetailDate.setText(ev.getDate());
        lblDetailVenue.setText(ev.getVenue());
        lblDetailCapacity.setText(ev.getCapacity() + " seats");
        lblDetailBooked.setText(booked + " registered");
        lblDetailAvailable.setText(available + " seats left");

        if (available > 0) {
            lblDetailStatus.setText("  ● Open for Registration  ");
            lblDetailStatus.setBackground(UITheme.SUCCESS_BG);
            lblDetailStatus.setForeground(UITheme.SUCCESS_TEXT);
            lblDetailStatus.setBorder(new LineBorder(UITheme.SUCCESS_BORDER, 1, true));
            lblDetailAvailable.setForeground(new Color(22, 101, 52));
            lblDetailAvailable.setFont(UITheme.FONT_BOLD);
        } else {
            lblDetailStatus.setText("  ✕ Sold Out  ");
            lblDetailStatus.setBackground(UITheme.DANGER_BG);
            lblDetailStatus.setForeground(UITheme.DANGER_TEXT);
            lblDetailStatus.setBorder(new LineBorder(UITheme.DANGER_BORDER, 1, true));
            lblDetailAvailable.setForeground(UITheme.DANGER_TEXT);
            lblDetailAvailable.setFont(UITheme.FONT_BOLD);
        }

        int occInt = (int) Math.round(occupancy);
        progressDetailOccupancy.setValue(Math.min(100, occInt));
        progressDetailOccupancy.setString(String.format("%.1f%%", occupancy));
        if (occInt >= 100) {
            progressDetailOccupancy.setForeground(UITheme.DANGER_BTN);
        } else if (occInt >= 80) {
            progressDetailOccupancy.setForeground(new Color(245, 158, 11));
        } else {
            progressDetailOccupancy.setForeground(UITheme.BLUE_ACCENT);
        }
    }

    private void handleRegistration() {
        EventComboItem selected = (EventComboItem) cmbEvents.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an event to register for.", "Event Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        // 1. Empty field checks
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all participant details (Name, Email, Phone).", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Delimiter checks
        if (name.contains("|") || email.contains("|") || phone.contains("|")) {
            JOptionPane.showMessageDialog(this, "Fields cannot contain pipe ('|') character.", "Invalid Character", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g., student@domain.com).", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Phone format (10 digits)
        if (!phone.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain exactly 10 numeric digits.", "Invalid Phone Number", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 5. Capacity Check
        int availableSeats = service.getAvailableSeats(selected.event.getEventId());
        if (availableSeats <= 0) {
            JOptionPane.showMessageDialog(this, "Sorry! This event has reached its maximum capacity. No seats available.", "Event Full", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6. Attempt registration through service
        try {
            Registration reg = service.registerParticipant(selected.event.getEventId(), name, email, phone);

            // Display Confirmation Dialog with receipt details
            showConfirmationDialog(reg, selected.event);
            clearForm();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showConfirmationDialog(Registration reg, Event event) {
        JPanel receipt = new JPanel();
        receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
        receipt.setBackground(Color.WHITE);
        receipt.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblSuccess = new JLabel("REGISTRATION CONFIRMED!");
        lblSuccess.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSuccess.setForeground(new Color(22, 101, 52));
        lblSuccess.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRegId = new JLabel("Registration ID: " + reg.getRegistrationId());
        lblRegId.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRegId.setForeground(UITheme.BLUE_ACCENT);
        lblRegId.setAlignmentX(Component.CENTER_ALIGNMENT);

        receipt.add(lblSuccess);
        receipt.add(Box.createVerticalStrut(6));
        receipt.add(lblRegId);
        receipt.add(Box.createVerticalStrut(14));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        receipt.add(sep);
        receipt.add(Box.createVerticalStrut(10));

        addReceiptLine(receipt, "Participant Name:", reg.getParticipant().getName());
        addReceiptLine(receipt, "Email Address:", reg.getParticipant().getEmail());
        addReceiptLine(receipt, "Phone Number:", reg.getParticipant().getPhone());
        addReceiptLine(receipt, "Event:", event.getEventName());
        addReceiptLine(receipt, "Date & Venue:", event.getDate() + " at " + event.getVenue());
        addReceiptLine(receipt, "Status:", "Confirmed");

        JOptionPane.showMessageDialog(
                this,
                receipt,
                "Registration Success",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void addReceiptLine(JPanel container, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
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

    private void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        if (cmbEvents.getItemCount() > 0) {
            cmbEvents.setSelectedIndex(0);
        }
        updateSelectedEventInfo();
    }

    public void refreshData() {
        EventComboItem currentlySelected = (EventComboItem) cmbEvents.getSelectedItem();
        int selectedId = (currentlySelected != null) ? currentlySelected.event.getEventId() : -1;

        cmbEvents.removeAllItems();
        List<Event> events = service.getEvents();
        EventComboItem toSelect = null;

        for (Event ev : events) {
            int available = service.getAvailableSeats(ev.getEventId());
            EventComboItem item = new EventComboItem(ev, available);
            cmbEvents.addItem(item);
            if (ev.getEventId() == selectedId) {
                toSelect = item;
            }
        }

        if (toSelect != null) {
            cmbEvents.setSelectedItem(toSelect);
        } else if (cmbEvents.getItemCount() > 0) {
            cmbEvents.setSelectedIndex(0);
        }

        updateSelectedEventInfo();
    }

    /**
     * Item wrapper for JComboBox to display Event Name and remaining seats cleanly.
     */
    private static class EventComboItem {
        final Event event;
        final int availableSeats;

        EventComboItem(Event event, int availableSeats) {
            this.event = event;
            this.availableSeats = availableSeats;
        }

        @Override
        public String toString() {
            String seatText = (availableSeats > 0) ? (availableSeats + " seats left") : "SOLD OUT";
            return event.getEventName() + " (" + event.getDate() + ") - [" + seatText + "]";
        }
    }
}
