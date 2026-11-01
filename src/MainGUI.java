import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * MainGUI: Top-level desktop GUI frame for the Event Registration System.
 * Houses the professional navy blue sidebar navigation and modern CardLayout views.
 */
public class MainGUI extends JFrame {

    public static final String SCREEN_DASHBOARD = "DASHBOARD";
    public static final String SCREEN_EVENTS = "EVENTS";
    public static final String SCREEN_REGISTRATION = "REGISTRATION";
    public static final String SCREEN_MANAGEMENT = "MANAGEMENT";
    public static final String SCREEN_REPORTS = "REPORTS";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);
    private final Map<String, NavButton> navButtons = new HashMap<>();
    private String currentScreen = SCREEN_DASHBOARD;

    // Panels
    private DashboardPanel dashboardPanel;
    private EventPanel eventPanel;
    private RegistrationPanel registrationPanel;
    private RegistrationManagementPanel registrationManagementPanel;
    private ReportPanel reportPanel;

    public MainGUI() {
        setTitle("Event Registration & Management System - Course Project");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1220, 780);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);

        // Handle safe window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.BG_MAIN);

        // 1. Sidebar Navigation (West)
        rootPanel.add(createSidebar(), BorderLayout.WEST);

        // 2. Card Layout Content Area (Center)
        initCardScreens();
        rootPanel.add(cardsPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);
        showScreen(SCREEN_DASHBOARD);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(UITheme.NAVY_DARK);
        sidebar.setPreferredSize(new Dimension(240, 0));

        // Top Brand Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(UITheme.NAVY_DARK);
        brandPanel.setBorder(new EmptyBorder(26, 20, 24, 20));

        JLabel lblBrand = new JLabel("EVENT REGISTRATION");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblSubBrand = new JLabel("System Management Suite");
        lblSubBrand.setFont(UITheme.FONT_SMALL);
        lblSubBrand.setForeground(UITheme.TEXT_SIDEBAR);

        brandPanel.add(lblBrand);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(lblSubBrand);

        JSeparator brandSep = new JSeparator();
        brandSep.setForeground(UITheme.NAVY_SIDEBAR_ACTIVE);
        brandSep.setBackground(UITheme.NAVY_SIDEBAR_ACTIVE);
        brandSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.NAVY_DARK);
        topContainer.add(brandPanel);
        topContainer.add(brandSep);
        topContainer.add(Box.createVerticalStrut(14));

        sidebar.add(topContainer, BorderLayout.NORTH);

        // Center Navigation Items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(UITheme.NAVY_DARK);
        menuPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        addNavItem(menuPanel, "Dashboard", SCREEN_DASHBOARD, "[ # ]");
        addNavItem(menuPanel, "Events", SCREEN_EVENTS, "[ E ]");
        addNavItem(menuPanel, "Register", SCREEN_REGISTRATION, "[ + ]");
        addNavItem(menuPanel, "Registrations", SCREEN_MANAGEMENT, "[ = ]");
        addNavItem(menuPanel, "Reports", SCREEN_REPORTS, "[ % ]");

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Bottom Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(UITheme.NAVY_DARK);
        footerPanel.setBorder(new EmptyBorder(12, 14, 20, 14));

        // System status
        JLabel lblStatus = new JLabel("Status: Local Storage Ready");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(new Color(134, 239, 172));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnExit = new JButton("Exit Application");
        btnExit.setFont(UITheme.FONT_BOLD);
        btnExit.setForeground(new Color(248, 113, 113));
        btnExit.setBackground(UITheme.NAVY_SIDEBAR_ACTIVE);
        btnExit.setFocusPainted(false);
        btnExit.setBorder(new CompoundBorder(
                new LineBorder(new Color(71, 85, 105), 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.addActionListener(e -> handleExit());

        footerPanel.add(lblStatus);
        footerPanel.add(Box.createVerticalStrut(12));
        footerPanel.add(btnExit);

        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavItem(JPanel container, String text, String screenKey, String iconText) {
        NavButton btn = new NavButton(text, iconText, screenKey);
        btn.addActionListener(e -> showScreen(screenKey));
        navButtons.put(screenKey, btn);

        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    private void initCardScreens() {
        dashboardPanel = new DashboardPanel(this);
        eventPanel = new EventPanel();
        registrationPanel = new RegistrationPanel();
        registrationManagementPanel = new RegistrationManagementPanel();
        reportPanel = new ReportPanel();

        cardsPanel.add(dashboardPanel, SCREEN_DASHBOARD);
        cardsPanel.add(eventPanel, SCREEN_EVENTS);
        cardsPanel.add(registrationPanel, SCREEN_REGISTRATION);
        cardsPanel.add(registrationManagementPanel, SCREEN_MANAGEMENT);
        cardsPanel.add(reportPanel, SCREEN_REPORTS);
    }

    public void showScreen(String screenKey) {
        this.currentScreen = screenKey;
        cardLayout.show(cardsPanel, screenKey);

        // Update active nav button state
        for (Map.Entry<String, NavButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(screenKey));
        }

        // Trigger panel refresh when navigated to
        switch (screenKey) {
            case SCREEN_DASHBOARD:
                if (dashboardPanel != null) dashboardPanel.refreshData();
                break;
            case SCREEN_EVENTS:
                if (eventPanel != null) eventPanel.refreshData();
                break;
            case SCREEN_REGISTRATION:
                if (registrationPanel != null) registrationPanel.refreshData();
                break;
            case SCREEN_MANAGEMENT:
                if (registrationManagementPanel != null) registrationManagementPanel.refreshData();
                break;
            case SCREEN_REPORTS:
                if (reportPanel != null) reportPanel.refreshData();
                break;
        }
    }

    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Event Registration System?\nAll data has been safely saved to data files.",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Custom styled navigation button for the modern navy sidebar.
     */
    private static class NavButton extends JButton {
        private final String screenKey;
        private boolean active = false;

        public NavButton(String text, String iconText, String screenKey) {
            super("  " + iconText + "   " + text);
            this.screenKey = screenKey;

            setFont(UITheme.FONT_BOLD);
            setForeground(UITheme.TEXT_SIDEBAR);
            setBackground(UITheme.NAVY_DARK);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(210, 42));
            setBorder(new EmptyBorder(9, 16, 9, 16));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!active) {
                        setBackground(UITheme.NAVY_SIDEBAR_HOVER);
                        setForeground(Color.WHITE);
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!active) {
                        setBackground(UITheme.NAVY_DARK);
                        setForeground(UITheme.TEXT_SIDEBAR);
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                setBackground(UITheme.NAVY_SIDEBAR_ACTIVE);
                setForeground(Color.WHITE);
                setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.BLUE_ACCENT),
                        new EmptyBorder(9, 12, 9, 16)
                ));
            } else {
                setBackground(UITheme.NAVY_DARK);
                setForeground(UITheme.TEXT_SIDEBAR);
                setBorder(new EmptyBorder(9, 16, 9, 16));
            }
            repaint();
        }
    }

    public static void main(String[] args) {
        // Set Look and Feel safely
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing Look and Feel
        }

        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }
}
