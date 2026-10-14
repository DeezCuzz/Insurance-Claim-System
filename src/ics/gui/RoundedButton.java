package ics.gui;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Our own button, painted by hand so it looks the same on Windows, Mac and Linux
 * (the built-in look ignores button colours on some systems).
 *   PRIMARY   = rose, for the main action on a screen (Save, Login)
 *   SECONDARY = white with a purple outline (Clear, Cancel)
 */
@SuppressWarnings({"serial", "this-escape"})
public class RoundedButton extends JButton {

    public enum Kind { PRIMARY, SECONDARY }

    private final Kind kind;

    public RoundedButton(String text, Kind kind) {
        super(text);
        this.kind = kind;
        setFont(Theme.FONT_BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(Theme.BUTTON_SIZE);
        setMinimumSize(Theme.BUTTON_MIN);
        setMaximumSize(Theme.BUTTON_MAX);
    }

    /** The text colour depends on the kind and on whether the button is enabled. */
    @Override
    public Color getForeground() {
        if (kind == null) {
            return super.getForeground();       // called once by the JButton constructor
        }
        if (!isEnabled()) {
            return new Color(0x9A9A9A);
        }
        return kind == Kind.PRIMARY ? Theme.WHITE : Theme.NAVY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ButtonModel m = getModel();
        Color fill;
        Color outline = null;
        if (!isEnabled()) {
            fill = new Color(0xEEEBF0);
            outline = new Color(0xDDD6E5);
        } else if (kind == Kind.PRIMARY) {
            fill = m.isPressed() ? Theme.ROSE.darker().darker()
                 : m.isRollover() ? Theme.ROSE.darker() : Theme.ROSE;
        } else {
            fill = m.isRollover() ? Theme.SOFT_PURPLE : Theme.WHITE;
            outline = Theme.NAVY;
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        if (outline != null) {
            g2.setColor(outline);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);                 // draws the text (and the Alt+key underline)
    }
}
