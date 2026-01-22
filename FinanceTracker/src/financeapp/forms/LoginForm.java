package financeapp.forms;

import financeapp.dao.UserDAO;
import financeapp.dao.UserPreferencesDAO;
import financeapp.models.User;
import financeapp.models.UserPreferences;
import financeapp.utils.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private UserDAO userDAO;

    // Boje
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);

    public LoginForm() {
        userDAO = new UserDAO();
        setupUI();
    }

    private void setupUI() {
        setTitle("LMS - Life Managment System");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                //Backgroung
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(41, 128, 185),
                        0, getHeight(), new Color(52, 152, 219)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        headerPanel.setPreferredSize(new Dimension(0, 150));
        headerPanel.setLayout(new GridBagLayout());

        // Title
        JLabel titleLabel = new JLabel("LIFE MANAGMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage your life");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // Username/Email
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username / Email");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        usernameLabel.setForeground(new Color(50, 50, 50));
        contentPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        usernameField = createStyledTextField();
        contentPanel.add(usernameField, gbc);

        // Email
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 8, 0);
        JLabel emailLabel = new JLabel("Email (samo za registraciju)");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        emailLabel.setForeground(new Color(100, 100, 100));
        contentPanel.add(emailLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(8, 0, 8, 0);
        emailField = createStyledTextField();
        contentPanel.add(emailField, gbc);

        // Password field
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 8, 0);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passwordLabel.setForeground(new Color(50, 50, 50));
        contentPanel.add(passwordLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 8, 0);
        passwordField = createStyledPasswordField();
        contentPanel.add(passwordField, gbc);

        // Login button
        gbc.gridy = 6;
        gbc.insets = new Insets(30, 0, 10, 0);
        JButton loginBtn = createStyledButton("PRIJAVI SE", PRIMARY_COLOR);
        loginBtn.addActionListener(e -> handleLogin());
        contentPanel.add(loginBtn, gbc);

        // Register button
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        JButton registerBtn = createStyledButton("REGISTRUJ SE", SUCCESS_COLOR);
        registerBtn.addActionListener(e -> handleRegister());
        contentPanel.add(registerBtn, gbc);

        return contentPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 45));
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(0, 45));
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });

        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStyledMessage("Molimo unesite username i password!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            // Učitaj preferences
            UserPreferencesDAO prefsDAO = new UserPreferencesDAO();
            UserPreferences prefs = prefsDAO.getPreferences(user.getId().toString());
            ThemeManager.setDarkMode(prefs.isDarkMode());

            showStyledMessage("Dobrodošli, " + user.getUsername() + "!", "Uspješan Login", JOptionPane.INFORMATION_MESSAGE);
            new MainDashboard(user);
            dispose();
        } else {
            showStyledMessage("Pogrešan username ili password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showStyledMessage("Molimo popunite sva polja za registraciju!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            showStyledMessage("Password mora imati najmanje 6 karaktera!", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userDAO.register(username, email, password);

        if (success) {
            showStyledMessage(
                    "Registracija uspješna!\nSada se možete prijaviti.",
                    "Uspjeh",
                    JOptionPane.INFORMATION_MESSAGE
            );
            emailField.setText("");
            passwordField.setText("");
        } else {
            showStyledMessage(
                    "Username ili email već postoji!\nPokušajte sa drugim podacima.",
                    "Greška",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
