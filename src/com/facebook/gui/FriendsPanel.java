package com.facebook.gui;

import com.facebook.Database;
import com.facebook.Main;
import com.facebook.User;
import com.facebook.Message;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class FriendsPanel extends JPanel {

    private HomePage homePage;
    private JPanel contentPanel;
    private JPanel tabsPanel;
    private String currentTab = "All Friends"; // "All Friends", "Requests", "Find Friends"

    public FriendsPanel(FacebookGUI parent, HomePage homePage) {
        this.homePage = homePage;
        setBackground(FacebookGUI.FB_BACKGROUND);
        setLayout(new BorderLayout());

        initComponents();
        showAllFriends();
    }

    private void initComponents() {
        // Wrapper for header and tabs
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(FacebookGUI.FB_BACKGROUND);
        topPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        // Header
        JLabel title = new JLabel("Friends");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(FacebookGUI.FB_TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tabs
        tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        tabsPanel.setBackground(FacebookGUI.FB_BACKGROUND);
        tabsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tabsPanel.add(createTabButton("Chats"));
        tabsPanel.add(createTabButton("All Friends"));
        tabsPanel.add(createTabButton("Requests"));
        tabsPanel.add(createTabButton("Sent"));
        tabsPanel.add(createTabButton("Find Friends"));
        tabsPanel.add(createTabButton("Suggestions"));

        topPanel.add(title);
        topPanel.add(tabsPanel);

        add(topPanel, BorderLayout.NORTH);

        // Content Area
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(FacebookGUI.FB_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private void updateTabs(String activeName) {
        currentTab = activeName;
        for (Component c : tabsPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b.getText().equals(activeName)) {
                    b.setForeground(FacebookGUI.FB_BLUE);
                    b.setBackground(new Color(235, 245, 255));
                    b.setOpaque(true);
                } else {
                    b.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
                    b.setOpaque(false);
                }
            }
        }
        tabsPanel.repaint();
    }

    private JButton createTabButton(String name) {
        JButton btn = new JButton(name);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (name.equals(currentTab)) {
            btn.setForeground(FacebookGUI.FB_BLUE);
            btn.setBackground(new Color(235, 245, 255));
            btn.setOpaque(true);
        } else {
            btn.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            btn.setOpaque(false);
        }

        btn.addActionListener(e -> {
            updateTabs(name);
            switch (name) {
                case "Chats" -> showChats();
                case "All Friends" -> showAllFriends();
                case "Requests" -> showRequests();
                case "Sent" -> showSentRequests();
                case "Find Friends" -> showFindFriends();
                case "Suggestions" -> showSuggestions();
            }
        });
        return btn;
    }

    private void showSuggestions() {
        contentPanel.removeAll();
        java.util.List<String> suggestions = Database.Load_Friend_of_Friends();
        java.util.HashMap<String, java.util.List<String>> mutuals = Database.mutual_frndz(suggestions);
        java.util.List<String> sorted = Database.Sort_by_mutual_count(suggestions, mutuals);

        if (sorted.isEmpty()) {
            addEmptyMessage("No suggestions available.");
        } else {
            for (String s : sorted) {
                User u = Database.LoadUser(s);
                if (u != null) {
                    contentPanel.add(createFindFriendRow(u));
                    contentPanel.add(Box.createVerticalStrut(15));
                }
            }
        }
        refreshUI();
    }

    private void showRequests() {
        contentPanel.removeAll();
        List<String> requests = Database.Load_Requests_Recieved();

        if (requests.isEmpty()) {
            addEmptyMessage("No new friend requests.");
        } else {
            for (String r : requests) {
                contentPanel.add(createRequestRow(Database.LoadUser(r)));
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }
        refreshUI();
    }

    private void showFindFriends() {
        contentPanel.removeAll();

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ModernTextField searchField = new ModernTextField("Search for people");
        AnimatedButton searchBtn = new AnimatedButton("Search", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        searchBtn.setPreferredSize(new Dimension(100, 42));
        searchBtn.setCornerRadius(21);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        // Wrap search bar in a container that allows width expansion but limits height
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FacebookGUI.FB_BACKGROUND);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        wrapper.add(searchPanel, BorderLayout.CENTER);

        contentPanel.add(wrapper);
        contentPanel.add(Box.createVerticalStrut(25));

        // Result Container
        JPanel resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(FacebookGUI.FB_BACKGROUND);
        contentPanel.add(resultsContainer);

        searchBtn.addActionListener(e -> {
            resultsContainer.removeAll();
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                ArrayList<User> results = Database.Search_Users_By_Name(query);
                if (results.isEmpty()) {
                    JLabel noRes = new JLabel("No people found matching '" + query + "'");
                    noRes.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noRes.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    resultsContainer.add(noRes);
                } else {
                    for (User u : results) {
                        resultsContainer.add(createFindFriendRow(u));
                        resultsContainer.add(Box.createVerticalStrut(15));
                    }
                }
            }
            refreshUI();
        });

        refreshUI();
    }

    public void showChats() {
        if (!currentTab.equals("Chats"))
            updateTabs("Chats");
        contentPanel.removeAll();

        ArrayList<com.facebook.Chat> chats = Database.LoadInbox();
        if (chats.isEmpty()) {
            addEmptyMessage("No active chats.");
        } else {
            for (com.facebook.Chat c : chats) {
                if (c instanceof com.facebook.DM_chat) {
                    com.facebook.DM_chat dm = (com.facebook.DM_chat) c;
                    String other = dm.getR_username();
                    // Get last message
                    ArrayList<Message> msgs = Database.Load_ALLMessages(dm.getFolder_path());
                    Message lastMsg = msgs.isEmpty() ? null : msgs.get(msgs.size() - 1);
                    contentPanel.add(createChatRow(Database.LoadUser(other), lastMsg));
                    contentPanel.add(Box.createVerticalStrut(15));
                }
                // Group chats can be added here too
            }
        }
        refreshUI();
    }

    private JPanel createChatRow(User user, Message lastMsg) {
        JPanel row = createBaseCard(user);
        Component centerComp = ((BorderLayout) row.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComp instanceof JPanel) {
            JPanel info = (JPanel) centerComp;
            if (lastMsg != null) {
                String preview = lastMsg.getContent();
                if (preview.length() > 30)
                    preview = preview.substring(0, 27) + "...";
                if (lastMsg.getSender().equals(Main.current.getFirstname()))
                    preview = "You: " + preview;

                JLabel msgLabel = new JLabel(preview);
                msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                msgLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

                if (info.getComponentCount() > 1) {
                    info.remove(1);
                    info.add(msgLabel);
                }
            }
        }
        AnimatedButton openBtn = new AnimatedButton("Open", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        openBtn.setPreferredSize(new Dimension(80, 36));
        openBtn.setCornerRadius(18);
        openBtn.addActionListener(e -> {
            if (homePage != null) {
                homePage.openChatWithFriend(user.getCredentials().getUsername());
            }
        });
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(openBtn);
        row.add(actions, BorderLayout.EAST);
        return row;
    }

    private void showAllFriends() {
        contentPanel.removeAll();
        ArrayList<String> friends = Database.Load_Friends(Main.current.getCredentials().getUsername());

        if (friends.isEmpty()) {
            addEmptyMessage("No friends added yet.");
        } else {
            for (String f : friends) {
                contentPanel.add(createFriendRow(Database.LoadUser(f)));
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }
        refreshUI();
    }

    private void showSentRequests() {
        contentPanel.removeAll();
        ArrayList<String> sent = Database.Load_Requests_Sent();

        if (sent.isEmpty()) {
            addEmptyMessage("No sent requests.");
        } else {
            for (String s : sent) {
                contentPanel.add(createSentRequestRow(Database.LoadUser(s)));
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }
        refreshUI();
    }

    private JPanel createFriendRow(User user) {
        JPanel row = createBaseCard(user);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        AnimatedButton profileBtn = new AnimatedButton("View Profile", new Color(228, 230, 235),
                new Color(210, 213, 218));
        profileBtn.setForeground(Color.BLACK);
        profileBtn.setPreferredSize(new Dimension(110, 36));
        profileBtn.addActionListener(e -> homePage.showProfilePanel(user));

        AnimatedButton msgBtn = new AnimatedButton("Message", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        msgBtn.setPreferredSize(new Dimension(100, 36));
        msgBtn.addActionListener(e -> {
            if (homePage != null) {
                homePage.openChatWithFriend(user.getCredentials().getUsername());
            }
        });

        actions.add(profileBtn);

        // Unfriend Button
        AnimatedButton unfriendBtn = new AnimatedButton("Unfriend", new Color(228, 230, 235), new Color(210, 213, 218));
        unfriendBtn.setForeground(Color.BLACK);
        unfriendBtn.setPreferredSize(new Dimension(100, 36));
        unfriendBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to unfriend " + user.getFullName() + "?", "Unfriend",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Database.RemoveFriend(user.getCredentials().getUsername());
                showAllFriends(); // Refresh list
            }
        });

        actions.add(msgBtn);
        actions.add(unfriendBtn);

        row.add(actions, BorderLayout.EAST);
        return row;
    }

    private JPanel createSentRequestRow(User user) {
        JPanel row = createBaseCard(user);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        AnimatedButton cancelBtn = new AnimatedButton("Cancel Request", new Color(228, 230, 235),
                new Color(210, 213, 218));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setPreferredSize(new Dimension(140, 36));
        cancelBtn.addActionListener(e -> {
            Database.Delete_FriendRequest_Sent(user.getCredentials().getUsername());
            showSentRequests();
        });

        actions.add(cancelBtn);
        row.add(actions, BorderLayout.EAST);
        return row;
    }

    // ... Rest content (showRequests, createRequestRow, showFindFriends, etc) ...

    private JPanel createRequestRow(User user) {
        JPanel row = createBaseCard(user);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        AnimatedButton confirmBtn = new AnimatedButton("Confirm", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        confirmBtn.setPreferredSize(new Dimension(110, 38));

        AnimatedButton deleteBtn = new AnimatedButton("Delete", new Color(228, 230, 235), new Color(210, 213, 218));
        deleteBtn.setForeground(Color.BLACK);
        deleteBtn.setPreferredSize(new Dimension(110, 38));

        confirmBtn.addActionListener(e -> {
            Database.WriteFriend(user.getCredentials().getUsername());
            Database.Delete_FriendRequest_Recieved(user.getCredentials().getUsername());
            showRequests(); // Refresh list
        });

        deleteBtn.addActionListener(e -> {
            Database.Delete_FriendRequest_Recieved(user.getCredentials().getUsername());
            showRequests(); // Refresh list
        });

        actions.add(confirmBtn);
        actions.add(deleteBtn);

        row.add(actions, BorderLayout.EAST);
        return row;
    }

    private JPanel createFindFriendRow(User user) {
        JPanel row = createBaseCard(user);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        String username = user.getCredentials().getUsername();
        boolean isFriend = Database.Already_Friend(user);
        boolean requestSent = Database.F_Request_Already_sent(username);

        JButton actionBtn;

        if (isFriend) {
            actionBtn = new JButton("Friends");
            actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            actionBtn.setForeground(FacebookGUI.FB_GREEN);
            actionBtn.setContentAreaFilled(false);
            actionBtn.setBorderPainted(false);
            actionBtn.setEnabled(false);
        } else if (requestSent) {
            // Updated to be more distinct "Pending"
            actionBtn = new AnimatedButton("Pending", new Color(235, 245, 255), new Color(225, 235, 245));
            actionBtn.setForeground(FacebookGUI.FB_BLUE); // Blue text
            actionBtn.setToolTipText("Click to cancel request");
            actionBtn.addActionListener(e -> {
                Database.Delete_FriendRequest_Sent(username);
                showFindFriends(); // Refresh to update button state
            });
        } else {
            actionBtn = new AnimatedButton("Add Friend", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
            actionBtn.addActionListener(e -> {
                Database.Friend_Request_Sent(username);
                showFindFriends(); // Refresh
            });
        }

        actionBtn.setPreferredSize(new Dimension(140, 38));

        AnimatedButton profileBtn = new AnimatedButton("View Profile", new Color(228, 230, 235),
                new Color(210, 213, 218));
        profileBtn.setForeground(Color.BLACK);
        profileBtn.setPreferredSize(new Dimension(110, 36));
        profileBtn.addActionListener(e -> homePage.showProfilePanel(user));

        actions.add(profileBtn);
        actions.add(actionBtn);
        row.add(actions, BorderLayout.EAST);
        return row;
    }

    // Helper to generic row with profile pic and name
    private JPanel createBaseCard(User user) {
        JPanel row = new JPanel(new BorderLayout(20, 0)); // Increased gap
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(15, 20, 15, 20)); // Increased padding
        row.setMaximumSize(new Dimension(800, 90)); // Increased height
        row.setPreferredSize(new Dimension(800, 90));
        row.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left

        // Profile Icon
        JLabel profile = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 0, 60, 60); // Bigger profile

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Bigger initial
                String initial = user.getFirstname().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (60 - fm.stringWidth(initial)) / 2;
                int y = (60 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        profile.setPreferredSize(new Dimension(60, 60));

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setOpaque(false);
        JLabel name = new JLabel(user.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Bigger Name

        JLabel sub = new JLabel(user.getCredentials().getUsername());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        info.add(name);
        info.add(sub);

        row.add(profile, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);

        return row;
    }

    private void addEmptyMessage(String msg) {
        JLabel label = new JLabel(msg);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createVerticalStrut(40));
        contentPanel.add(label);
    }

    private void refreshUI() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
