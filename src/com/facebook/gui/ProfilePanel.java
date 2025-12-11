package com.facebook.gui;

import com.facebook.*;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ProfilePanel extends JPanel {

    private HomePage homePage;
    private User user;
    private JPanel dynamicContentPanel;
    private JPanel tabsPanel;
    private String currentTab = "Posts"; // Default tab

    // Colors
    private final Color BG_COLOR = FacebookGUI.FB_BACKGROUND;
    private final Color CARD_BG = Color.WHITE;

    public ProfilePanel(FacebookGUI parent, HomePage homePage, User targetUser) {
        this.homePage = homePage;
        this.user = (targetUser != null) ? targetUser : Main.current;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Main Scrollable Area
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(BG_COLOR);

        // 1. Header (Cover + Info + Tabs)
        mainContainer.add(createHeaderSection());

        // 2. Dynamic Content Area
        dynamicContentPanel = new JPanel(new BorderLayout());
        dynamicContentPanel.setBackground(BG_COLOR);
        dynamicContentPanel.setBorder(new EmptyBorder(20, 100, 20, 100)); // Central margin
        mainContainer.add(dynamicContentPanel);

        // Initial Load
        loadTabContent("Posts");

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public ProfilePanel(FacebookGUI parent, HomePage homePage) {
        this(parent, homePage, Main.current);
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_BG);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, FacebookGUI.FB_BORDER));

        // Cover Photo (Gradient)
        JPanel coverPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 40, 40), 0, getHeight(),
                        new Color(20, 20, 20));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        coverPanel.setPreferredSize(new Dimension(1000, 250));
        coverPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 250));

        // Info Section
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(CARD_BG);
        infoSection.setBorder(new EmptyBorder(0, 40, 0, 40));

        // Top Row: Profile Pic + Name + Buttons
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(CARD_BG);
        topRow.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Profile Pic & Name
        JPanel identityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        identityPanel.setBackground(CARD_BG);

        // Circular Profile Pic
        JLabel profilePic = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 0, 120, 120);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 50));
                String initial = user.getFirstname().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (120 - fm.stringWidth(initial)) / 2;
                int y = (120 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        profilePic.setPreferredSize(new Dimension(120, 120));

        // Name & Friends Count
        JPanel nameBlock = new JPanel(new GridLayout(2, 1));
        nameBlock.setBackground(CARD_BG);
        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));

        int friendCount = Database.Load_Friends(user.getCredentials().getUsername()).size();
        JLabel friendsLabel = new JLabel(friendCount + " friends");
        friendsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        friendsLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        nameBlock.add(nameLabel);
        nameBlock.add(friendsLabel);

        identityPanel.add(profilePic);
        identityPanel.add(nameBlock);

        // Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 40));
        actionPanel.setBackground(CARD_BG);

        boolean isMe = user.getCredentials().getUsername().equals(Main.current.getCredentials().getUsername());

        if (isMe) {
            AnimatedButton editProfileBtn = new AnimatedButton("Edit Profile", new Color(228, 230, 235),
                    new Color(210, 213, 218));
            editProfileBtn.setForeground(Color.BLACK);
            editProfileBtn.setPreferredSize(new Dimension(140, 36));
            editProfileBtn.addActionListener(e -> homePage.showSettingsPanel());

            AnimatedButton privacyBtn = new AnimatedButton(user.getPrivacy() ? "Privacy: ON" : "Privacy: OFF",
                    user.getPrivacy() ? FacebookGUI.FB_GREEN : new Color(228, 230, 235),
                    user.getPrivacy() ? FacebookGUI.FB_GREEN_HOVER : new Color(210, 213, 218));
            if (!user.getPrivacy())
                privacyBtn.setForeground(Color.BLACK);
            else
                privacyBtn.setForeground(Color.WHITE);

            privacyBtn.setPreferredSize(new Dimension(140, 36));
            privacyBtn.addActionListener(e -> {
                if (user.getPrivacy()) {
                    user.Privacy_Mode_OFF();
                    privacyBtn.setText("Privacy: OFF");
                    privacyBtn.setColors(new Color(228, 230, 235), new Color(210, 213, 218));
                    privacyBtn.setForeground(Color.BLACK);
                } else {
                    user.Privacy_Mode_On();
                    privacyBtn.setText("Privacy: ON");
                    privacyBtn.setColors(FacebookGUI.FB_GREEN, FacebookGUI.FB_GREEN_HOVER);
                    privacyBtn.setForeground(Color.WHITE);
                }
                Database.WriteUser(user);
            });

            actionPanel.add(privacyBtn);
            actionPanel.add(editProfileBtn);
        } else {
            // Actions for OTHER user
            String username = user.getCredentials().getUsername();
            boolean isFriend = Database.Already_Friend(user);
            boolean requestSent = Database.F_Request_Already_sent(username);

            AnimatedButton mainActionBtn;
            if (isFriend) {
                mainActionBtn = new AnimatedButton("Friends", new Color(235, 245, 255), new Color(210, 230, 255));
                mainActionBtn.setForeground(FacebookGUI.FB_BLUE);
                mainActionBtn.setEnabled(false); // Already friends
            } else if (requestSent) {
                mainActionBtn = new AnimatedButton("Request Sent", new Color(228, 230, 235), new Color(210, 213, 218));
                mainActionBtn.setForeground(Color.BLACK);
                mainActionBtn.addActionListener(e -> {
                    Database.Delete_FriendRequest_Sent(username);
                    // Update button state (simplistic refresh)
                    homePage.showProfilePanel(user);
                });
            } else {
                mainActionBtn = new AnimatedButton("Add Friend", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
                mainActionBtn.addActionListener(e -> {
                    Database.Friend_Request_Sent(username);
                    homePage.showProfilePanel(user);
                });
            }
            mainActionBtn.setPreferredSize(new Dimension(140, 36));

            AnimatedButton msgBtn = new AnimatedButton("Message", new Color(228, 230, 235), new Color(210, 213, 218));
            msgBtn.setForeground(Color.BLACK);
            msgBtn.setPreferredSize(new Dimension(140, 36));
            msgBtn.addActionListener(e -> homePage.openChatWithFriend(username));

            actionPanel.add(mainActionBtn);
            actionPanel.add(msgBtn);
        }

        topRow.add(identityPanel, BorderLayout.WEST);
        topRow.add(actionPanel, BorderLayout.EAST);

        infoSection.add(topRow);
        infoSection.add(new JSeparator());

        // Tabs Menu
        tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        tabsPanel.setBackground(CARD_BG);
        tabsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        tabsPanel.add(createTabButton("Posts"));
        tabsPanel.add(createTabButton("About"));
        tabsPanel.add(createTabButton("Friends"));
        tabsPanel.add(createTabButton("Photos"));

        infoSection.add(tabsPanel);

        headerPanel.add(coverPanel);
        headerPanel.add(infoSection);

        return headerPanel;
    }

    private JButton createTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setMargin(new Insets(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Style based on state
        if (text.equals(currentTab)) {
            btn.setForeground(FacebookGUI.FB_BLUE);
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, FacebookGUI.FB_BLUE));
        } else {
            btn.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(currentTab))
                    btn.setBackground(new Color(242, 242, 242));
                btn.setContentAreaFilled(true);
            }

            public void mouseExited(MouseEvent e) {
                btn.setContentAreaFilled(false);
            }
        });

        btn.addActionListener(e -> {
            loadTabContent(text);
        });

        return btn;
    }

    private void loadTabContent(String tabName) {
        currentTab = tabName;
        updateTabStyles();

        dynamicContentPanel.removeAll();

        switch (tabName) {
            case "Posts" -> loadPostsView();
            case "About" -> loadAboutView();
            case "Friends" -> loadFriendsView();
            case "Photos" -> loadPhotosView();
        }

        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();
    }

    private void updateTabStyles() {
        for (Component c : tabsPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (btn.getText().equals(currentTab)) {
                    btn.setForeground(FacebookGUI.FB_BLUE);
                    btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, FacebookGUI.FB_BLUE));
                } else {
                    btn.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
                    btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
                }
            }
        }
    }

    // ================= VIEWS =================

    private void loadPostsView() {
        // Two columns: Left (Intro/Photos) - Right (Create Post/Feed)
        JPanel container = new JPanel(new BorderLayout(20, 0));
        container.setBackground(BG_COLOR);

        // Left Col
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(BG_COLOR);

        // Intro Card
        RoundedPanel introCard = new RoundedPanel(10);
        introCard.setLayout(new BoxLayout(introCard, BoxLayout.Y_AXIS));
        introCard.setBackground(Color.WHITE);
        introCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel introTitle = new JLabel("Intro");
        introTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        introTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bioLabel = new JLabel("<html><body style='width: 250px; text-align: center'>"
                + (user.getBio() != null ? user.getBio() : "No bio added") + "</body></html>");
        bioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        introCard.add(introTitle);
        introCard.add(Box.createVerticalStrut(15));
        introCard.add(bioLabel);
        introCard.add(Box.createVerticalStrut(15));
        introCard.add(new JSeparator());
        introCard.add(Box.createVerticalStrut(10));

        // Details
        introCard.add(createDetailItem("🎂 Born on " + user.getBirth()));
        introCard.add(Box.createVerticalStrut(8));
        introCard.add(createDetailItem("⚧ " + user.getGender()));

        leftCol.add(introCard);

        // Photos Preview (static for now)
        leftCol.add(Box.createVerticalStrut(15));
        RoundedPanel photosCard = new RoundedPanel(10);
        photosCard.setLayout(new BorderLayout());
        photosCard.setBackground(Color.WHITE);
        photosCard.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel photosTitle = new JLabel("Photos");
        photosTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel noPhotos = new JLabel("No photos");
        noPhotos.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        photosCard.add(photosTitle, BorderLayout.NORTH);
        photosCard.add(noPhotos, BorderLayout.CENTER);

        leftCol.add(photosCard);
        leftCol.setPreferredSize(new Dimension(360, 500));
        leftCol.setMaximumSize(new Dimension(360, 1000));

        // Right Col (Feed)
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(BG_COLOR);

        boolean isMe = user.getCredentials().getUsername().equals(Main.current.getCredentials().getUsername());

        if (isMe) {
            // Create Post Box
            RoundedPanel createPostCard = new RoundedPanel(8);
            createPostCard.setBackground(Color.WHITE);
            createPostCard.setLayout(new BorderLayout(10, 0));
            createPostCard.setBorder(new EmptyBorder(12, 12, 12, 12));
            createPostCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JPanel createContent = new JPanel(new BorderLayout(10, 0));
            createContent.setOpaque(false);

            // Mini Pic
            JLabel miniPic = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(FacebookGUI.FB_BLUE);
                    g2.fillOval(0, 0, 40, 40);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    String initial = user.getFirstname().substring(0, 1).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (40 - fm.stringWidth(initial)) / 2;
                    int y = (40 + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(initial, x, y);
                    g2.dispose();
                }
            };
            miniPic.setPreferredSize(new Dimension(40, 40));

            // Create post button (opens dialog) - Consistent with FeedPanel
            JButton createPostBtn = new JButton("What's on your mind?");
            createPostBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            createPostBtn.setForeground(new Color(101, 103, 107));
            createPostBtn.setHorizontalAlignment(SwingConstants.LEFT);
            createPostBtn.setContentAreaFilled(false);
            createPostBtn.setBorderPainted(false);
            createPostBtn.setFocusPainted(false);
            createPostBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            createPostBtn.setOpaque(true);
            createPostBtn.setBackground(new Color(240, 242, 245));
            createPostBtn.setBorder(new EmptyBorder(8, 12, 8, 12));

            createPostBtn.addActionListener(e -> showCreatePostDialog());

            createContent.add(miniPic, BorderLayout.WEST);
            createContent.add(createPostBtn, BorderLayout.CENTER);

            createPostCard.add(createContent);

            rightCol.add(createPostCard);
            rightCol.add(Box.createVerticalStrut(15));
        }

        // Feed List
        ArrayList<Post> myPosts = Database.Load_User_Posts(user.getCredentials().getUsername());
        if (myPosts.isEmpty()) {
            JLabel empty = new JLabel("No posts yet.");
            empty.setFont(new Font("Segoe UI", Font.BOLD, 18));
            empty.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            rightCol.add(Box.createVerticalStrut(40));
            rightCol.add(empty);
        } else {
            // Sort by time descending theoretically, mostly they depend on file order
            for (Post p : myPosts) {
                rightCol.add(createPostComponent(p));
                rightCol.add(Box.createVerticalStrut(15));
            }
        }

        container.add(leftCol, BorderLayout.WEST);
        container.add(rightCol, BorderLayout.CENTER);

        dynamicContentPanel.add(container);
    }

    private void loadAboutView() {
        RoundedPanel panel = new RoundedPanel(10);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("About");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createDetailRow("Bio", user.getBio()));
        panel.add(new JSeparator());
        panel.add(createDetailRow("First Name", user.getFirstname()));
        panel.add(new JSeparator());
        panel.add(createDetailRow("Last Name", user.getLastname()));
        panel.add(new JSeparator());
        panel.add(createDetailRow("Gender", user.getGender().toString()));
        panel.add(new JSeparator());
        panel.add(createDetailRow("Date of Birth", user.getBirth().toString()));

        dynamicContentPanel.add(panel);
    }

    private void loadFriendsView() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);

        RoundedPanel card = new RoundedPanel(10);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Friends");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        card.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20)); // 2 columns
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(20, 0, 0, 0));

        ArrayList<String> friends = Database.Load_Friends(user.getCredentials().getUsername());
        if (friends.isEmpty()) {
            card.add(new JLabel("No friends found."), BorderLayout.CENTER);
        } else {
            for (String f : friends) {
                User u = Database.LoadUser(f);
                if (u != null) {
                    grid.add(createFriendGridItem(u));
                }
            }
            card.add(grid, BorderLayout.CENTER);
        }

        container.add(card, BorderLayout.NORTH);
        dynamicContentPanel.add(container);
    }

    private void loadPhotosView() {
        RoundedPanel panel = new RoundedPanel(10);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel icon = new JLabel("📷", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));

        JLabel text = new JLabel("No photos posted yet", SwingConstants.CENTER);
        text.setFont(new Font("Segoe UI", Font.BOLD, 24));
        text.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);
        center.add(icon);
        center.add(text);

        panel.add(center, BorderLayout.CENTER);
        dynamicContentPanel.add(panel);
    }

    // ================= HELPERS =================

    private JPanel createDetailItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(FacebookGUI.FB_TEXT_PRIMARY);
        p.add(lbl);
        return p;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        JLabel v = new JLabel(value != null ? value : "N/A");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private JPanel createFriendGridItem(User u) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(FacebookGUI.FB_BORDER));
        p.setPreferredSize(new Dimension(200, 80));

        // Pic
        JLabel pic = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 0, 60, 60);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String initial = u.getFirstname().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (60 - fm.stringWidth(initial)) / 2;
                int y = (60 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        pic.setPreferredSize(new Dimension(60, 60));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel name = new JLabel(u.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));

        info.add(name);
        // Maybe add mutual friends count later

        p.add(pic, BorderLayout.WEST);
        p.add(info, BorderLayout.CENTER);

        // Add Padding
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapper.add(p, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createPostComponent(Post post) {
        RoundedPanel card = new RoundedPanel(8);
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel pic = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 0, 40, 40);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String initial = user.getFirstname().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (40 - fm.stringWidth(initial)) / 2, (40 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        pic.setPreferredSize(new Dimension(40, 40));

        JPanel meta = new JPanel(new GridLayout(2, 1));
        meta.setOpaque(false);
        JLabel name = new JLabel(user.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel time = new JLabel(post.getTime().toString().split("T")[0]); // Simplified date
        time.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        time.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        meta.add(name);
        meta.add(time);

        header.add(pic, BorderLayout.WEST);
        header.add(meta, BorderLayout.CENTER);

        // Content
        JTextArea content = new JTextArea(post.getText());
        content.setWrapStyleWord(true);
        content.setLineWrap(true);
        content.setEditable(false);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Bigger font for post content
        content.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        actions.setOpaque(false);

        // Just visual buttons for now
        JButton like = new JButton("Like");
        like.setFont(new Font("Segoe UI", Font.BOLD, 14));
        like.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        like.setContentAreaFilled(false);
        like.setBorderPainted(false);

        JButton comment = new JButton("Comment");
        comment.setFont(new Font("Segoe UI", Font.BOLD, 14));
        comment.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        comment.setContentAreaFilled(false);
        comment.setBorderPainted(false);

        actions.add(like);
        actions.add(comment);

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    private void showCreatePostDialog() {
        JDialog dialog = new JDialog(javax.swing.SwingUtilities.getWindowAncestor(this), "Create Post",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Create post");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JTextArea contentArea = new JTextArea("What's on your mind, " + Main.current.getFirstname() + "?");
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        contentArea.setForeground(new Color(150, 150, 150));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (contentArea.getForeground().equals(new Color(150, 150, 150))) {
                    contentArea.setText("");
                    contentArea.setForeground(FacebookGUI.FB_TEXT_PRIMARY);
                }
            }
        });

        // Privacy selector
        String[] privacyOptions = { "Friends", "Friends of Friends", "Everyone" };
        JComboBox<String> privacyCombo = new JComboBox<>(privacyOptions);
        privacyCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        privacyCombo.setBackground(new Color(240, 242, 245));
        privacyCombo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel privacyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        privacyPanel.setBackground(Color.WHITE);
        privacyPanel.add(new JLabel("Who can see this? "));
        privacyPanel.add(privacyCombo);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(219, 223, 231)));

        AnimatedButton postBtn = new AnimatedButton("Post", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        postBtn.setPreferredSize(new Dimension(0, 40));

        postBtn.addActionListener(e -> {
            String content = contentArea.getText().trim();
            if (!content.isEmpty() && !content.equals("What's on your mind, " + Main.current.getFirstname() + "?")) {
                Post newPost = new Post(content, Main.current.getCredentials().getUsername());
                String path = Database.Write_Post(newPost);
                Database.WriteFeed(path, Main.current.getCredentials().getUsername(), newPost);

                // Determine audience based on selection
                int selectedPrivacy = privacyCombo.getSelectedIndex();
                java.util.List<String> recipients;

                if (selectedPrivacy == 0) { // Friends
                    recipients = Database.Load_Friends(Main.current.getCredentials().getUsername());
                } else if (selectedPrivacy == 1) { // Friends of Friends
                    recipients = Database.Load_everyone(2);
                } else { // Everyone
                    recipients = Database.Load_everyone(6);
                }

                // Add to feeds
                Main.Add_in_Feed(recipients, path, newPost, false);

                dialog.dispose();
                loadPostsView(); // Refresh local view
            }
        });

        panel.add(title, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(privacyPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(postBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
