package ics.gui;

import ics.exception.ICSException;
import ics.exception.InvalidInputException;
import ics.model.Customer;
import ics.service.CustomerService;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

/**
 * Customer records: add, edit, search. This is the model for every other CRUD screen:
 * copy it, change the fields, and swap CustomerService for your own service.
 *
 * Shows the rubric items: required-field markers, tooltips, Alt+key shortcuts, Ctrl+N,
 * auto-numbered IDs, friendly error messages.
 * TODO (Week 2): Delete must ask for the Admin password first.
 */
@SuppressWarnings({"serial", "this-escape"})
public class CustomerPanel extends BasePanel {

    private final CustomerService service;

    private final JTextField idField = Theme.styleField(new JTextField());
    private final JTextField nameField = Theme.styleField(new JTextField());
    private final JTextField contactField = Theme.styleField(new JTextField());
    private final JTextField addressField = Theme.styleField(new JTextField());
    private final JTextField searchField = Theme.styleField(new JTextField());

    private final DefaultTableModel model = new DefaultTableModel(new String[] {"ID", "Name", "Contact", "Address"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    // Narrow window: the table scrolls sideways. Wide window: it stretches to fill the card.
    private final JTable table = new JTable(model) {
        @Override public boolean getScrollableTracksViewportWidth() {
            return getParent() != null && getPreferredSize().width < getParent().getWidth();
        }
    };

    private final RoundedButton saveButton = new RoundedButton("Save new", RoundedButton.Kind.PRIMARY);
    private final RoundedButton updateButton = new RoundedButton("Update", RoundedButton.Kind.SECONDARY);
    private final RoundedButton clearButton = new RoundedButton("Clear", RoundedButton.Kind.SECONDARY);
    private final RoundedButton deleteButton = new RoundedButton("Delete", RoundedButton.Kind.SECONDARY);

    private final JLabel countLabel = Theme.label("", Theme.FONT_CAPTION, Theme.TEXT_MUTED);
    private final JLabel statusLabel = Theme.label(" ", Theme.FONT_LABEL, Theme.SUCCESS);

    private String selectedId;      // null = adding a new customer, otherwise editing this one
    private boolean adjusting;      // true while the code (not the user) changes the table selection

    public CustomerPanel(CustomerService service) {
        super("Customers", "Register, edit and find the people who buy policies");
        this.service = service;
        buildUi();
        wireActions();
        setEditing(false);
    }

    // ------------------------------------------------------------------ layout
    private void buildUi() {
        // left: the form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        idField.setEditable(false);
        idField.setFocusable(false);
        idField.setFont(Theme.FONT_MONO);
        idField.setBackground(Theme.BACKGROUND);
        Theme.tip(idField, "Assigned automatically when you save (CUS-0001, CUS-0002 ...)");
        Theme.tip(nameField, "Full name of the customer (required)");
        Theme.tip(contactField, "Mobile or landline number (required)");
        Theme.tip(addressField, "Home address (optional)");

        addField(form, 0, Theme.fieldLabel("Customer ID"), idField);
        addField(form, 1, Theme.requiredLabel("Full name"), nameField);
        addField(form, 2, Theme.requiredLabel("Contact number"), contactField);
        addField(form, 3, Theme.fieldLabel("Address"), addressField);

        saveButton.setMnemonic(KeyEvent.VK_S);
        updateButton.setMnemonic(KeyEvent.VK_U);
        clearButton.setMnemonic(KeyEvent.VK_C);
        deleteButton.setMnemonic(KeyEvent.VK_D);
        Theme.tip(saveButton, "Save this customer (Alt+S)");
        Theme.tip(updateButton, "Save changes to the selected customer (Alt+U)");
        Theme.tip(clearButton, "Empty the form to add someone new (Alt+C or Ctrl+N)");
        Theme.tip(deleteButton, "Delete the selected customer (Alt+D). Will ask for the Admin password.");

        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 10));
        buttons.setOpaque(false);
        buttons.add(saveButton);
        buttons.add(updateButton);
        buttons.add(clearButton);
        buttons.add(deleteButton);

        JPanel formCard = Theme.card(24);
        formCard.setLayout(new BorderLayout(0, 18));
        formCard.setPreferredSize(new Dimension(350, 0));
        JLabel formTitle = Theme.label("Customer details", Theme.FONT_HEADING, Theme.TEXT_DARK);
        formCard.add(formTitle, BorderLayout.NORTH);
        JPanel formTop = new JPanel(new BorderLayout());
        formTop.setOpaque(false);
        formTop.add(form, BorderLayout.NORTH);          // keeps the fields at the top of the card
        formCard.add(formTop, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 10));
        south.setOpaque(false);
        south.add(buttons, BorderLayout.NORTH);
        south.add(statusLabel, BorderLayout.SOUTH);
        formCard.add(south, BorderLayout.SOUTH);

