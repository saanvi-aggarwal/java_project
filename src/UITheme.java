import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Modern design system tokens and component helpers for the Swing application.
 * Professional navy blue, clean slate, and vibrant functional accents.
 */
public class UITheme {

    // Color Palette
    public static final Color NAVY_DARK = new Color(15, 23, 42);       // #0F172A - Sidebar & Dark elements
    public static final Color NAVY_SIDEBAR_ACTIVE = new Color(30, 41, 59); // #1E293B
    public static final Color NAVY_SIDEBAR_HOVER = new Color(24, 33, 47);
    public static final Color BLUE_ACCENT = new Color(37, 99, 235);    // #2563EB - Primary action
    public static final Color BLUE_ACCENT_HOVER = new Color(29, 78, 216);
    public static final Color BLUE_LIGHT = new Color(219, 234, 254);   // #DBEAFE - Selection / soft blue

    public static final Color BG_MAIN = new Color(248, 250, 252);      // #F8FAFC
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // #E2E8F0
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);  // #3B82F6

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // #64748B
    public static final Color TEXT_SIDEBAR = new Color(148, 163, 184); // #94A3B8

    public static final Color SUCCESS_TEXT = new Color(22, 101, 52);   // #166534
    public static final Color SUCCESS_BG = new Color(220, 252, 231);   // #DCFCE7
    public static final Color SUCCESS_BORDER = new Color(134, 239, 172);

    public static final Color DANGER_TEXT = new Color(153, 27, 27);    // #991B1B
    public static final Color DANGER_BG = new Color(254, 226, 226);    // #FEE2E2
    public static final Color DANGER_BORDER = new Color(252, 165, 165);
    public static final Color DANGER_BTN = new Color(239, 68, 68);
    public static final Color DANGER_BTN_HOVER = new Color(220, 38, 38);

    public static final Color WARNING_TEXT = new Color(146, 64, 14);   // #92400E
    public static final Color WARNING_BG = new Color(254, 243, 199);   // #FEF3C7
    public static final Color WARNING_BORDER = new Color(253, 211, 77);

    // Typography (Segoe UI is standard on Windows; fallbacks safely to SansSerif)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Create a modern white card panel with a subtle border and padding.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        return card;
    }

    /**
     * Create a screen header with title and subtitle.
     */
    public static JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_MAIN);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SUBTITLE);
        lblSub.setForeground(TEXT_MUTED);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);

        return header;
    }

    /**
     * Create a modern primary button (Navy/Blue accent) with hover state.
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, BLUE_ACCENT, BLUE_ACCENT_HOVER, Color.WHITE);
    }

    /**
     * Create a modern secondary outline button.
     */
    public static JButton createSecondaryButton(String text) {
        final JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setForeground(new Color(51, 65, 85));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    /**
     * Create a danger button (Red) for destructive actions like Cancel.
     */
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER_BTN, DANGER_BTN_HOVER, Color.WHITE);
    }

    private static JButton createStyledButton(String text, Color normalBg, Color hoverBg, Color fg) {
        final JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setForeground(fg);
        button.setBackground(normalBg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 18, 9, 18));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
            }
        });
        return button;
    }

    /**
     * Create a styled text field with rounded-like padded border and focus highlight.
     */
    public static JTextField createTextField(int columns) {
        final JTextField field = new JTextField(columns);
        field.setFont(FONT_REGULAR);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));

        final Border defaultBorder = new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        );
        final Border focusedBorder = new CompoundBorder(
                new LineBorder(BORDER_FOCUS, 2, true),
                new EmptyBorder(5, 9, 5, 9)
        );

        field.setBorder(defaultBorder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(defaultBorder);
            }
        });

        return field;
    }

    /**
     * Style a JTable with modern headers, alternating row colors, and custom padding.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(BLUE_LIGHT);
        table.setSelectionForeground(NAVY_DARK);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(NAVY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, NAVY_DARK));

        // Default cell renderer with alternating row colors
        DefaultTableCellRenderer alternateRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(alternateRenderer);
        }
    }

    /**
     * Custom renderer for rendering status pill badges ("Confirmed", "Cancelled", "Open", "Sold Out").
     */
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            String text = (value == null) ? "" : value.toString().trim();
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? BLUE_LIGHT : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));

            JLabel badge = new JLabel("  " + text + "  ");
            badge.setFont(FONT_BADGE);
            badge.setOpaque(true);

            if ("Confirmed".equalsIgnoreCase(text) || "Open".equalsIgnoreCase(text) || "Active".equalsIgnoreCase(text)) {
                badge.setBackground(SUCCESS_BG);
                badge.setForeground(SUCCESS_TEXT);
                badge.setBorder(new LineBorder(SUCCESS_BORDER, 1, true));
            } else if ("Cancelled".equalsIgnoreCase(text)) {
                badge.setBackground(DANGER_BG);
                badge.setForeground(DANGER_TEXT);
                badge.setBorder(new LineBorder(DANGER_BORDER, 1, true));
            } else if ("Sold Out".equalsIgnoreCase(text) || "Full".equalsIgnoreCase(text)) {
                badge.setBackground(WARNING_BG);
                badge.setForeground(WARNING_TEXT);
                badge.setBorder(new LineBorder(WARNING_BORDER, 1, true));
            } else {
                badge.setBackground(new Color(241, 245, 249));
                badge.setForeground(TEXT_MUTED);
                badge.setBorder(new LineBorder(BORDER_COLOR, 1, true));
            }

            panel.add(badge);
            return panel;
        }
    }

    /**
     * Custom renderer for displaying an occupancy progress bar inside a table cell.
     */
    public static class ProgressBarRenderer extends DefaultTableCellRenderer {
        private final JProgressBar progressBar = new JProgressBar(0, 100);
        private final JPanel panel = new JPanel(new BorderLayout(8, 0));
        private final JLabel lblText = new JLabel();

        public ProgressBarRenderer() {
            progressBar.setStringPainted(false);
            progressBar.setPreferredSize(new Dimension(80, 12));
            lblText.setFont(FONT_SMALL);
            lblText.setPreferredSize(new Dimension(45, 16));
            panel.setBorder(new EmptyBorder(6, 8, 6, 8));
            panel.add(progressBar, BorderLayout.CENTER);
            panel.add(lblText, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            panel.setBackground(isSelected ? BLUE_LIGHT : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
            int percent = 0;
            if (value instanceof Number) {
                percent = (int) Math.round(((Number) value).doubleValue());
            } else if (value != null) {
                try {
                    String str = value.toString().replace("%", "").trim();
                    percent = (int) Math.round(Double.parseDouble(str));
                } catch (NumberFormatException ignored) {}
            }

            percent = Math.max(0, Math.min(100, percent));
            progressBar.setValue(percent);
            lblText.setText(percent + "%");

            if (percent >= 100) {
                progressBar.setForeground(DANGER_BTN);
            } else if (percent >= 80) {
                progressBar.setForeground(new Color(245, 158, 11)); // Amber
            } else {
                progressBar.setForeground(BLUE_ACCENT);
            }

            return panel;
        }
    }
}
