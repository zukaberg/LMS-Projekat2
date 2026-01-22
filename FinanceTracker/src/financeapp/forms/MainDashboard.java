package financeapp.forms;

import financeapp.dao.UserPreferencesDAO;
import financeapp.models.User;
import financeapp.models.UserPreferences;
import financeapp.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private UserPreferencesDAO preferencesDAO;

    public MainDashboard(User user) {
        this.currentUser = user;
        this.preferencesDAO = new UserPreferencesDAO();

        // Učitaj preferences i primijeni temu
        UserPreferences prefs = preferencesDAO.getPreferences(user.getId().toString());
        ThemeManager.setDarkMode(prefs.isDarkMode());

        setupUI();

        // Primijeni temu nakon što je UI kreiran - rekurzivno na sve
        SwingUtilities.invokeLater(() -> {
            ThemeManager.applyTheme(this);
            revalidate();
            repaint();
        });
    }

    private void setupUI() {
        setTitle("Life Management System - " + currentUser.getUsername());
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout());

        // Top navigation bar
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Dodaj sve panele
        contentPanel.add(new ViewProfilePanel(currentUser), "profile");
        contentPanel.add(new FinanceAppPanel(currentUser), "finance");
        contentPanel.add(new MyTrackersPanel(currentUser), "trackers");

        add(contentPanel, BorderLayout.CENTER);

        // Prikaži prvi panel
        cardLayout.show(contentPanel, "profile");

        setVisible(true);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(41, 128, 185));
        topBar.setPreferredSize(new Dimension(0, 80));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Title
        JLabel titleLabel = new JLabel("Life Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        navPanel.setOpaque(false);

        JButton profileBtn = createNavButton("View Profile");
        JButton financeBtn = createNavButton("Financeapp");
        JButton trackersBtn = createNavButton("My Trackers");

        profileBtn.addActionListener(e -> cardLayout.show(contentPanel, "profile"));
        financeBtn.addActionListener(e -> cardLayout.show(contentPanel, "finance"));
        trackersBtn.addActionListener(e -> cardLayout.show(contentPanel, "trackers"));

        navPanel.add(profileBtn);
        navPanel.add(financeBtn);
        navPanel.add(trackersBtn);

        topBar.add(navPanel, BorderLayout.CENTER);

        // Right panel: Dark Mode + User + Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Dark Mode Toggle
        JButton darkModeBtn = new JButton(ThemeManager.isDarkMode() ? "Light Mode" : "Dark Mode");
        darkModeBtn.setBackground(new Color(52, 73, 94));
        darkModeBtn.setForeground(Color.WHITE);
        darkModeBtn.setFocusPainted(false);
        darkModeBtn.setBorderPainted(false);
        darkModeBtn.setPreferredSize(new Dimension(110, 35));
        darkModeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        darkModeBtn.addActionListener(e -> toggleDarkMode(darkModeBtn));

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setPreferredSize(new Dimension(80, 35));
        logoutBtn.addActionListener(e -> handleLogout());

        rightPanel.add(darkModeBtn);
        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);

        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 152, 219));
            }
        });

        return btn;
    }

    private void toggleDarkMode(JButton darkModeBtn) {
        // Toggle dark mode
        ThemeManager.setDarkMode(!ThemeManager.isDarkMode());

        // Sačuvaj u bazu
        preferencesDAO.toggleDarkMode(currentUser.getId().toString());

        // Ažuriraj dugme text
        darkModeBtn.setText(ThemeManager.isDarkMode() ? "Light Mode" : "Dark Mode");

        // Primijeni temu rekurzivno
        SwingUtilities.invokeLater(() -> {
            ThemeManager.applyTheme(this);

            // Posebno refresh-uj MyTrackersPanel
            for (Component comp : contentPanel.getComponents()) {
                if (comp instanceof MyTrackersPanel) {
                    ((MyTrackersPanel) comp).refreshTheme();
                }
            }

            revalidate();
            repaint();
        });

        // Poruka
        JOptionPane.showMessageDialog(this,
                ThemeManager.isDarkMode() ? "Dark mode aktiviran!" : "Light mode aktiviran!");
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Da li ste sigurni da zelite izaci?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm();
        }
    }
}
