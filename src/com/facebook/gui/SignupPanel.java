package com.facebook.gui;

import com.facebook.*;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Facebook Signup Panel - Exact replica of Facebook's signup page
 * Features: All form fields, validations, animations, hover effects
 */
public class SignupPanel extends JPanel {

    private FacebookGUI parent;

    // Form fields
    private ModernTextField firstNameField;
    private ModernTextField lastNameField;
    private ModernComboBox<Integer> dayCombo;
    private ModernComboBox<String> monthCombo;
    private ModernComboBox<Integer> yearCombo;
    private GenderRadioPanel genderPanel;
    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private AnimatedButton signupButton;
    private LinkLabel loginLink;

    // Error label
    private JLabel errorLabel;

    // Animation
    private Timer fadeInTimer;
    private float opacity = 0f;

    // Months
    private final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    public SignupPanel(FacebookGUI parent) {
        this.parent = parent;
        setBackground(FacebookGUI.FB_BACKGROUND);
        setLayout(new GridBagLayout());
        initComponents();
        startFadeInAnimation();
    }

    private void initComponents() {
        // ==================== Main Content Panel ====================
        // Use GridBagLayout for vertical stacking and centering
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0); // Padding around logo
        gbc.anchor = GridBagConstraints.CENTER;

        // ==================== TOP - Logo ====================
        // Simplified logo - no gradient for minimalism
        JLabel logoLabel = new JLabel("facebook");
        logoLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 50));
        logoLabel.setForeground(new Color(24, 119, 242));

        contentPanel.add(logoLabel, gbc);

        // ==================== CENTER - Signup Card ====================
        RoundedPanel signupCard = new RoundedPanel(10);
        signupCard.setBackground(Color.WHITE);
        signupCard.setLayout(new BorderLayout()); // Changed to BorderLayout for inner scroll
        signupCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Compact, centered card like Facebook
        int cardWidth = 460; // Increased to 460 to prevent clipping
        int cardHeight = 580;
        signupCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
        signupCard.setMinimumSize(new Dimension(cardWidth, cardHeight));

        // Internal form panel that holds the content
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(Color.WHITE);
        formContent.setBorder(new EmptyBorder(0, 0, 0, 10)); // Gap for scrollbar

        populateSignupCard(formContent);

        // Inner Scroll Pane
        JScrollPane innerScroll = new JScrollPane(formContent);
        innerScroll.setBorder(null);
        innerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        innerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        innerScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Mini Scroll Bar Style
        innerScroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
                this.trackColor = Color.WHITE;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setPreferredSize(new Dimension(0, 0));
                jbutton.setMinimumSize(new Dimension(0, 0));
                jbutton.setMaximumSize(new Dimension(0, 0));
                return jbutton;
            }

            @Override
            public Dimension getPreferredSize(JComponent c) {
                return new Dimension(8, super.getPreferredSize(c).height); // Thin width
            }
        });

        signupCard.add(innerScroll, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0); // Bottom padding
        contentPanel.add(signupCard, gbc);

        // ==================== Scroll Pane ====================
        // Wrap content in scroll pane (Outer scroll disabled)
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); // Disabled outer
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Add scroll pane to main panel
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateSignupCard(JPanel signupCard) {
        // Title
        JLabel titleLabel = new JLabel("Create a new account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("It's quick and easy.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator after title
        JSeparator titleSep = new JSeparator();
        titleSep.setForeground(new Color(218, 220, 224));
        titleSep.setPreferredSize(new Dimension(360, 1));
        titleSep.setMaximumSize(new Dimension(360, 1));
        titleSep.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ==================== Name Row ====================
        JPanel nameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        nameRow.setOpaque(false);
        nameRow.setPreferredSize(new Dimension(360, 52));
        nameRow.setMaximumSize(new Dimension(360, 52));
        nameRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        firstNameField = new ModernTextField("First name");
        lastNameField = new ModernTextField("Surname");

        nameRow.add(firstNameField);
        nameRow.add(lastNameField);

        // ==================== Date of Birth Section ====================
        JPanel dobLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dobLabelPanel.setOpaque(false);
        dobLabelPanel.setPreferredSize(new Dimension(360, 25));
        dobLabelPanel.setMaximumSize(new Dimension(360, 25));
        dobLabelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel dobLabel = new JLabel("Date of birth ");
        dobLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dobLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        // Info icon - simple circle with ?
        JLabel infoIcon = new JLabel("?") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(150, 150, 150));
                g2.fillOval(0, 0, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("?", 5, 12);
                g2.dispose();
            }
        };
        infoIcon.setPreferredSize(new Dimension(16, 16));
        infoIcon.setToolTipText(
                "<html>Providing your date of birth helps make sure<br>you get the right Facebook experience for your age.</html>");
        infoIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        dobLabelPanel.add(dobLabel);
        dobLabelPanel.add(infoIcon);

        // Date combo boxes
        JPanel dobRow = new JPanel(new GridLayout(1, 3, 12, 0));
        dobRow.setOpaque(false);
        dobRow.setPreferredSize(new Dimension(360, 45));
        dobRow.setMaximumSize(new Dimension(360, 45));
        dobRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create day combo (1-31)
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++)
            days[i] = i + 1;
        dayCombo = new ModernComboBox<>(days);
        dayCombo.setPreferredSize(new Dimension(0, 45));

        // Set current day
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        dayCombo.setSelectedItem(currentDay);

        // Create month combo
        monthCombo = new ModernComboBox<>(MONTHS);
        monthCombo.setPreferredSize(new Dimension(0, 45));

        // Set current month
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        monthCombo.setSelectedIndex(currentMonth);

        // Create year combo (1905 - current year)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[currentYear - 1905 + 1];
        for (int i = 0; i < years.length; i++)
            years[i] = currentYear - i;
        yearCombo = new ModernComboBox<>(years);
        yearCombo.setPreferredSize(new Dimension(0, 45));

        dobRow.add(dayCombo);
        dobRow.add(monthCombo);
        dobRow.add(yearCombo);

        // ==================== Gender Section ====================
        JPanel genderLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderLabelPanel.setOpaque(false);
        genderLabelPanel.setPreferredSize(new Dimension(360, 25));
        genderLabelPanel.setMaximumSize(new Dimension(360, 25));
        genderLabelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel genderLabel = new JLabel("Gender ");
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        genderLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        JLabel genderInfoIcon = new JLabel("?") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(150, 150, 150));
                g2.fillOval(0, 0, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("?", 5, 12);
                g2.dispose();
            }
        };
        genderInfoIcon.setPreferredSize(new Dimension(16, 16));
        genderInfoIcon.setToolTipText("<html>You can change who sees your gender on<br>your profile later.</html>");
        genderInfoIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        genderLabelPanel.add(genderLabel);
        genderLabelPanel.add(genderInfoIcon);

        genderPanel = new GenderRadioPanel();
        genderPanel.setPreferredSize(new Dimension(360, 45));
        genderPanel.setMaximumSize(new Dimension(360, 45));
        genderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ==================== Email/Phone ====================
        emailField = new ModernTextField("Mobile number or email address");
        emailField.setPreferredSize(new Dimension(360, 52));
        emailField.setMaximumSize(new Dimension(360, 52));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ==================== Password ====================
        passwordField = new ModernPasswordField("New password");
        passwordField.setPreferredSize(new Dimension(360, 52));
        passwordField.setMaximumSize(new Dimension(360, 52));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSignup();
                }
            }
        });

        // ==================== Terms Text ====================
        JLabel termsLabel = new JLabel("<html><div style='width: 340px; font-size: 11px; color: #65676b;'>" +
                "People who use our service may have uploaded your contact information to Facebook. " +
                "<a href='#' style='color: #1877f2;'>Learn more</a>." +
                "</div></html>");
        termsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        termsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel policyLabel = new JLabel("<html><div style='width: 340px; font-size: 11px; color: #65676b;'>" +
                "By clicking Sign Up, you agree to our <a href='#' style='color: #1877f2;'>Terms</a>, " +
                "<a href='#' style='color: #1877f2;'>Privacy Policy</a> and " +
                "<a href='#' style='color: #1877f2;'>Cookies Policy</a>. " +
                "You may receive SMS notifications from us and can opt out at any time." +
                "</div></html>");
        policyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        policyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ==================== Error Label ====================
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(FacebookGUI.FB_ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        // ==================== Button ====================
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainer.setOpaque(false);
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupButton = new AnimatedButton("Sign Up", FacebookGUI.FB_GREEN, FacebookGUI.FB_GREEN_HOVER);
        signupButton.setPreferredSize(new Dimension(194, 36));
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        signupButton.addActionListener(e -> performSignup());
        buttonContainer.add(signupButton);

        // ==================== Already have account? ====================
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginLinkPanel.setOpaque(false);
        loginLinkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginLink = new LinkLabel("Already have an account?");
        loginLink.setForeground(FacebookGUI.FB_BLUE);
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.showLoginPanel();
            }
        });
        loginLinkPanel.add(loginLink);

        // ==================== Add all to card ====================
        signupCard.add(titleLabel);
        signupCard.add(Box.createVerticalStrut(4));
        signupCard.add(subtitleLabel);
        signupCard.add(Box.createVerticalStrut(10));
        signupCard.add(titleSep);
        signupCard.add(Box.createVerticalStrut(12));
        signupCard.add(nameRow);
        signupCard.add(Box.createVerticalStrut(10));
        signupCard.add(dobLabelPanel);
        signupCard.add(Box.createVerticalStrut(4));
        signupCard.add(dobRow);
        signupCard.add(Box.createVerticalStrut(10));
        signupCard.add(genderLabelPanel);
        signupCard.add(Box.createVerticalStrut(4));
        signupCard.add(genderPanel);
        signupCard.add(Box.createVerticalStrut(10));
        signupCard.add(emailField);
        signupCard.add(Box.createVerticalStrut(8));
        signupCard.add(passwordField);
        signupCard.add(Box.createVerticalStrut(8));
        signupCard.add(termsLabel);
        signupCard.add(Box.createVerticalStrut(4));
        signupCard.add(policyLabel);
        signupCard.add(errorLabel);
        signupCard.add(Box.createVerticalStrut(10));
        signupCard.add(buttonContainer);
        signupCard.add(Box.createVerticalStrut(8));
        signupCard.add(loginLinkPanel);
    }

    private void performSignup() {
        // Reset error states
        firstNameField.setError(false);
        lastNameField.setError(false);
        emailField.setError(false);
        passwordField.setError(false);
        errorLabel.setText(" ");

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = emailField.getText().trim();
        String password = passwordField.getPasswordText();

        // ==================== VALIDATIONS ====================

        // First name validation
        if (firstName.isEmpty()) {
            firstNameField.setError(true);
            showError("What's your name?");
            shakeComponent(firstNameField);
            return;
        }

        // Last name validation
        if (lastName.isEmpty()) {
            lastNameField.setError(true);
            showError("What's your surname?");
            shakeComponent(lastNameField);
            return;
        }

        // Gender validation
        if (!genderPanel.isGenderSelected()) {
            showError("Please select your gender.");
            shakeComponent(genderPanel);
            return;
        }

        // Username validation (8-12 characters, no spaces)
        if (username.isEmpty()) {
            emailField.setError(true);
            showError("You'll use this when you log in.");
            shakeComponent(emailField);
            return;
        }

        if (username.length() < 8 || username.length() > 12) {
            emailField.setError(true);
            showError("Username must be 8-12 characters long.");
            shakeComponent(emailField);
            return;
        }

        if (username.contains(" ")) {
            emailField.setError(true);
            showError("Username cannot contain spaces.");
            shakeComponent(emailField);
            return;
        }

        // Check if username exists
        if (Database.LoadUser(username) != null) {
            emailField.setError(true);
            showError("This username is already taken. Try another one.");
            shakeComponent(emailField);
            return;
        }

        // Password validation (8-15 characters, no spaces)
        if (password.isEmpty()) {
            passwordField.setError(true);
            showError("Enter a password.");
            shakeComponent(passwordField);
            return;
        }

        if (password.length() < 8 || password.length() > 15) {
            passwordField.setError(true);
            showError("Password must be 8-15 characters long.");
            shakeComponent(passwordField);
            return;
        }

        if (password.contains(" ")) {
            passwordField.setError(true);
            showError("Password cannot contain spaces.");
            shakeComponent(passwordField);
            return;
        }

        // ==================== Create Account ====================
        try {
            // Get date of birth
            int day = (Integer) dayCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex() + 1; // 1-based month
            int year = (Integer) yearCombo.getSelectedItem();
            LocalDate birthDate = LocalDate.of(year, month, day);

            // Get gender
            String genderStr = genderPanel.getSelectedGender();
            Gender gender = genderStr.equals("Female") ? Gender.FEMALE : Gender.MALE;

            // Create credentials
            Credentials credentials = new Credentials(username, password);

            // Create user with gender
            String bio = "Hey there! I'm using Facebook.";
            User newUser = new User(firstName, lastName, birthDate, bio, credentials, gender);

            // Save to database
            Database.Write_new_account(newUser);

            // Show success animation
            animateSuccess(() -> {
                JOptionPane.showMessageDialog(parent,
                        "Your account has been created successfully!\nYou can now log in.",
                        "Account Created",
                        JOptionPane.INFORMATION_MESSAGE);
                parent.showLoginPanel();
            });

        } catch (Exception e) {
            showError("Invalid date of birth. Please check your selection.");
        }
    }

    private void showError(String message) {
        errorLabel.setForeground(FacebookGUI.FB_ERROR);
        errorLabel.setText(message);

        // Fade in animation
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

    private void startFadeInAnimation() {
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

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g2.dispose();
    }

    public void resetFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        genderPanel.clearSelection();

        firstNameField.setError(false);
        lastNameField.setError(false);
        emailField.setError(false);
        passwordField.setError(false);
        errorLabel.setText(" ");

        // Reset date to current
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        dayCombo.setSelectedItem(currentDay);
        monthCombo.setSelectedIndex(currentMonth);
        yearCombo.setSelectedItem(currentYear);

        firstNameField.requestFocus();
    }
}
