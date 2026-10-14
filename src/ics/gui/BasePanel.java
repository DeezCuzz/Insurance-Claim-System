package ics.gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;

/**
 * Parent of every screen. It supplies the title, the subtitle and the body area,
 * so every screen looks the same without copying code (inheritance).
 *
 * To make a new screen:
 *   1. extends BasePanel and call super("Title", "One-line description")
 *   2. build your layout inside getBody()
 *   3. override onShow() to reload data each time the screen opens
 */
@SuppressWarnings({"serial", "this-escape"})
public abstract class BasePanel extends JPanel {

    private final JPanel body = new JPanel(new BorderLayout());

    protected BasePanel(String title, String subtitle) {
        super(new BorderLayout(0, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        setMinimumSize(Theme.CONTENT_MIN);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel titleLabel = Theme.label(title, Theme.FONT_TITLE, Theme.NAVY);
        JLabel subtitleLabel = Theme.label(subtitle, Theme.FONT_SUBTITLE, Theme.TEXT_MUTED);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);

        body.setOpaque(false);
        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    protected JPanel getBody() {
        return body;
    }

    /** Called every time the user opens this screen. Reload tables and counts here. */
    public void onShow() {
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Adds "label above field" to a form that uses GridBagLayout. index = 0, 1, 2 ... */
    protected static void addField(JPanel form, int index, JLabel label, JComponent field) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0;
        lc.gridy = index * 2;
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(index == 0 ? 0 : 14, 0, 4, 0);
        form.add(label, lc);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 0;
        fc.gridy = index * 2 + 1;
        fc.weightx = 1;
        fc.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, fc);
    }
}
