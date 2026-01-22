package financeapp.forms;

import financeapp.dao.SleepDAO;
import financeapp.models.SleepEntry;
import financeapp.models.User;
import financeapp.utils.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SleepTrackerPanel extends JPanel {

    private User currentUser;
    private SleepDAO dao;

    private final double RECOMMENDED_SLEEP = 8.0;

    private JTextField hoursField;
    private JComboBox<String> qualityCombo;
    private JProgressBar progressBar;
    private JLabel todaySleepLabel;
    private JLabel averageSleepLabel;
    private JLabel daysTrackedLabel;
    private JTable sleepTable;
    private DefaultTableModel tableModel;

    public SleepTrackerPanel(User user) {
        this.currentUser = user;
        this.dao = new SleepDAO();
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

        JLabel headerLabel = new JLabel("Sleep Tracker");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        mainContent.add(createStatsRow());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createProgressPanel());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createQuickAddPanel());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createInputForm());
        mainContent.add(Box.createVerticalStrut(20));
        mainContent.add(createTablePanel());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsRow() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Today sleep card
        JPanel todayCard = new JPanel(new BorderLayout());
        todayCard.setBackground(Color.WHITE);
        todayCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel todayContent = new JPanel();
        todayContent.setLayout(new BoxLayout(todayContent, BoxLayout.Y_AXIS));
        todayContent.setBackground(Color.WHITE);

        JLabel todayTitle = new JLabel("Spavano sinoć");
        todayTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        todayTitle.setForeground(Color.GRAY);
        todayTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        todaySleepLabel = new JLabel("0.0 h");
        todaySleepLabel.setFont(new Font("Arial", Font.BOLD, 20));
        todaySleepLabel.setForeground(new Color(52, 152, 219));
        todaySleepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        todayContent.add(todayTitle);
        todayContent.add(Box.createVerticalStrut(8));
        todayContent.add(todaySleepLabel);
        todayCard.add(todayContent, BorderLayout.CENTER);

        // Average sleep card
        JPanel avgCard = new JPanel(new BorderLayout());
        avgCard.setBackground(Color.WHITE);
        avgCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel avgContent = new JPanel();
        avgContent.setLayout(new BoxLayout(avgContent, BoxLayout.Y_AXIS));
        avgContent.setBackground(Color.WHITE);

        JLabel avgTitle = new JLabel("Prosjek (7 dana)");
        avgTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        avgTitle.setForeground(Color.GRAY);
        avgTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        averageSleepLabel = new JLabel("0.0 h");
        averageSleepLabel.setFont(new Font("Arial", Font.BOLD, 20));
        averageSleepLabel.setForeground(new Color(155, 89, 182));
        averageSleepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        avgContent.add(avgTitle);
        avgContent.add(Box.createVerticalStrut(8));
        avgContent.add(averageSleepLabel);
        avgCard.add(avgContent, BorderLayout.CENTER);

        // Days tracked card
        JPanel daysCard = new JPanel(new BorderLayout());
        daysCard.setBackground(Color.WHITE);
        daysCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel daysContent = new JPanel();
        daysContent.setLayout(new BoxLayout(daysContent, BoxLayout.Y_AXIS));
        daysContent.setBackground(Color.WHITE);

        JLabel daysTitle = new JLabel("Dana praćeno");
        daysTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        daysTitle.setForeground(Color.GRAY);
        daysTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        daysTrackedLabel = new JLabel("0");
        daysTrackedLabel.setFont(new Font("Arial", Font.BOLD, 20));
        daysTrackedLabel.setForeground(new Color(46, 204, 113));
        daysTrackedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        daysContent.add(daysTitle);
        daysContent.add(Box.createVerticalStrut(8));
        daysContent.add(daysTrackedLabel);
        daysCard.add(daysContent, BorderLayout.CENTER);

        panel.add(todayCard);
        panel.add(avgCard);
        panel.add(daysCard);

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel label = new JLabel("Preporučeno spavanje (8 sati):");
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));

        progressBar = new JProgressBar(0, (int)(RECOMMENDED_SLEEP * 10));
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setPreferredSize(new Dimension(0, 30));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        progressBar.setForeground(new Color(52, 152, 219));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(progressBar);

        return panel;
    }

    private JPanel createQuickAddPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel("Brzo dodavanje:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        buttonsPanel.add(createQuickButton("5h", 5.0));
        buttonsPanel.add(createQuickButton("6h", 6.0));
        buttonsPanel.add(createQuickButton("7h", 7.0));
        buttonsPanel.add(createQuickButton("8h", 8.0));

        panel.add(buttonsPanel);

        return panel;
    }

    private JButton createQuickButton(String text, double hours) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 152, 219));
            }
        });

        btn.addActionListener(e -> quickAddSleep(hours));

        return btn;
    }

    private JPanel createInputForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.getPanelColor());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                        "Detaljno dodavanje",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Arial", Font.BOLD, 12),
                        ThemeManager.getTitleColor()),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel fieldsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        fieldsPanel.setBackground(Color.WHITE);

        // Hours
        JPanel hoursPanel = new JPanel(new BorderLayout(5, 0));
        hoursPanel.setBackground(Color.WHITE);
        hoursPanel.add(new JLabel("Sati spavanja:"), BorderLayout.NORTH);
        hoursField = new JTextField();
        hoursPanel.add(hoursField, BorderLayout.CENTER);

        // Quality
        JPanel qualityPanel = new JPanel(new BorderLayout(5, 0));
        qualityPanel.setBackground(Color.WHITE);
        qualityPanel.add(new JLabel("Kvalitet sna:"), BorderLayout.NORTH);
        qualityCombo = new JComboBox<>(new String[]{"Odličan", "Dobar", "Loš"});
        qualityPanel.add(qualityCombo, BorderLayout.CENTER);

        // Button
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(Box.createVerticalStrut(20), BorderLayout.NORTH);
        JButton addBtn = new JButton("DODAJ");
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addSleepEntry());
        buttonPanel.add(addBtn, BorderLayout.CENTER);

        fieldsPanel.add(hoursPanel);
        fieldsPanel.add(qualityPanel);
        fieldsPanel.add(buttonPanel);

        panel.add(fieldsPanel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Sleep History"));
        panel.setPreferredSize(new Dimension(0, 300));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable direct cell editing
            }
        };

        tableModel.addColumn("ID");
        tableModel.addColumn("Datum");
        tableModel.addColumn("Sati");
        tableModel.addColumn("Kvalitet");

        sleepTable = new JTable(tableModel);
        sleepTable.setFont(new Font("Arial", Font.PLAIN, 13));
        sleepTable.setRowHeight(30);
        sleepTable.getColumnModel().getColumn(0).setMinWidth(0);
        sleepTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sleepTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(sleepTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // CRUD buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(Color.WHITE);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(new Color(230, 126, 34));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> editSelectedEntry());

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteSelectedEntry());

        buttonsPanel.add(editBtn);
        buttonsPanel.add(deleteBtn);

        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void quickAddSleep(double hours) {
        if (dao.hasTodayEntry(currentUser.getId().toString())) {
            JOptionPane.showMessageDialog(this,
                    "Već ste unijeli podatke o spavanju za danas!\nMožete imati samo jedan unos po danu.",
                    "Dupli unos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] options = {"Odličan", "Dobar", "Loš"};
        String quality = (String) JOptionPane.showInputDialog(
                this,
                "Kako ste spavali?",
                "Kvalitet sna",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (quality == null) return;

        SleepEntry entry = new SleepEntry(hours, quality);
        entry.setUserId(currentUser.getId().toString());
        entry.setDate(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

        dao.addSleepEntry(entry);

        loadData();
        updateStats();

        JOptionPane.showMessageDialog(this, "Dodato: " + hours + "h (" + quality + ")");
    }

    private void addSleepEntry() {
        if (dao.hasTodayEntry(currentUser.getId().toString())) {
            JOptionPane.showMessageDialog(this,
                    "Već ste unijeli podatke o spavanju za danas!",
                    "Dupli unos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hoursStr = hoursField.getText().trim();
        String quality = (String) qualityCombo.getSelectedItem();

        if (hoursStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unesite broj sati!");
            return;
        }

        try {
            double hours = Double.parseDouble(hoursStr);

            if (hours <= 0 || hours > 24) {
                JOptionPane.showMessageDialog(this, "Sati moraju biti između 0 i 24!");
                return;
            }

            SleepEntry entry = new SleepEntry(hours, quality);
            entry.setUserId(currentUser.getId().toString());
            entry.setDate(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

            dao.addSleepEntry(entry);

            hoursField.setText("");

            loadData();
            updateStats();

            JOptionPane.showMessageDialog(this, "Sleep entry dodat!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Unesite validan broj!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedEntry() {
        int selectedRow = sleepTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite red za editovanje!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0); // String umjesto int
        String currentHours = ((String) tableModel.getValueAt(selectedRow, 2)).replace(" h", "");
        String currentQuality = (String) tableModel.getValueAt(selectedRow, 3);

        JPanel editPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        JTextField newHoursField = new JTextField(currentHours);
        JComboBox<String> newQualityCombo = new JComboBox<>(new String[]{"Odličan", "Dobar", "Loš"});
        newQualityCombo.setSelectedItem(currentQuality);

        editPanel.add(new JLabel("Sati spavanja:"));
        editPanel.add(newHoursField);
        editPanel.add(new JLabel("Kvalitet sna:"));
        editPanel.add(newQualityCombo);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Sleep Entry",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double newHours = Double.parseDouble(newHoursField.getText().trim());
                String newQuality = (String) newQualityCombo.getSelectedItem();

                if (newHours <= 0 || newHours > 24) {
                    JOptionPane.showMessageDialog(this, "Sati moraju biti između 0 i 24!");
                    return;
                }

                dao.updateSleepEntry(id, newHours, newQuality);

                loadData();
                updateStats();

                JOptionPane.showMessageDialog(this, "Unos ažuriran!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Unesite validan broj!");
            }
        }
    }

    private void deleteSelectedEntry() {
        int selectedRow = sleepTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite red za brisanje!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da želite obrisati ovaj unos?",
                "Potvrda brisanja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);

            dao.deleteSleepEntry(id);

            loadData();
            updateStats();

            JOptionPane.showMessageDialog(this, "Unos obrisan!");
        }
    }

    private void loadData() {
        ArrayList<SleepEntry> entries = dao.getSleepEntriesByUserId(currentUser.getId().toString());

        tableModel.setRowCount(0);

        for (SleepEntry e : entries) {
            tableModel.addRow(new Object[]{
                    e.getId().toString(),
                    e.getDate(),
                    String.format("%.1f h", e.getHours()),
                    e.getQuality()
            });
        }
    }

    private void updateStats() {
        double todaySleep = dao.getTodaySleepByUser(currentUser.getId().toString());
        double avgSleep = dao.getAverageSleepByUser(currentUser.getId().toString(), 7);
        int daysTracked = dao.getTotalDaysTracked(currentUser.getId().toString());

        todaySleepLabel.setText(String.format("%.1f h", todaySleep));
        averageSleepLabel.setText(String.format("%.1f h", avgSleep));
        daysTrackedLabel.setText(String.valueOf(daysTracked));

        int progress = (int)((todaySleep / RECOMMENDED_SLEEP) * 100);
        progressBar.setValue((int)(todaySleep * 10));
        progressBar.setString(progress + "%");

        if (todaySleep >= 7 && todaySleep <= 9) {
            progressBar.setForeground(new Color(46, 204, 113));
        } else if (todaySleep >= 6 && todaySleep < 7) {
            progressBar.setForeground(new Color(243, 156, 18));
        } else {
            progressBar.setForeground(new Color(231, 76, 60));
        }
    }
}
