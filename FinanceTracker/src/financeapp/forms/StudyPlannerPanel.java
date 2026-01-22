package financeapp.forms;


import financeapp.dao.StudySessionDAO;
import financeapp.models.StudySession;
import financeapp.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StudyPlannerPanel extends JPanel {

    private User currentUser;
    private StudySessionDAO dao;

    private JTextField subjectField;
    private JTextField hoursField;
    private JTextArea notesArea;
    private JTable studyTable;
    private DefaultTableModel tableModel;
    private JLabel todayHoursLabel;
    private JLabel avgHoursLabel;
    private JLabel daysStudiedLabel;

    public StudyPlannerPanel(User user) {
        this.currentUser = user;
        this.dao = new StudySessionDAO();
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

        JLabel headerLabel = new JLabel("Study Planner");
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

        // Today hours
        JPanel todayCard = createStatCard("Učeno danas", "0.0 h", new Color(52, 152, 219));
        todayHoursLabel = (JLabel) ((JPanel)todayCard.getComponent(0)).getComponent(2);

        // Average hours
        JPanel avgCard = createStatCard("Prosjek (7 dana)", "0.0 h", new Color(155, 89, 182));
        avgHoursLabel = (JLabel) ((JPanel)avgCard.getComponent(0)).getComponent(2);

        // Days studied
        JPanel daysCard = createStatCard("Dana učeno", "0", new Color(46, 204, 113));
        daysStudiedLabel = (JLabel) ((JPanel)daysCard.getComponent(0)).getComponent(2);

        panel.add(todayCard);
        panel.add(avgCard);
        panel.add(daysCard);

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
                BorderFactory.createTitledBorder("Dodaj sesiju učenja"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        fieldsPanel.setBackground(Color.WHITE);

        fieldsPanel.add(new JLabel("Predmet:"));
        subjectField = new JTextField();
        fieldsPanel.add(subjectField);

        fieldsPanel.add(new JLabel("Sati:"));
        hoursField = new JTextField();
        fieldsPanel.add(hoursField);

        fieldsPanel.add(new JLabel("Bilješke:"));
        notesArea = new JTextArea(2, 20);
        notesArea.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        fieldsPanel.add(notesScroll);

        panel.add(fieldsPanel);
        panel.add(Box.createVerticalStrut(10));

        JButton addBtn = new JButton("DODAJ");
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.setBackground(new Color(46, 204, 113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> addStudySession());
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(addBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Study History"));
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
        tableModel.addColumn("Predmet");
        tableModel.addColumn("Sati");
        tableModel.addColumn("Bilješke");

        studyTable = new JTable(tableModel);
        studyTable.setFont(new Font("Arial", Font.PLAIN, 13));
        studyTable.setRowHeight(30);

        // Hide ID column
        studyTable.getColumnModel().getColumn(0).setMinWidth(0);
        studyTable.getColumnModel().getColumn(0).setMaxWidth(0);
        studyTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(studyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // CRUD buttons
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

    private void addStudySession() {
        String subject = subjectField.getText().trim();
        String hoursStr = hoursField.getText().trim();
        String notes = notesArea.getText().trim();

        if (subject.isEmpty() || hoursStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Unesite predmet i sate!");
            return;
        }

        try {
            double hours = Double.parseDouble(hoursStr);

            if (hours <= 0 || hours > 24) {
                JOptionPane.showMessageDialog(this, "Sati moraju biti između 0 i 24!");
                return;
            }

            StudySession entry = new StudySession();
            entry.setUserId(currentUser.getId().toString());
            entry.setSubject(subject);
            entry.setHours(hours);
            entry.setNotes(notes);
            entry.setDate(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            entry.setTimestamp(System.currentTimeMillis());

            dao.addStudySession(entry);

            subjectField.setText("");
            hoursField.setText("");
            notesArea.setText("");

            loadData();
            updateStats();

            JOptionPane.showMessageDialog(this, "Study entry dodat!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Unesite validan broj!");
        }
    }

    private void editSelectedEntry() {
        int selectedRow = studyTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Izaberite red za editovanje!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String currentSubject = (String) tableModel.getValueAt(selectedRow, 2);
        String currentHours = ((String) tableModel.getValueAt(selectedRow, 3)).replace(" h", "");
        String currentNotes = (String) tableModel.getValueAt(selectedRow, 4);

        JPanel editPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField newSubjectField = new JTextField(currentSubject);
        JTextField newHoursField = new JTextField(currentHours);
        JTextArea newNotesArea = new JTextArea(currentNotes, 3, 20);
        newNotesArea.setLineWrap(true);

        editPanel.add(new JLabel("Predmet:"));
        editPanel.add(newSubjectField);
        editPanel.add(new JLabel("Sati:"));
        editPanel.add(newHoursField);
        editPanel.add(new JLabel("Bilješke:"));
        editPanel.add(new JScrollPane(newNotesArea));

        int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Study Entry",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newSubject = newSubjectField.getText().trim();
                double newHours = Double.parseDouble(newHoursField.getText().trim());
                String newNotes = newNotesArea.getText().trim();

                if (newSubject.isEmpty() || newHours <= 0 || newHours > 24) {
                    JOptionPane.showMessageDialog(this, "Nevalidni podaci!");
                    return;
                }

                dao.updateStudySession(id, newSubject, newHours, newNotes);

                loadData();
                updateStats();

                JOptionPane.showMessageDialog(this, "Unos ažuriran!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Unesite validan broj!");
            }
        }
    }

    private void deleteSelectedEntry() {
        int selectedRow = studyTable.getSelectedRow();

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

            dao.deleteStudySession(id);

            loadData();
            updateStats();

            JOptionPane.showMessageDialog(this, "Unos obrisan!");
        }
    }

    private void loadData() {
        ArrayList<StudySession> entries = dao.getStudyEntriesByUserId(currentUser.getId().toString());

        tableModel.setRowCount(0);

        for (StudySession e : entries) {
            tableModel.addRow(new Object[]{
                    e.getId().toString(),
                    e.getDate(),
                    e.getSubject(),
                    String.format("%.1f h", e.getHours()),
                    e.getNotes()
            });
        }
    }

    private void updateStats() {
        double todayHours = dao.getTodayStudyHours(currentUser.getId().toString());
        double avgHours = dao.getAverageStudyHours(currentUser.getId().toString(), 7);
        int daysStudied = dao.getTotalDaysStudied(currentUser.getId().toString());

        todayHoursLabel.setText(String.format("%.1f h", todayHours));
        avgHoursLabel.setText(String.format("%.1f h", avgHours));
        daysStudiedLabel.setText(String.valueOf(daysStudied));
    }
}
