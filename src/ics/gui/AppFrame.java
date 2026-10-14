package ics.gui;

import ics.repository.DataStore;

import javax.swing.JFrame;

/** The one and only window. It swaps its content between the login screen and the main layout. */
@SuppressWarnings({"serial", "this-escape"})
public class AppFrame extends JFrame {

    private final DataStore store;

    public AppFrame(DataStore store) {
        super("Insurance Claims System");
        this.store = store;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ShieldIcon(64, Theme.NAVY, Theme.WHITE).toImage());
        setMinimumSize(Theme.MIN_WINDOW);
        setSize(Theme.DEFAULT_WINDOW);
        setLocationRelativeTo(null);
        showLogin();
    }

    private void showLogin() {
        LoginPanel login = new LoginPanel(this::showShell);
        setContentPane(login);
        getRootPane().setDefaultButton(login.getLoginButton());   // Enter = log in
        revalidate();
        repaint();
    }

    private void showShell(String username) {
        getRootPane().setDefaultButton(null);
        setContentPane(new ShellPanel(store, username, this::showLogin));
        revalidate();
        repaint();
    }
}
