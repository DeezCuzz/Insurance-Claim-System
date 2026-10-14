package ics.gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * A screen that is planned but not built yet. It shows what will be here and in which week.
 * When you build the real screen, replace the placeholder in ShellPanel with your new panel.
 */
@SuppressWarnings({"serial", "this-escape"})
public class PlaceholderPanel extends BasePanel {

    public PlaceholderPanel(String title, String subtitle, String week, String... plannedItems) {
        super(title, subtitle);

        JPanel card = Theme.card(28);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel chip = Theme.label(week, Theme.FONT_LABEL, Theme.NAVY);
        chip.setOpaque(true);
        chip.setBackground(Theme.SOFT_PURPLE);
        chip.setBorder(new EmptyBorder(5, 12, 5, 12));
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(chip);

        card.add(javax.swing.Box.createVerticalStrut(16));
        JLabel heading = Theme.label("Planned for this screen", Theme.FONT_HEADING, Theme.TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(javax.swing.Box.createVerticalStrut(10));

        for (String item : plannedItems) {
            JLabel line = Theme.label("\u2022  " + item, Theme.FONT_BODY, Theme.TEXT_DARK);
            line.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(line);
            card.add(javax.swing.Box.createVerticalStrut(8));
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.NORTH);
        getBody().add(wrapper, BorderLayout.CENTER);
    }
}
