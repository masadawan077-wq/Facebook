package com.facebook.gui;

import com.facebook.Database;
import com.facebook.Main;
import com.facebook.gui.components.AnimatedButton;
import com.facebook.gui.components.ModernTextField;
import com.facebook.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SettingsPanel extends JPanel {

    private FacebookGUI parent;
    private HomePage homePage;

    // Components
    private ModernTextField firstNameField;
    private ModernTextField lastNameField;
    private ModernTextField bioField;
    private ModernTextField dobField; // Format YYYY-MM-DD
    private JCheckBox privacyToggle;

    public SettingsPanel(FacebookGUI parent, HomePage homePage) {
        this.parent = parent;
        this.homePage = homePage;
        setBackground(FacebookGUI.FB_BACKGROUND);
        setLayout(new BorderLayout());

        // Wrapper for scrolling
        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(FacebookGUI.FB_BACKGROUND);
        contentWrapper.setBorder(new EmptyBorder(30, 0, 30, 0)); // Vertical padding

        // Add content to wrapper
        contentWrapper.add(createSettingsCard());
        contentWrapper.add(Box.createVerticalGlue());

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSettingsCard() {
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setMaximumSize(new Dimension(800, Integer.MAX_VALUE)); // Max width constraint
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Header
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(FacebookGUI.FB_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage your profile information and privacy");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(30));

        // --- Personal Info Section ---
        JLabel infoHeader = new JLabel("Personal Information");
        infoHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(infoHeader);
        card.add(Box.createVerticalStrut(20));

        // Name Fields
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        namePanel.setBackground(Color.WHITE);
        namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        firstNameField = new ModernTextField("First Name");
        firstNameField.setText(Main.current.getFirstname());

        lastNameField = new ModernTextField("Last Name");
        lastNameField.setText(Main.current.getLastname());

        namePanel.add(createFieldContainer("First Name", firstNameField));
        namePanel.add(createFieldContainer("Last Name", lastNameField));

        card.add(namePanel);
        card.add(Box.createVerticalStrut(20));

        // Bio
        bioField = new ModernTextField("Bio");
        bioField.setText(Main.current.getBio());
        card.add(createFieldContainer("Bio", bioField));
        card.add(Box.createVerticalStrut(20));

        // DOB
        dobField = new ModernTextField("YYYY-MM-DD");
        dobField.setText(Main.current.getBirth().toString());
        card.add(createFieldContainer("Date of Birth (YYYY-MM-DD)", dobField));
        card.add(Box.createVerticalStrut(30));

        // Save Personal Info Button
        AnimatedButton saveInfoBtn = new AnimatedButton("Save Changes", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        saveInfoBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveInfoBtn.setPreferredSize(new Dimension(150, 40));
        saveInfoBtn.setMaximumSize(new Dimension(150, 40));
        saveInfoBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveInfoBtn.addActionListener(e -> savePersonalInfo());

        card.add(saveInfoBtn);
        card.add(Box.createVerticalStrut(40));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(30));

        // --- Privacy Section ---
        JLabel privacyHeader = new JLabel("Privacy & Security");
        privacyHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        privacyHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(privacyHeader);
        card.add(Box.createVerticalStrut(20));

        JPanel privacyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        privacyPanel.setBackground(Color.WHITE);
        privacyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        privacyToggle = new JCheckBox("Enable Privacy Mode");
        privacyToggle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        privacyToggle.setBackground(Color.WHITE);
        privacyToggle.setFocusPainted(false);
        privacyToggle.setSelected(Main.current.getPrivacy());
        privacyToggle.addActionListener(e -> togglePrivacy());

        JLabel privacyHint = new JLabel(" (Hides your online status)");
        privacyHint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        privacyHint.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        privacyPanel.add(privacyToggle);
        privacyPanel.add(privacyHint);

        card.add(privacyPanel);
        card.add(Box.createVerticalStrut(40));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(30));

        // --- Security Section (Password) ---
        JLabel securityHeader = new JLabel("Security");
        securityHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        securityHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(securityHeader);
        card.add(Box.createVerticalStrut(20));

        JPanel securityPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        securityPanel.setBackground(Color.WHITE);
        securityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        securityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPasswordField curPassField = new JPasswordField();
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        securityPanel.add(createFieldContainer("Current Password", curPassField));
        securityPanel.add(createFieldContainer("New Password", newPassField));
        securityPanel.add(createFieldContainer("Confirm New Password", confirmPassField));

        card.add(securityPanel);
        card.add(Box.createVerticalStrut(20));

        AnimatedButton changePassBtn = new AnimatedButton("Change Password", new Color(228, 230, 235),
                new Color(210, 213, 218));
        changePassBtn.setForeground(Color.BLACK);
        changePassBtn.setPreferredSize(new Dimension(160, 40));
        changePassBtn.addActionListener(e -> {
            String curP = new String(curPassField.getPassword());
            String newP = new String(newPassField.getPassword());
            String conP = new String(confirmPassField.getPassword());

            if (curP.isEmpty() || newP.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all password fields.");
                return;
            }

            if (!Main.current.getCredentials().p_Verify(curP)) {
                JOptionPane.showMessageDialog(this, "Current password incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newP.equals(conP)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newP.length() < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Main.current.getCredentials().setPassword(newP);
            Database.WriteUser(Main.current);
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            curPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
        });

        card.add(changePassBtn);
        card.add(Box.createVerticalStrut(40));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(30));

        // --- Danger Zone (Delete Account & Logout) ---
        JLabel dangerHeader = new JLabel("Danger Zone");
        dangerHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dangerHeader.setForeground(FacebookGUI.FB_ERROR);
        dangerHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(dangerHeader);
        card.add(Box.createVerticalStrut(20));

        // Logout
        AnimatedButton logoutBtn = new AnimatedButton("Log Out", new Color(255, 230, 230), new Color(255, 200, 200));
        logoutBtn.setForeground(FacebookGUI.FB_ERROR);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setPreferredSize(new Dimension(150, 40));
        logoutBtn.setMaximumSize(new Dimension(150, 40));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> homePage.logout());

        card.add(logoutBtn);
        card.add(Box.createVerticalStrut(15));

        // Delete Account
        JLabel deleteHint = new JLabel("Once you delete your account, there is no going back. Please be certain.");
        deleteHint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteHint.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        deleteHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(deleteHint);
        card.add(Box.createVerticalStrut(15));

        AnimatedButton deleteBtn = new AnimatedButton("Delete Account", FacebookGUI.FB_ERROR, new Color(200, 35, 51));
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setPreferredSize(new Dimension(150, 40));
        deleteBtn.setMaximumSize(new Dimension(150, 40));
        deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteBtn.addActionListener(e -> deleteAccount());

        card.add(deleteBtn);

        return card;
    }

    private JPanel createFieldContainer(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Limit height

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(101, 103, 107));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void savePersonalInfo() {
        String fname = firstNameField.getText().trim();
        String lname = lastNameField.getText().trim();
        String bio = bioField.getText().trim();
        String dobStr = dobField.getText().trim();

        if (fname.isEmpty() || lname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name fields cannot be empty.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update user
        Main.current.setFirstname(fname);
        Main.current.setLastname(lname);
        Main.current.setBio(bio);
        Main.current.setBirth(dob);

        // Persist
        Database.WriteUser(Main.current);

        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void togglePrivacy() {
        boolean enable = privacyToggle.isSelected();
        if (enable) {
            Main.current.Privacy_Mode_On();
        } else {
            Main.current.Privacy_Mode_OFF();
        }
        Database.WriteUser(Main.current);
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to PERMANENTLY delete your account?\nThis action cannot be undone.",
                "Delete Account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Database.Delete_Acc();
            Main.current = null;
            parent.showLoginPanel(); // Redirect to login
        }
    }
}
