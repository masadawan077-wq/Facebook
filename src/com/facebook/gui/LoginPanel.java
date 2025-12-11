package com.facebook.gui;

import com.facebook.Database;
import com.facebook.Main;
import com.facebook.User;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Facebook Login Panel - Exact replica of Facebook's login page
 * Features: Animations, hover effects, validations, professional design
 */
public class LoginPanel extends JPanel {

    private FacebookGUI parent;
    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private AnimatedButton loginButton;
    private LinkLabel forgotPasswordLink;
    private AnimatedButton createAccountButton;
    private JLabel errorLabel;

    // Animation
    private Timer fadeInTimer;
    private float opacity = 0f;

    public LoginPanel(FacebookGUI parent) {
        this.parent = parent;
        setBackground(new Color(240, 242, 245)); // Darker background
        setLayout(new GridBagLayout());
        initComponents();
        startFadeInAnimation();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        // Main container panel
        JPanel containerPanel = new JPanel(new GridBagLayout());
        containerPanel.setOpaque(false);

        // ==================== LEFT SIDE - Logo and Tagline ====================
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(0, 0, 100, 50));

        // Facebook Logo - Simplified (no gradient for minimalism)
        JLabel logoLabel = new JLabel("facebook");
        logoLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 60));
        logoLabel.setForeground(new Color(24, 119, 242));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tagline - Lighter font
        JLabel taglineLabel = new JLabel(
                "<html><div style='width: 400px;'>Facebook helps you connect and share<br>with the people in your life.</div></html>");
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        taglineLabel.setForeground(new Color(96, 103, 112));
        taglineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taglineLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

        leftPanel.add(logoLabel);
        leftPanel.add(taglineLabel);

        // ==================== RIGHT SIDE - Login Form ====================
        RoundedPanel loginCard = new RoundedPanel(12);
        loginCard.setBackground(Color.WHITE);
        loginCard.setLayout(new GridBagLayout());
        loginCard.setBorder(new EmptyBorder(30, 25, 30, 25));

        // Fixed size to prevent overflow
        int cardWidth = 400;
        int cardHeight = 400;
        loginCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
        loginCard.setMinimumSize(new Dimension(cardWidth, cardHeight));
        loginCard.setMaximumSize(new Dimension(cardWidth, cardHeight));

        // Email/Phone field
        emailField = new ModernTextField("Email address or phone number");
        emailField.setPreferredSize(new Dimension(350, 52));
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        // Password field
        passwordField = new ModernPasswordField("Password");
        passwordField.setPreferredSize(new Dimension(350, 52));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        errorLabel.setForeground(FacebookGUI.FB_ERROR);

        // Login button
        loginButton = new AnimatedButton("Log in", new Color(24, 119, 242), new Color(66, 103, 178));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(350, 48));
        loginButton.addActionListener(e -> performLogin());

        // Forgot password link
        forgotPasswordLink = new LinkLabel("Forgotten password?");
        forgotPasswordLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPasswordLink.addActionListener(e -> showForgotPasswordDialog());

        // Separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(218, 220, 224));

        // Create new account button
        createAccountButton = new AnimatedButton("Create new account", FacebookGUI.FB_GREEN,
                FacebookGUI.FB_GREEN_HOVER);
        createAccountButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        createAccountButton.setPreferredSize(new Dimension(200, 48));
        createAccountButton.addActionListener(e -> parent.showSignupPanel());

        // Layout components with GridBagLayout for perfect alignment
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;
        cardGbc.insets = new Insets(0, 0, 0, 0);

        // Email field
        cardGbc.gridy = 0;
        loginCard.add(emailField, cardGbc);

        // Spacing
        cardGbc.gridy = 1;
        cardGbc.insets = new Insets(15, 0, 0, 0);
        loginCard.add(passwordField, cardGbc);

        // Error label
        cardGbc.gridy = 2;
        cardGbc.insets = new Insets(5, 5, 0, 0);
        cardGbc.fill = GridBagConstraints.NONE;
        cardGbc.anchor = GridBagConstraints.WEST;
        loginCard.add(errorLabel, cardGbc);

        // Login button
        cardGbc.gridy = 3;
        cardGbc.insets = new Insets(18, 0, 0, 0);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        loginCard.add(loginButton, cardGbc);

        // Forgot password link
        cardGbc.gridy = 4;
        cardGbc.insets = new Insets(22, 0, 0, 0);
        cardGbc.anchor = GridBagConstraints.CENTER;
        cardGbc.fill = GridBagConstraints.NONE;
        loginCard.add(forgotPasswordLink, cardGbc);

        // Separator
        cardGbc.gridy = 5;
        cardGbc.insets = new Insets(25, 0, 0, 0);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        loginCard.add(separator, cardGbc);

        // Create account button
        cardGbc.gridy = 6;
        cardGbc.insets = new Insets(25, 0, 0, 0);
        cardGbc.anchor = GridBagConstraints.CENTER;
        cardGbc.fill = GridBagConstraints.NONE;
        loginCard.add(createAccountButton, cardGbc);

        // ==================== Footer ====================
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);

        JLabel createPageLabel = new JLabel(
                "<html><span style='font-weight:bold;'>Create a Page</span> for a celebrity, brand or business.</html>");
        createPageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        createPageLabel.setForeground(new Color(96, 103, 112));
        footerPanel.add(createPageLabel);

        // ==================== Add to container ====================
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 40);
        containerPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 40, 0, 0);
        containerPanel.add(loginCard, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 30, 0);
        containerPanel.add(footerPanel, gbc);

        // Wrap container in scroll pane for responsiveness
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add scroll pane to main panel
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performLogin() {
        String username = emailField.getText().trim();
        String password = passwordField.getPasswordText();

        // Reset error states
        emailField.setError(false);
        passwordField.setError(false);
        errorLabel.setText(" ");

        // Validation
        if (username.isEmpty()) {
            emailField.setError(true);
            showError("Please enter your email or phone number");
            shakeComponent(emailField);
            return;
        }

        if (password.isEmpty()) {
            passwordField.setError(true);
            showError("Please enter your password");
            shakeComponent(passwordField);
            return;
        }

        // Check if user exists
        User user = Database.LoadUser(username);
        if (user == null) {
            emailField.setError(true);
            showError("The username you entered isn't connected to an account.");
            shakeComponent(emailField);
            return;
        }

        // Verify password
        if (!user.getCredentials().p_Verify(password)) {
            passwordField.setError(true);
            showError("The password you entered is incorrect.");
            shakeComponent(passwordField);
            return;
        }

        // Success!
        Main.current = user;
        Database.Write_Online();

        // Success animation
        animateSuccess(() -> parent.onLoginSuccess());
    }

    private void showError(String message) {
        errorLabel.setForeground(FacebookGUI.FB_ERROR);
        errorLabel.setText(message);

        // Fade in error animation
        Timer fadeTimer = new Timer(20, null);
        final float[] alpha = { 0f };
        fadeTimer.addActionListener(e -> {
            alpha[0] += 0.1f;
            if (alpha[0] >= 1f) {
                fadeTimer.stop();
            }
            errorLabel.setForeground(new Color(220, 53, 69, (int) (alpha[0] * 255)));
        });
        fadeTimer.start();
    }

    private void shakeComponent(JComponent component) {
        Point originalLocation = component.getLocation();
        Timer shakeTimer = new Timer(20, null);
        final int[] shakeCount = { 0 };
        final int[] direction = { 1 };

        shakeTimer.addActionListener(e -> {
            if (shakeCount[0] >= 6) {
                component.setLocation(originalLocation);
                shakeTimer.stop();
                return;
            }

            int offset = 5 * direction[0];
            component.setLocation(originalLocation.x + offset, originalLocation.y);
            direction[0] *= -1;
            shakeCount[0]++;
        });
        shakeTimer.start();
    }

    private void animateSuccess(Runnable onComplete) {
        // Flash green on success
        Timer successTimer = new Timer(100, null);
        final int[] count = { 0 };

        successTimer.addActionListener(e -> {
            count[0]++;
            if (count[0] >= 3) {
                successTimer.stop();
                onComplete.run();
            }
        });
        successTimer.start();
    }

    private void showForgotPasswordDialog() {
        // Create custom dialog with proper sizing
        JDialog dialog = new JDialog(parent, "Forgot Password", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instructionLabel = new JLabel(
                "<html><div style='width: 400px;'>Enter your username below and prove your identity to reset your password.</div></html>");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fixed width text fields to prevent overflow
        ModernTextField usernameField = new ModernTextField("Username");
        usernameField.setPreferredSize(new Dimension(420, 50));
        usernameField.setMaximumSize(new Dimension(420, 50));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernTextField firstNameField = new ModernTextField("First Name");
        firstNameField.setPreferredSize(new Dimension(420, 50));
        firstNameField.setMaximumSize(new Dimension(420, 50));
        firstNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernTextField lastNameField = new ModernTextField("Last Name");
        lastNameField.setPreferredSize(new Dimension(420, 50));
        lastNameField.setMaximumSize(new Dimension(420, 50));
        lastNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        AnimatedButton searchButton = new AnimatedButton("Verify Identity", FacebookGUI.FB_BLUE,
                FacebookGUI.FB_BLUE_HOVER);
        searchButton.setPreferredSize(new Dimension(420, 45));
        searchButton.setMaximumSize(new Dimension(420, 45));
        searchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(instructionLabel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(firstNameField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lastNameField);
        panel.add(Box.createVerticalStrut(18));
        panel.add(searchButton);

        searchButton.addActionListener(e -> {
            String uname = usernameField.getText().trim();
            String fname = firstNameField.getText().trim();
            String lname = lastNameField.getText().trim();

            User user = Database.LoadUser(uname);
            if (user != null &&
                    user.getFirstname().equalsIgnoreCase(fname) &&
                    user.getLastname().equalsIgnoreCase(lname)) {

                dialog.dispose();
                showNewPasswordDialog(user);
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Could not verify your identity. Please check your information.",
                        "Verification Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private void showNewPasswordDialog(User user) {
        JDialog dialog = new JDialog(parent, "New Password", true);
        dialog.setSize(480, 310);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Create New Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fixed width password fields
        ModernPasswordField newPassField = new ModernPasswordField("New password");
        newPassField.setPreferredSize(new Dimension(400, 50));
        newPassField.setMaximumSize(new Dimension(400, 50));
        newPassField.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernPasswordField confirmPassField = new ModernPasswordField("Confirm password");
        confirmPassField.setPreferredSize(new Dimension(400, 50));
        confirmPassField.setMaximumSize(new Dimension(400, 50));
        confirmPassField.setAlignmentX(Component.LEFT_ALIGNMENT);

        AnimatedButton saveButton = new AnimatedButton("Save Password", FacebookGUI.FB_GREEN,
                FacebookGUI.FB_GREEN_HOVER);
        saveButton.setPreferredSize(new Dimension(400, 45));
        saveButton.setMaximumSize(new Dimension(400, 45));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(newPassField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(confirmPassField);
        panel.add(Box.createVerticalStrut(18));
        panel.add(saveButton);

        saveButton.addActionListener(e -> {
            String newPass = newPassField.getPasswordText();
            String confirmPass = confirmPassField.getPasswordText();

            if (newPass.length() < 8 || newPass.length() > 15) {
                JOptionPane.showMessageDialog(dialog,
                        "Password must be 8-15 characters long.",
                        "Invalid Password", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (newPass.contains(" ")) {
                JOptionPane.showMessageDialog(dialog,
                        "Password cannot contain spaces.",
                        "Invalid Password", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog,
                        "Passwords do not match.",
                        "Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            user.getCredentials().setPassword(newPass);
            Database.WriteUser(user);

            JOptionPane.showMessageDialog(dialog,
                    "Password changed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private void startFadeInAnimation() {
        // Simple fade in animation - works better with GridBagLayout
        fadeInTimer = new Timer(20, e -> {
            opacity += 0.05f;
            if (opacity >= 1f) {
                opacity = 1f;
                fadeInTimer.stop();
            }
            repaint();
        });
        fadeInTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Apply fade effect
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.dispose();
    }

    public void resetFields() {
        emailField.setText("");
        passwordField.setText("");
        emailField.setError(false);
        passwordField.setError(false);
        errorLabel.setText(" ");
        emailField.requestFocus();
    }
}
