package ics.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

/**
 * TEMPLATE login screen. The layout, required-field markers and error message are final.
 * The check is NOT real yet: any username and password are accepted.
 * TODO (Week 1): replace the marked line with AuthService.login(username, password),
 *                which checks users.txt, counts failed attempts, and blocks after 3.
 */
@SuppressWarnings({"serial", "this-escape"})
public class LoginPanel extends JPanel {

    private final JTextField usernameField = Theme.styleField(new JTextField());
    private final JPasswordField passwordField = Theme.styleField(new JPasswordField());
    private final JLabel errorLabel = Theme.label(" ", Theme.FONT_LABEL, Theme.ERROR);
    private final RoundedButton loginButton = new RoundedButton("Log in", RoundedButton.Kind.PRIMARY);
    private final Consumer<String> onSuccess;

    /** onSuccess receives the username after a successful login. */
    public LoginPanel(Consumer<String> onSuccess) {
        super(new GridBagLayout());
        this.onSuccess = onSuccess;
        setBackground(Theme.BACKGROUND);

        JPanel card = Theme.card(32);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 540));

        JLabel logo = new JLabel(new ShieldIcon(64, Theme.NAVY, Theme.WHITE));
        JLabel title = Theme.label("Insurance Claims System", Theme.FONT_HEADING, Theme.NAVY);
        JLabel subtitle = Theme.label("Sign in to continue", Theme.FONT_SUBTITLE, Theme.TEXT_MUTED);

        Theme.tip(usernameField, "Your system username (required)");
        Theme.tip(passwordField, "Your password (required)");
        Theme.tip(loginButton, "Log in (Enter)");
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        RoundedButton createAccount = new RoundedButton("Create account", RoundedButton.Kind.SECONDARY);
        createAccount.setEnabled(false);
        Theme.tip(createAccount, "Sign-up with password verification is built in Week 1");
        createAccount.setPreferredSize(new Dimension(0, 40));

        JLabel footnote = Theme.label("<html>Template only: login is not connected to users.txt yet.</html>",
                Theme.FONT_CAPTION, Theme.TEXT_MUTED);

        int row = 0;
        row = add(card, logo, row, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        row = add(card, title, row, 12, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        row = add(card, subtitle, row, 2, GridBagConstraints.CENTER, GridBagConstraints.NONE);
        row = add(card, Theme.requiredLabel("Username"), row, 26, GridBagConstraints.WEST, GridBagConstraints.NONE);
        row = add(card, usernameField, row, 4, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        row = add(card, Theme.requiredLabel("Password"), row, 14, GridBagConstraints.WEST, GridBagConstraints.NONE);
        row = add(card, passwordField, row, 4, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        row = add(card, errorLabel, row, 10, GridBagConstraints.WEST, GridBagConstraints.NONE);
        row = add(card, loginButton, row, 8, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        row = add(card, createAccount, row, 10, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL);
        add(card, footnote, row, 16, GridBagConstraints.CENTER, GridBagConstraints.NONE);

        add(card);
        loginButton.addActionListener(e -> attemptLogin());
    }

    public RoundedButton getLoginButton() {
        return loginButton;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty()) {
            errorLabel.setText("Username is required.");
            usernameField.requestFocusInWindow();
            return;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Password is required.");
            passwordField.requestFocusInWindow();
            return;
        }
        // TODO (Week 1): AuthService.login(username, password) goes here.
        errorLabel.setText(" ");
        passwordField.setText("");
        onSuccess.accept(username);
    }

    private static int add(JPanel card, java.awt.Component c, int row, int topGap, int anchor, int fill) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 1;
        gc.anchor = anchor;
        gc.fill = fill;
        gc.insets = new Insets(topGap, 0, 0, 0);
        card.add(c, gc);
        return row + 1;
    }
}
