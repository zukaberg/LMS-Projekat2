package financeapp.forms;

import financeapp.models.User;
import financeapp.utils.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MyTrackersPanel extends JPanel {

    private User currentUser;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private JPanel sidebar; // Dodaj field za sidebar

    public MyTrackersPanel(User user) {
        this.currentUser = user;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("My Trackers");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sidebar - sačuvaj referencu
        sidebar = createSidebar();
        mainContent.add(sidebar, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        contentArea.add(new SleepTrackerPanel(currentUser), "sleep");
        contentArea.add(new MealPlannerPanel(currentUser), "meal");
        contentArea.add(new StudyPlannerPanel(currentUser), "study");

        mainContent.add(contentArea, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        cardLayout.show(contentArea, "sleep");
    }

    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(ThemeManager.getPanelColor());
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        JLabel title = new JLabel("Trackers:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(ThemeManager.getForegroundColor());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(title);
        sidebarPanel.add(Box.createVerticalStrut(15));

        JButton sleepBtn = createSidebarButton("Sleep Tracker");
        JButton mealBtn = createSidebarButton("Meal Planner");
        JButton studyBtn = createSidebarButton("Study Planner");

        sleepBtn.addActionListener(e -> cardLayout.show(contentArea, "sleep"));
        mealBtn.addActionListener(e -> cardLayout.show(contentArea, "meal"));
        studyBtn.addActionListener(e -> cardLayout.show(contentArea, "study"));

        sidebarPanel.add(sleepBtn);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(mealBtn);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(studyBtn);

        return sidebarPanel;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Dark mode theme
        btn.setBackground(ThemeManager.getInputBackgroundColor());
        btn.setForeground(ThemeManager.getForegroundColor());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ThemeManager.ACCENT_BLUE);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ThemeManager.getInputBackgroundColor());
                btn.setForeground(ThemeManager.getForegroundColor());
            }
        });

        return btn;
    }

    // PUBLIC metoda za refresh theme
    public void refreshTheme() {
        if (sidebar != null) {
            refreshSidebar(sidebar);
        }

        // Refresh header
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                ThemeManager.applyTheme((JPanel) comp);
            }
        }

        revalidate();
        repaint();
    }

    private void refreshSidebar(JPanel sidebarPanel) {
        sidebarPanel.setBackground(ThemeManager.getPanelColor());

        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(ThemeManager.getInputBackgroundColor());
                btn.setForeground(ThemeManager.getForegroundColor());
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            } else if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getForegroundColor());
            }
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }
}
