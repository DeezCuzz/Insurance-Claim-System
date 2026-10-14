package ics.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

/**
 * A sidebar item. Hover = lighter navy. Selected = magenta, bold text, and a white bar on the left,
 * so the selected state does not depend on colour alone.
 */
@SuppressWarnings({"serial", "this-escape"})
public class NavButton extends JButton {

    private final String key;
    private boolean selected;

    public NavButton(String key) {
        super(key);
        this.key = key;
        setFont(Theme.FONT_BUTTON);
        setHorizontalAlignment(LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 8));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 44));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setAlignmentX(LEFT_ALIGNMENT);
    }

    public String getKey() {
        return key;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setFont(selected ? Theme.FONT_BUTTON.deriveFont(Font.BOLD) : Theme.FONT_BODY);
        repaint();
    }

    @Override
    public Color getForeground() {
        return Theme.WHITE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (selected) {
            g.setColor(Theme.MAGENTA);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Theme.WHITE);
            g.fillRect(0, 0, 5, getHeight());
        } else if (getModel().isRollover()) {
            g.setColor(Theme.NAV_HOVER);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }
}
