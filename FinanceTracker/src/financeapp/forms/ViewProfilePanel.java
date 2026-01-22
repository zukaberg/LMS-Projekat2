package financeapp.forms;

import financeapp.dao.UserDAO;
import financeapp.models.User;
import financeapp.utils.AvatarUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class ViewProfilePanel extends JPanel {

    private User currentUser;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel avatarLabel;

    public ViewProfilePanel(User user) {
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

        JLabel headerLabel = new JLabel("View Profile");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Avatar
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 20, 10);

        avatarLabel = new JLabel();
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateAvatar();
        contentPanel.add(avatarLabel, gbc);

        gbc.gridy = 1;
        contentPanel.add(Box.createVerticalStrut(20), gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridy = 2;
        gbc.gridx = 0;
        contentPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameLabel = new JLabel(currentUser.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(usernameLabel, gbc);

        // Email
        gbc.gridy = 3;
        gbc.gridx = 0;
        contentPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailLabel = new JLabel(currentUser.getEmail());
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(emailLabel, gbc);

        // User ID
        gbc.gridy = 4;
        gbc.gridx = 0;
        contentPanel.add(new JLabel("User ID:"), gbc);

        gbc.gridx = 1;
        JLabel idValue = new JLabel(currentUser.getId().toString());
        idValue.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPanel.add(idValue, gbc);

        // Edit button
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton editBtn = new JButton("Edit Profile");
        editBtn.setPreferredSize(new Dimension(150, 35));
        editBtn.setBackground(new Color(52, 152, 219));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.addActionListener(e -> editProfile());
        contentPanel.add(editBtn, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void updateAvatar() {
        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            ImageIcon customAvatar = AvatarUtil.createAvatarFromImage(currentUser.getProfileImagePath(), 150);  // 120 -> 150
            if (customAvatar != null) {
                avatarLabel.setIcon(customAvatar);
                return;
            }
        }

        avatarLabel.setIcon(AvatarUtil.createAvatarIcon(currentUser.getUsername(), 150));  // 120 -> 150
    }

    private void editProfile() {
        JPanel editPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField newUsernameField = new JTextField(currentUser.getUsername());
        JTextField newEmailField = new JTextField(currentUser.getEmail());
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        JButton chooseImageBtn = new JButton("Choose Profile Image");
        JLabel imagePathLabel = new JLabel(currentUser.getProfileImagePath() != null ? "Image selected" : "No image");

        chooseImageBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg", "gif"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                currentUser.setProfileImagePath(selectedFile.getAbsolutePath());
                imagePathLabel.setText("Image selected");
            }
        });

        editPanel.add(new JLabel("Username:"));
        editPanel.add(newUsernameField);
        editPanel.add(new JLabel("Email:"));
        editPanel.add(newEmailField);
        editPanel.add(new JLabel("New Password (prazno = bez promjene):"));
        editPanel.add(newPasswordField);
        editPanel.add(new JLabel("Confirm Password:"));
        editPanel.add(confirmPasswordField);
        editPanel.add(new JLabel("Profile Image:"));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(chooseImageBtn, BorderLayout.CENTER);
        imagePanel.add(imagePathLabel, BorderLayout.SOUTH);
        editPanel.add(imagePanel);

        int result = JOptionPane.showConfirmDialog(
                this,
                editPanel,
                "Edit Profile",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String newUsername = newUsernameField.getText().trim();
            String newEmail = newEmailField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username i Email ne mogu biti prazni!");
                return;
            }

            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwordi se ne poklapaju!");
                    return;
                }
                currentUser.setPassword(newPassword);
            }

            currentUser.setUsername(newUsername);
            currentUser.setEmail(newEmail);

            UserDAO userDAO = new UserDAO();
            userDAO.updateUser(currentUser);

            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            updateAvatar();

            JOptionPane.showMessageDialog(this, "Profil uspjesno azuriran!");
        }
    }
}
