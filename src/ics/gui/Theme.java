package ics.gui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * One place for every colour, font and size, taken from the Bulletin Board sheet.
 * Change a value here and every screen follows. No screen should hard-code a colour or size.
 */
public final class Theme {

    private Theme() { }

    // ---- colours (Bulletin Board: Colors) ----
    public static final Color NAVY       = new Color(0x4B1C9C);   // sidebar, headings
    public static final Color MAGENTA    = new Color(0x931DA3);   // active navigation item
    public static final Color ROSE       = new Color(0xA31D72);   // primary action buttons
    public static final Color WHITE      = Color.WHITE;
    public static final Color BACKGROUND = new Color(0xF7F5F7);   // page background
    // extra shades (derived, not on the Bulletin Board yet)
    public static final Color TEXT_DARK  = new Color(0x2B2B2B);
    public static final Color TEXT_MUTED = new Color(0x6B6B6B);
    public static final Color BORDER     = new Color(0xDDD6E5);
    public static final Color NAV_HOVER  = new Color(0x663EAB);   // lighter than NAVY, clearly different from ROSE
    public static final Color SOFT_PURPLE = new Color(0xEFE8FA);
    public static final Color ERROR      = new Color(0xC0392B);
    public static final Color SUCCESS    = new Color(0x2E8B4F);

    // ---- sizes (Bulletin Board: Minimum / Maximum Panel Size) ----
    public static final Dimension MIN_WINDOW     = new Dimension(1024, 768);
    public static final Dimension DEFAULT_WINDOW = new Dimension(1280, 800);
    public static final int SIDEBAR_WIDTH        = 240;                    // allowed 220 to 300
    public static final Dimension CONTENT_MIN    = new Dimension(700, 480);
    public static final Dimension FIELD_SIZE     = new Dimension(260, 32); // min 180x28, max 320x32
    public static final Dimension BUTTON_SIZE    = new Dimension(120, 36); // min 90x32, max 160x40
    public static final Dimension BUTTON_MIN     = new Dimension(90, 32);
    public static final Dimension BUTTON_MAX     = new Dimension(160, 40);

    // ---- fonts (Bulletin Board: Fonts & Size). Falls back if Montserrat is not installed. ----
    private static final Set<String> INSTALLED = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    private static String pick(String fallback, String... wanted) {
        for (String name : wanted) {
            if (INSTALLED.contains(name)) {
                return name;
            }
        }
        return fallback;
    }

    private static final String SANS  = pick(Font.SANS_SERIF, "Montserrat", "Segoe UI", "Helvetica Neue", "Arial");
    private static final String LIGHT = pick(SANS, "Montserrat Light");
    private static final String XLIGHT = pick(LIGHT, "Montserrat ExtraLight");
    private static final String MONO  = pick(Font.MONOSPACED, "JetBrains Mono", "Consolas", "Menlo");

    public static final Font FONT_TITLE    = new Font(SANS, Font.BOLD, 30);
    public static final Font FONT_HEADING  = new Font(SANS, Font.BOLD, 22);
    public static final Font FONT_STAT     = new Font(SANS, Font.BOLD, 34);
    public static final Font FONT_LABEL    = new Font(SANS, Font.BOLD, 12);
    public static final Font FONT_BUTTON   = new Font(SANS, Font.BOLD, 13);
    public static final Font FONT_BODY     = new Font(LIGHT, Font.PLAIN, 14);
    public static final Font FONT_SUBTITLE = new Font(LIGHT, Font.PLAIN, 14);
    public static final Font FONT_CAPTION  = new Font(XLIGHT, Font.PLAIN, 12);
    public static final Font FONT_MONO     = new Font(MONO, Font.PLAIN, 11);

    /** Call once, before any window is created. */
    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // the default look is fine if this fails
        }
        UIManager.put("OptionPane.messageFont", FONT_BODY);
        UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
        UIManager.put("ToolTip.font", FONT_CAPTION);
        UIManager.put("ToolTip.background", new Color(0xFFFBE6));
        UIManager.put("ToolTip.foreground", TEXT_DARK);
        ToolTipManager.sharedInstance().setInitialDelay(300);
    }

    // ---- small builders used by every screen ----

    /** White rounded-looking card with padding. */
    public static JPanel card(int padding) {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), new EmptyBorder(padding, padding, padding, padding)));
        return panel;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /** Field label that shows a red asterisk (rubric II.A.4: required fields must be indicated). */
    public static JLabel requiredLabel(String text) {
        JLabel label = new JLabel("<html>" + text + " <font color='#C0392B'>*</font></html>");
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JLabel fieldLabel(String text) {
        return label(text, FONT_LABEL, TEXT_DARK);
    }

    /** Gives a text field the standard look and a purple outline while it has focus. */
    public static <T extends JTextComponent> T styleField(T field) {
        final Border normal = BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), new EmptyBorder(4, 8, 4, 8));
        final Border focused = BorderFactory.createCompoundBorder(
                new LineBorder(NAVY, 2, true), new EmptyBorder(3, 7, 3, 7));
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_DARK);
        field.setBackground(WHITE);
        field.setCaretColor(NAVY);
        field.setBorder(normal);
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(new Dimension(180, 28));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.setBorder(focused); }
            @Override public void focusLost(FocusEvent e)   { field.setBorder(normal); }
        });
        return field;
    }

    /** Sets the tooltip (rubric I.A.3) and returns the same component. */
    public static <T extends JComponent> T tip(T component, String text) {
        component.setToolTipText(text);
        return component;
    }

    /** Navy header row, padded cells, light purple selection. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_DARK);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setSelectionBackground(SOFT_PURPLE);
        table.setSelectionForeground(TEXT_DARK);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        DefaultTableCellRenderer header = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, false, false, row, col);
                l.setOpaque(true);
                l.setBackground(NAVY);
                l.setForeground(WHITE);
                l.setFont(FONT_LABEL);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                l.setHorizontalAlignment(LEFT);
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(header);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, selected, false, row, col);
                l.setBorder(new EmptyBorder(0, 10, 0, 10));
                l.setFont(col == 0 ? FONT_MONO : FONT_BODY);
                return l;
            }
        });
    }
}
