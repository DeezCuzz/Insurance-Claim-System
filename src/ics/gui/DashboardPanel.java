package ics.gui;

import ics.service.DashboardService;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

/** Home screen: live record counts and shortcuts. */
@SuppressWarnings({"serial", "this-escape"})
public class DashboardPanel extends BasePanel {

    private final DashboardService service;
    private final JLabel customersValue = statValue();
    private final JLabel policiesValue = statValue();
    private final JLabel claimsValue = statValue();
    private final JLabel payoutsValue = statValue();

    /** navigator receives a page name such as "Customers" and opens that screen. */
    public DashboardPanel(DashboardService service, Consumer<String> navigator) {
        super("Dashboard", "A quick look at what is in the system");
        this.service = service;

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("CUSTOMERS", customersValue, "registered"));
        stats.add(statCard("POLICIES", policiesValue, "sold"));
        stats.add(statCard("CLAIMS", claimsValue, "filed"));
        stats.add(statCard("PAYOUTS", payoutsValue, "released"));

        JPanel actions = Theme.card(24);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        JLabel heading = Theme.label("Quick actions", Theme.FONT_HEADING, Theme.TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.add(heading);
        actions.add(javax.swing.Box.createVerticalStrut(14));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoundedButton newCustomer = new RoundedButton("New customer", RoundedButton.Kind.PRIMARY);
        Theme.tip(newCustomer, "Open the Customers screen and register someone new");
        newCustomer.addActionListener(e -> navigator.accept("Customers"));
        RoundedButton sellPolicy = new RoundedButton("Sell a policy", RoundedButton.Kind.SECONDARY);
        Theme.tip(sellPolicy, "Policy sales are built in Week 1");
        sellPolicy.addActionListener(e -> navigator.accept("Policies"));
        newCustomer.setPreferredSize(new java.awt.Dimension(150, 40));
        sellPolicy.setPreferredSize(new java.awt.Dimension(150, 40));
        buttons.add(newCustomer);
        buttons.add(javax.swing.Box.createHorizontalStrut(12));
        buttons.add(sellPolicy);
        actions.add(buttons);

        JPanel column = new JPanel(new BorderLayout(0, 16));
        column.setOpaque(false);
        column.add(stats, BorderLayout.NORTH);
        column.add(actions, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(column, BorderLayout.NORTH);
        getBody().add(wrapper, BorderLayout.CENTER);
    }

    @Override
    public void onShow() {
        customersValue.setText(String.valueOf(service.countCustomers()));
        policiesValue.setText(String.valueOf(service.countPolicies()));
        claimsValue.setText(String.valueOf(service.countClaims()));
        payoutsValue.setText(String.valueOf(service.countPayouts()));
    }

    private static JLabel statValue() {
        return Theme.label("0", Theme.FONT_STAT, Theme.NAVY);
    }

    private static JPanel statCard(String caption, JLabel value, String hint) {
        JPanel card = Theme.card(18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel c = Theme.label(caption, Theme.FONT_LABEL, Theme.TEXT_MUTED);
        JLabel h = Theme.label(hint, Theme.FONT_CAPTION, Theme.TEXT_MUTED);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(c);
        card.add(value);
        card.add(h);
        return card;
    }
}
