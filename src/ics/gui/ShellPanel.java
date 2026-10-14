package ics.gui;

import ics.repository.DataStore;
import ics.service.CustomerService;
import ics.service.DashboardService;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The main layout after login: navy sidebar on the left, one screen at a time on the right.
 *
 * To add a screen: put it in the `pages` map below. The sidebar button appears by itself.
 * Replace a PlaceholderPanel with your real panel when it is ready.
 */
@SuppressWarnings({"serial", "this-escape"})
public class ShellPanel extends JPanel {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, BasePanel> pages = new LinkedHashMap<>();
    private final List<NavButton> navButtons = new ArrayList<>();

    public ShellPanel(DataStore store, String username, Runnable onLogout) {
        super(new BorderLayout());

        // ---- the screens (key = sidebar text) ----
        pages.put("Dashboard", new DashboardPanel(new DashboardService(store), this::show));
        pages.put("Customers", new CustomerPanel(new CustomerService(store)));
        pages.put("Policies", new PlaceholderPanel("Policies",
                "Sell, view, edit and cancel policies", "Planned for Week 1",
                "Policy catalog (Health, Auto) with premium preview",
                "Sell a policy to a customer",
                "Policy list with status, remaining limit and deductible",
                "Edit or cancel a policy (cancel needs the Admin password)"));
        pages.put("Claims", new PlaceholderPanel("Claims",
                "File a claim and follow it to payout", "Planned for Week 2",
                "File claim form with required-field markers",
                "Automatic rule checks with reason codes",
                "Claims list with status badges"));
        pages.put("Review", new PlaceholderPanel("Review",
                "Approve or deny pending claims", "Planned for Week 2",
                "Pending claims queue and claim detail view",
                "Approve (payout calculated) or deny with a reason code",
                "Mark as paid and print the payout slip"));
        pages.put("Users", new PlaceholderPanel("Users",
                "Admin only: manage who can log in", "Planned for Week 1",
                "Add a system user and choose Admin or Staff",
                "Unblock an account locked after 3 wrong passwords",
                "Change password"));

        for (Map.Entry<String, BasePanel> entry : pages.entrySet()) {
            content.add(entry.getValue(), entry.getKey());
        }
        content.setBackground(Theme.BACKGROUND);

        add(buildSidebar(username, onLogout), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        show("Dashboard");
    }

    private JPanel buildSidebar(String username, Runnable onLogout) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.NAVY);
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));

        // brand block
        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(26, 28, 26, 12));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(new JLabel(new ShieldIcon(40, Theme.WHITE, Theme.NAVY)), BorderLayout.WEST);
        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.add(Theme.label("ICS", Theme.FONT_HEADING, Theme.WHITE));
        names.add(Theme.label("Claims System", Theme.FONT_CAPTION, new java.awt.Color(0xD9CCF2)));
        brand.add(names, BorderLayout.CENTER);
        sidebar.add(brand);

        // navigation
        for (String key : pages.keySet()) {
            NavButton button = new NavButton(key);
            Theme.tip(button, "Open " + key);
            button.addActionListener(e -> show(key));
            navButtons.add(button);
            sidebar.add(button);
        }
        sidebar.add(Box.createVerticalGlue());

        // footer
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(16, 28, 24, 28));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        JLabel signedIn = Theme.label("Signed in as", Theme.FONT_CAPTION, new java.awt.Color(0xD9CCF2));
        JLabel name = Theme.label(username, Theme.FONT_BUTTON, Theme.WHITE);
        signedIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        RoundedButton logout = new RoundedButton("Log out", RoundedButton.Kind.SECONDARY);
        Theme.tip(logout, "Return to the login screen");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> onLogout.run());
        footer.add(signedIn);
        footer.add(name);
        footer.add(Box.createVerticalStrut(12));
        footer.add(logout);
        sidebar.add(footer);
        return sidebar;
    }

    /** Opens one screen, reloads its data, and highlights its sidebar button. */
    public void show(String key) {
        cards.show(content, key);
        pages.get(key).onShow();
        for (NavButton button : navButtons) {
            button.setSelected(button.getKey().equals(key));
        }
    }
}
