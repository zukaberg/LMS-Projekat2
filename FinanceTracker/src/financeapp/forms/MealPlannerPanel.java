package financeapp.forms;

import financeapp.dao.MealDAO;
import financeapp.models.Meal;
import financeapp.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MealPlannerPanel extends JPanel {

    private User currentUser;
    private MealDAO dao;

    private JComboBox<String> mealTypeCombo;
    private JTextField nameField;
    private JTextField caloriesField;
    private JTextField proteinField;
    private JTextField carbsField;
    private JTextField fatsField;
    private JTable mealTable;
    private DefaultTableModel tableModel;

    private JLabel todayCaloriesLabel;
    private JLabel todayProteinLabel;
    private JLabel todayCarbsLabel;
    private JLabel todayFatsLabel;

    public MealPlannerPanel(User user) {
        this.currentUser = user;
        this.dao = new MealDAO();
        setupUI();
        loadData();
        updateStats();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Meal Planner");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        mainContent.add(createStatsPanel());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createInputForm());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createTablePanel());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel caloriesCard = createStatCard("Kalorije danas", "0 kcal", new Color(231, 76, 60));
        todayCaloriesLabel = (JLabel) ((JPanel)caloriesCard.getComponent(0)).getComponent(2);

        JPanel proteinCard = createStatCard("Proteini", "0.0 g", new Color(52, 152, 219));
        todayProteinLabel = (JLabel) ((JPanel)proteinCard.getComponent(0)).getComponent(2);

        JPanel carbsCard = createStatCard("Ugljikohidrati", "0.0 g", new Color(230, 126, 34));
        todayCarbsLabel = (JLabel) ((JPanel)carbsCard.getComponent(0)).getComponent(2);

        JPanel fatsCard = createStatCard("Masti", "0.0 g", new Color(46, 204, 113));
        todayFatsLabel = (JLabel) ((JPanel)fatsCard.getComponent(0)).getComponent(2);

        panel.add(caloriesCard);
        panel.add(proteinCard);
        panel.add(carbsCard);
        panel.add(fatsCard);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInputForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dodaj obrok"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);

        fieldsPanel.add(new JLabel("Tip obroka:"));
        mealTypeCombo = new JComboBox<>(new String[]{"Doručak", "Ručak", "Večera", "Užina"});
        fieldsPanel.add(mealTypeCombo);

        fieldsPanel.add(new JLabel("Naziv obroka:"));
        nameField = new JTextField();
        fieldsPanel.add(nameField);

        fieldsPanel.add(new JLabel("Kalorije:"));
        caloriesField = new JTextField();
        fieldsPanel.add(caloriesField);

        fieldsPanel.add(new JLabel("Proteini (g):"));
        proteinField = new JTextField();
        fieldsPanel.add(proteinField);

        fieldsPanel.add(new JLabel("Ugljikohidrati (g):"));
        carbsField = new JTextField();
        fieldsPanel.add(carbsField);

        fieldsPanel.add(new JLabel("Masti (g):"));
        fatsField = new JTextField();
        fieldsPanel.add(fatsField);

        panel.add(fieldsPanel);
        panel.add(Box.createVerticalStrut(10));

        JButton addBtn = new JButton("DODAJ OBROK");
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addMeal());
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.setMaximumSize(new Dimension(200, 35));

        panel.add(addBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Meal History"));
        panel.setPreferredSize(new Dimension(0, 300));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Datum");
        tableModel.addColumn("Tip");
        tableModel.addColumn("Naziv");
        tableModel.addColumn("Kalorije");
        tableModel.addColumn("Proteini");
        tableModel.addColumn("Ugljikohidrati");
        tableModel.addColumn("Masti");

        mealTable = new JTable(tableModel);
        mealTable.setFont(new Font("Arial", Font.PLAIN, 13));
        mealTable.setRowHeight(30);
        mealTable.getColumnModel().getColumn(0).setMinWidth(0);
        mealTable.getColumnModel().getColumn(0).setMaxWidth(0);
        mealTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(mealTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // CRUD buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(230, 126, 34));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> editSelectedMeal());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteSelectedMeal());

        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addMeal() {
        String mealType = (String) mealTypeCombo.getSelectedItem();
        String name = nameField.getText().trim();
        String caloriesStr = caloriesField.getText().trim();
        String proteinStr = proteinField.getText().trim();
        String carbsStr = carbsField.getText().trim();
        String fatsStr = fatsField.getText().trim();

        if (name.isEmpty() || caloriesStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unesite naziv i kalorije!");
            return;
        }

        try {
            int calories = Integer.parseInt(caloriesStr);
            double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
            double carbs = carbsStr.isEmpty() ? 0 : Double.parseDouble(carbsStr);
            double fats = fatsStr.isEmpty() ? 0 : Double.parseDouble(fatsStr);

            Meal meal = new Meal();
            meal.setUserId(currentUser.getId().toString());
            meal.setMealType(mealType);
            meal.setName(name);
            meal.setCalories(calories);
            meal.setProtein(protein);
            meal.setCarbs(carbs);
            meal.setFats(fats);
            meal.setDate(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            meal.setTimestamp(System.currentTimeMillis());

            dao.addMeal(meal);

            nameField.setText("");
            caloriesField.setText("");
            proteinField.setText("");
            carbsField.setText("");
            fatsField.setText("");

            loadData();
            updateStats();

            JOptionPane.showMessageDialog(this, "Obrok dodat!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Unesite validne brojeve!");
        }
    }

    private void editSelectedMeal() {
        int selectedRow = mealTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite red za editovanje!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String currentType = (String) tableModel.getValueAt(selectedRow, 2);
        String currentName = (String) tableModel.getValueAt(selectedRow, 3);
        String currentCalories = tableModel.getValueAt(selectedRow, 4).toString();
        String currentProtein = tableModel.getValueAt(selectedRow, 5).toString().replace(" g", "");
        String currentCarbs = tableModel.getValueAt(selectedRow, 6).toString().replace(" g", "");
        String currentFats = tableModel.getValueAt(selectedRow, 7).toString().replace(" g", "");

        JPanel editPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        JComboBox<String> newTypeCombo = new JComboBox<>(new String[]{"Doručak", "Ručak", "Večera", "Užina"});
        newTypeCombo.setSelectedItem(currentType);

        JTextField newNameField = new JTextField(currentName);
        JTextField newCaloriesField = new JTextField(currentCalories);
        JTextField newProteinField = new JTextField(currentProtein);
        JTextField newCarbsField = new JTextField(currentCarbs);
        JTextField newFatsField = new JTextField(currentFats);

        editPanel.add(new JLabel("Tip:"));
        editPanel.add(newTypeCombo);
        editPanel.add(new JLabel("Naziv:"));
        editPanel.add(newNameField);
        editPanel.add(new JLabel("Kalorije:"));
        editPanel.add(newCaloriesField);
        editPanel.add(new JLabel("Proteini (g):"));
        editPanel.add(newProteinField);
        editPanel.add(new JLabel("Ugljikohidrati (g):"));
        editPanel.add(newCarbsField);
        editPanel.add(new JLabel("Masti (g):"));
        editPanel.add(newFatsField);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Meal",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newType = (String) newTypeCombo.getSelectedItem();
                String newName = newNameField.getText().trim();
                int newCalories = Integer.parseInt(newCaloriesField.getText().trim());
                double newProtein = Double.parseDouble(newProteinField.getText().trim());
                double newCarbs = Double.parseDouble(newCarbsField.getText().trim());
                double newFats = Double.parseDouble(newFatsField.getText().trim());

                if (newName.isEmpty() || newCalories <= 0) {
                    JOptionPane.showMessageDialog(this, "Nevalidni podaci!");
                    return;
                }

                dao.updateMeal(id, newType, newName, newCalories, newProtein, newCarbs, newFats);

                loadData();
                updateStats();

                JOptionPane.showMessageDialog(this, "Obrok ažuriran!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Unesite validne brojeve!");
            }
        }
    }

    private void deleteSelectedMeal() {
        int selectedRow = mealTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite red za brisanje!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati ovaj obrok?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);

            dao.deleteMeal(id);

            loadData();
            updateStats();

            JOptionPane.showMessageDialog(this, "Obrok obrisan!");
        }
    }

    private void loadData() {
        ArrayList<Meal> meals = dao.getMealsByUserId(currentUser.getId().toString());

        tableModel.setRowCount(0);

        for (Meal m : meals) {
            tableModel.addRow(new Object[]{
                    m.getId().toString(),
                    m.getDate(),
                    m.getMealType(),
                    m.getName(),
                    m.getCalories(),
                    String.format("%.1f g", m.getProtein()),
                    String.format("%.1f g", m.getCarbs()),
                    String.format("%.1f g", m.getFats())
            });
        }
    }

    private void updateStats() {
        int calories = dao.getTodayCalories(currentUser.getId().toString());
        double protein = dao.getTodayProtein(currentUser.getId().toString());
        double carbs = dao.getTodayCarbs(currentUser.getId().toString());
        double fats = dao.getTodayFats(currentUser.getId().toString());

        todayCaloriesLabel.setText(calories + " kcal");
        todayProteinLabel.setText(String.format("%.1f g", protein));
        todayCarbsLabel.setText(String.format("%.1f g", carbs));
        todayFatsLabel.setText(String.format("%.1f g", fats));
    }
}
