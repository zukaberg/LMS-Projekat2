package financeapp.forms;

import financeapp.dao.TransactionDAO;
import financeapp.models.Transaction;
import financeapp.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class FinanceAppPanel extends JPanel {

    private User currentUser;
    private TransactionDAO dao;

    private JTextField amountField;
    private JTextField descriptionField;
    private JComboBox<String> typeCombo;
    private JButton addButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel balanceLabel;

    public FinanceAppPanel(User user) {
        this.currentUser = user;
        this.dao = new TransactionDAO();
        setupUI();
        loadTransactions();
        updateStats();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Finance Tracker");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainContent.add(createInputPanel());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createSummaryPanel());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createTablePanel());

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));

        panel.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        panel.add(typeCombo);

        panel.add(new JLabel("Amount (KM):"));
        amountField = new JTextField();
        panel.add(amountField);

        panel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel(""));
        addButton = new JButton("ADD TRANSACTION");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.addActionListener(e -> handleAddTransaction());
        panel.add(addButton);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        incomeLabel = new JLabel("Income: 0.00 KM", SwingConstants.CENTER);
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        incomeLabel.setForeground(new Color(46, 204, 113));

        expenseLabel = new JLabel("Expense: 0.00 KM", SwingConstants.CENTER);
        expenseLabel.setFont(new Font("Arial", Font.BOLD, 16));
        expenseLabel.setForeground(new Color(231, 76, 60));

        balanceLabel = new JLabel("Balance: 0.00 KM", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(52, 152, 219));

        panel.add(incomeLabel);
        panel.add(expenseLabel);
        panel.add(balanceLabel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        panel.setPreferredSize(new Dimension(0, 350));

        String[] columns = {"ID", "Type", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 13));
        transactionTable.setRowHeight(30);

        // Sakrij ID kolonu
        transactionTable.getColumnModel().getColumn(0).setMinWidth(0);
        transactionTable.getColumnModel().getColumn(0).setMaxWidth(0);
        transactionTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // CRUD buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(230, 126, 34));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> editSelectedTransaction());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteSelectedTransaction());

        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void handleAddTransaction() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText().trim());
            String description = descriptionField.getText().trim();

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description cannot be empty!");
                return;
            }

            Transaction t = new Transaction(type, amount, description);
            t.setUserId(currentUser.getId().toString());

            dao.addTransaction(t);

            amountField.setText("");
            descriptionField.setText("");

            loadTransactions();
            updateStats();

            JOptionPane.showMessageDialog(this, "Transaction added successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite transakciju za editovanje!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String currentType = (String) tableModel.getValueAt(selectedRow, 1);
        String currentAmount = tableModel.getValueAt(selectedRow, 2).toString().replace(" KM", "");
        String currentDescription = (String) tableModel.getValueAt(selectedRow, 3);

        JPanel editPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JComboBox<String> typeComboEdit = new JComboBox<>(new String[]{"Income", "Expense"});
        typeComboEdit.setSelectedItem(currentType);

        JTextField amountFieldEdit = new JTextField(currentAmount);
        JTextField descriptionFieldEdit = new JTextField(currentDescription);

        editPanel.add(new JLabel("Type:"));
        editPanel.add(typeComboEdit);
        editPanel.add(new JLabel("Amount:"));
        editPanel.add(amountFieldEdit);
        editPanel.add(new JLabel("Description:"));
        editPanel.add(descriptionFieldEdit);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Transaction",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newType = (String) typeComboEdit.getSelectedItem();
                double newAmount = Double.parseDouble(amountFieldEdit.getText().trim());
                String newDescription = descriptionFieldEdit.getText().trim();

                if (newDescription.isEmpty() || newAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Nevalidni podaci!");
                    return;
                }

                dao.updateTransaction(id, newType, "", newAmount, newDescription);

                loadTransactions();
                updateStats();

                JOptionPane.showMessageDialog(this, "Transakcija ažurirana!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Unesite validan broj!");
            }
        }
    }

    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite transakciju za brisanje!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati ovu transakciju?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);

            dao.deleteTransaction(id);

            loadTransactions();
            updateStats();

            JOptionPane.showMessageDialog(this, "Transakcija obrisana!");
        }
    }

    private void loadTransactions() {
        ArrayList<Transaction> transactions = dao.getTransactionsByUserId(currentUser.getId().toString());

        tableModel.setRowCount(0);

        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getId().toString(),
                    t.getType(),
                    String.format("%.2f KM", t.getAmount()),
                    t.getDescription()
            });
        }
    }

    private void updateStats() {
        double income = dao.getTotalIncomeByUser(currentUser.getId().toString());
        double expense = dao.getTotalExpenseByUser(currentUser.getId().toString());
        double balance = dao.getBalance(currentUser.getId().toString());

        incomeLabel.setText(String.format("Income: %.2f KM", income));
        expenseLabel.setText(String.format("Expense: %.2f KM", expense));
        balanceLabel.setText(String.format("Balance: %.2f KM", balance));
    }
}
