package financeapp.utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ThemeManager {

    // Light mode
    public static final Color LIGHT_BG = Color.WHITE;
    public static final Color LIGHT_FG = Color.BLACK;
    public static final Color LIGHT_PANEL = new Color(236, 240, 241);
    public static final Color LIGHT_BORDER = new Color(220, 220, 220);
    public static final Color LIGHT_INPUT_BG = Color.WHITE;

    // Dark mode
    public static final Color DARK_BG = new Color(30, 30, 30);
    public static final Color DARK_FG = new Color(220, 220, 220);
    public static final Color DARK_PANEL = new Color(45, 45, 45);
    public static final Color DARK_BORDER = new Color(60, 60, 60);
    public static final Color DARK_INPUT_BG = new Color(50, 50, 50);

    // Accent colors
    public static final Color ACCENT_BLUE = new Color(52, 152, 219);
    public static final Color ACCENT_GREEN = new Color(46, 204, 113);
    public static final Color ACCENT_RED = new Color(231, 76, 60);
    public static final Color ACCENT_ORANGE = new Color(230, 126, 34);
    public static final Color ACCENT_PURPLE = new Color(155, 89, 182);

    private static boolean darkMode = false;

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static Color getTitleColor() {
        return darkMode ? Color.WHITE : Color.BLACK;
    }

    public static Color getBackgroundColor() {
        return darkMode ? DARK_BG : LIGHT_BG;
    }

    public static Color getForegroundColor() {
        return darkMode ? DARK_FG : LIGHT_FG;
    }

    public static Color getPanelColor() {
        return darkMode ? DARK_PANEL : LIGHT_PANEL;
    }

    public static Color getBorderColor() {
        return darkMode ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color getInputBackgroundColor() {
        return darkMode ? DARK_INPUT_BG : LIGHT_INPUT_BG;
    }

    // Theme on panel - apply
    public static void applyTheme(JPanel panel) {
        panel.setBackground(getBackgroundColor());
        panel.setForeground(getForegroundColor());

        for (Component comp : panel.getComponents()) {
            applyThemeToComponent(comp);
        }
    }

    // Theme on component - apply
    public static void applyThemeToComponent(Component comp) {
        if (comp instanceof JPanel) {
            JPanel p = (JPanel) comp;

            Color currentBg = p.getBackground();
            if (currentBg.equals(Color.WHITE) || currentBg.equals(new Color(236, 240, 241))
                    || currentBg.equals(new Color(30, 30, 30)) || currentBg.equals(new Color(45, 45, 45))) {
                p.setBackground(getBackgroundColor());
            }

            p.setForeground(getForegroundColor());

            if (p.getBorder() instanceof javax.swing.border.CompoundBorder) {
                javax.swing.border.CompoundBorder compoundBorder = (javax.swing.border.CompoundBorder) p.getBorder();

                if (compoundBorder.getOutsideBorder() instanceof TitledBorder) {
                    TitledBorder titledBorder = (TitledBorder) compoundBorder.getOutsideBorder();
                    titledBorder.setTitleColor(getTitleColor());
                    titledBorder.setBorder(BorderFactory.createLineBorder(getBorderColor()));
                }
            } else if (p.getBorder() instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder) p.getBorder();
                titledBorder.setTitleColor(getTitleColor());
                titledBorder.setBorder(BorderFactory.createLineBorder(getBorderColor()));
            }

            for (Component child : p.getComponents()) {
                applyThemeToComponent(child);
            }
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;

            Color currentFg = label.getForeground();

            boolean isAccentColor =
                    currentFg.equals(ACCENT_BLUE) ||
                            currentFg.equals(ACCENT_GREEN) ||
                            currentFg.equals(ACCENT_RED) ||
                            currentFg.equals(ACCENT_ORANGE) ||
                            currentFg.equals(ACCENT_PURPLE) ||
                            currentFg.equals(new Color(52, 152, 219)) ||
                            currentFg.equals(new Color(46, 204, 113)) ||
                            currentFg.equals(new Color(231, 76, 60)) ||
                            currentFg.equals(new Color(230, 126, 34)) ||
                            currentFg.equals(new Color(155, 89, 182));

            if (!isAccentColor) {
                label.setForeground(getForegroundColor());
            }

        } else if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            Color currentBg = btn.getBackground();
            if (currentBg.equals(Color.WHITE) || currentBg.equals(new Color(238, 238, 238))) {
                btn.setBackground(getPanelColor());
                btn.setForeground(getForegroundColor());
                btn.setBorder(BorderFactory.createLineBorder(getBorderColor()));
            }
        } else if (comp instanceof JTextField) {
            JTextField field = (JTextField) comp;
            field.setBackground(getInputBackgroundColor());
            field.setForeground(getForegroundColor());
            field.setCaretColor(getForegroundColor());
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getBorderColor()),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        } else if (comp instanceof JPasswordField) {
            JPasswordField field = (JPasswordField) comp;
            field.setBackground(getInputBackgroundColor());
            field.setForeground(getForegroundColor());
            field.setCaretColor(getForegroundColor());
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getBorderColor()),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        } else if (comp instanceof JTextArea) {
            JTextArea area = (JTextArea) comp;
            area.setBackground(getInputBackgroundColor());
            area.setForeground(getForegroundColor());
            area.setCaretColor(getForegroundColor());
        } else if (comp instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) comp;
            combo.setBackground(getInputBackgroundColor());
            combo.setForeground(getForegroundColor());
        } else if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            table.setBackground(getBackgroundColor());
            table.setForeground(getForegroundColor());
            table.setGridColor(getBorderColor());
            table.setSelectionBackground(darkMode ? new Color(60, 60, 60) : new Color(200, 200, 200));
            table.setSelectionForeground(getForegroundColor());

            // Table header
            if (table.getTableHeader() != null) {
                table.getTableHeader().setBackground(getPanelColor());
                table.getTableHeader().setForeground(getForegroundColor());
            }
        } else if (comp instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) comp;
            scroll.setBackground(getBackgroundColor());
            scroll.getViewport().setBackground(getBackgroundColor());
            scroll.setBorder(BorderFactory.createLineBorder(getBorderColor()));

            Component view = scroll.getViewport().getView();
            if (view != null) {
                applyThemeToComponent(view);
            }
        } else if (comp instanceof JProgressBar) {
            JProgressBar bar = (JProgressBar) comp;
            bar.setBackground(getPanelColor());
            bar.setBorder(BorderFactory.createLineBorder(getBorderColor()));
        }
    }

    // Apply theme on whole frame
    public static void applyTheme(JFrame frame) {
        Container contentPane = frame.getContentPane();
        applyThemeToContainer(contentPane);
        frame.repaint();
    }

    private static void applyThemeToContainer(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                applyTheme((JPanel) comp);
            } else if (comp instanceof Container) {
                applyThemeToContainer((Container) comp);
            } else {
                applyThemeToComponent(comp);
            }
        }
    }
}