        // right: search + table
        Theme.styleTable(table);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        int[] widths = {90, 170, 130, 150};          // ID, Name, Contact, Address
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            table.getColumnModel().getColumn(i).setMinWidth(60);
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.WHITE);

        Theme.tip(searchField, "Type part of an ID, name or contact number");
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        searchRow.add(Theme.fieldLabel("Search"), BorderLayout.WEST);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(countLabel, BorderLayout.EAST);

        JPanel tableCard = Theme.card(20);
        tableCard.setLayout(new BorderLayout(0, 14));
        tableCard.add(searchRow, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        getBody().setLayout(new BorderLayout(16, 0));
        getBody().add(formCard, BorderLayout.WEST);
        getBody().add(tableCard, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ behaviour
    private void wireActions() {
        saveButton.addActionListener(e -> save());
        updateButton.addActionListener(e -> update());
        clearButton.addActionListener(e -> {
            clearForm();
            setStatus(" ", true);
        });
        deleteButton.addActionListener(e -> showInfo(
                "Deleting a customer will ask for the Admin password.\nThat dialog is built in Week 2."));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { refreshTable(); }
            @Override public void removeUpdate(DocumentEvent e)  { refreshTable(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || adjusting) {
                return;
            }
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            selectedId = (String) model.getValueAt(row, 0);
            idField.setText(selectedId);
            nameField.setText((String) model.getValueAt(row, 1));
            contactField.setText((String) model.getValueAt(row, 2));
            addressField.setText((String) model.getValueAt(row, 3));
            setEditing(true);
            setStatus(" ", true);
        });

        // Ctrl+N = new customer, from anywhere on this screen
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK), "newCustomer");
        getActionMap().put("newCustomer", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                clearForm();
                setStatus(" ", true);
            }
        });
    }

    @Override
    public void onShow() {
        refreshTable();
        refreshDefaultButton();
    }

    private void save() {
        try {
            Customer saved = service.add(nameField.getText(), contactField.getText(), addressField.getText());
            refreshTable();
            clearForm();
            setStatus("Saved " + saved.getId() + " (" + saved.getName() + ")", true);
        } catch (InvalidInputException e) {
            showFieldError(e);
        } catch (ICSException e) {
            setStatus(e.getMessage(), false);
        }
    }

    private void update() {
        try {
            service.update(selectedId, nameField.getText(), contactField.getText(), addressField.getText());
            String id = selectedId;
            refreshTable();
            setStatus("Updated " + id, true);
        } catch (InvalidInputException e) {
            showFieldError(e);
        } catch (ICSException e) {
            setStatus(e.getMessage(), false);
        }
    }

    private void showFieldError(InvalidInputException e) {
        setStatus(e.getMessage(), false);
        JComponent target = switch (e.getFieldName()) {
            case "Name" -> nameField;
            case "Contact number" -> contactField;
            default -> null;
        };
        if (target != null) {
            target.requestFocusInWindow();
        }
    }

    private void refreshTable() {
        List<Customer> customers = service.search(searchField.getText());
        adjusting = true;
        model.setRowCount(0);
        for (Customer c : customers) {
            model.addRow(new Object[] {c.getId(), c.getName(), c.getContact(), c.getAddress()});
        }
        // keep the same row selected after a refresh, if it is still listed
        boolean found = false;
        for (int row = 0; row < model.getRowCount(); row++) {
            if (model.getValueAt(row, 0).equals(selectedId)) {
                table.setRowSelectionInterval(row, row);
                found = true;
                break;
            }
        }
        adjusting = false;
        if (!found && selectedId != null && searchField.getText().isBlank()) {
            clearForm();
        }
        countLabel.setText(customers.size() + (customers.size() == 1 ? " customer" : " customers"));
    }

    private void clearForm() {
        adjusting = true;
        table.clearSelection();
        adjusting = false;
        selectedId = null;
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        addressField.setText("");
        setEditing(false);
        nameField.requestFocusInWindow();
    }

    /** editing = a customer is selected: Update/Delete on, Save new off. */
    private void setEditing(boolean editing) {
        saveButton.setEnabled(!editing);
        updateButton.setEnabled(editing);
        deleteButton.setEnabled(editing);
        refreshDefaultButton();
    }

    private void refreshDefaultButton() {
        if (SwingUtilities.getRootPane(this) != null) {
            SwingUtilities.getRootPane(this).setDefaultButton(selectedId == null ? saveButton : updateButton);
        }
    }

    private void setStatus(String text, boolean ok) {
        statusLabel.setForeground(ok ? Theme.SUCCESS : Theme.ERROR);
        statusLabel.setText(text.isBlank() ? " " : text);
    }
}
