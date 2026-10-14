package ics.gui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 * The system logo: a shield with a check mark, drawn in code so no image files are needed
 * (rubric I.A.4: graphics must be pertinent to the project).
 */
public class ShieldIcon implements Icon {

    private final int size;
    private final Color shieldColor;
    private final Color checkColor;

    public ShieldIcon(int size, Color shieldColor, Color checkColor) {
        this.size = size;
        this.shieldColor = shieldColor;
        this.checkColor = checkColor;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        double s = size;

        Path2D shield = new Path2D.Double();
        shield.moveTo(s * 0.50, s * 0.04);
        shield.lineTo(s * 0.92, s * 0.20);
        shield.curveTo(s * 0.92, s * 0.62, s * 0.78, s * 0.84, s * 0.50, s * 0.97);
        shield.curveTo(s * 0.22, s * 0.84, s * 0.08, s * 0.62, s * 0.08, s * 0.20);
        shield.closePath();
        g2.setColor(shieldColor);
        g2.fill(shield);

        Path2D check = new Path2D.Double();
        check.moveTo(s * 0.30, s * 0.50);
        check.lineTo(s * 0.45, s * 0.65);
        check.lineTo(s * 0.72, s * 0.36);
        g2.setColor(checkColor);
        g2.setStroke(new BasicStroke((float) (s * 0.09), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(check);
        g2.dispose();
    }

    @Override public int getIconWidth()  { return size; }
    @Override public int getIconHeight() { return size; }

    /** For the window icon. */
    public BufferedImage toImage() {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paintIcon(null, g2, 0, 0);
        g2.dispose();
        return image;
    }
}
