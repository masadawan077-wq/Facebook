package com.facebook.gui;

import com.facebook.Main;

import javax.swing.*;
import java.awt.*;

/**
 * Facebook Home Page - Main dashboard after login
 * Features: Top navbar, left sidebar, feed, right sidebar (friends list)
 */
public class HomePage extends JPanel {

    private FacebookGUI parent;
    private TopNavBar topNavBar;
    private LeftSidebar leftSidebar;
    private FeedPanel feedPanel;
    private RightSidebar rightSidebar;
    private FriendsPanel friendsPanel;

    private JPanel mainContent;
    private JScrollPane leftScrollPane;
    private JScrollPane feedScrollPane;
    private JScrollPane rightScrollPane;

    public HomePage(FacebookGUI parent) {
        this.parent = parent;
        setBackground(FacebookGUI.FB_BACKGROUND);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Top Navigation Bar
        topNavBar = new TopNavBar(parent, this);
        add(topNavBar, BorderLayout.NORTH);

        // Main content area
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(FacebookGUI.FB_BACKGROUND);

        // Left Sidebar
        leftSidebar = new LeftSidebar(parent, this);
        leftScrollPane = new JScrollPane(leftSidebar);
        leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        leftScrollPane.setPreferredSize(new Dimension(300, 0)); // Increased width

        // Center Feed
        feedPanel = new FeedPanel(parent, this);
        feedScrollPane = new JScrollPane(feedPanel);
        feedScrollPane.setBorder(BorderFactory.createEmptyBorder());
        feedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        feedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        feedScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Right Sidebar (Friends/Contacts)
        rightSidebar = new RightSidebar(parent, this);
        rightScrollPane = new JScrollPane(rightSidebar);
        rightScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightScrollPane.setPreferredSize(new Dimension(250, 0)); // Reduced width

        mainContent.add(leftScrollPane, BorderLayout.WEST);
        mainContent.add(feedScrollPane, BorderLayout.CENTER);
        mainContent.add(rightScrollPane, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
    }

    public void openChatWithFriend(String username) {
        ChatDialog chatDialog = new ChatDialog(parent, username);
        chatDialog.setVisible(true);
    }

    public void openGamesDialog() {
        GamesDialog gamesDialog = new GamesDialog(parent);
        gamesDialog.setVisible(true);
    }

    public void refreshFeed() {
        feedPanel.refreshFeed();
    }

    public void showFriendsPanel() {
        if (friendsPanel == null) {
            friendsPanel = new FriendsPanel(parent, this);
        }

        mainContent.removeAll();
        mainContent.add(leftScrollPane, BorderLayout.WEST);

        JScrollPane friendsScrollPane = new JScrollPane(friendsPanel);
        friendsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        friendsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        friendsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        friendsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainContent.add(friendsScrollPane, BorderLayout.CENTER);
        mainContent.add(rightScrollPane, BorderLayout.EAST);

        mainContent.revalidate();
        mainContent.repaint();
    }

    public void showMessages() {
        showFriendsPanel(); // Load panel
        friendsPanel.showChats(); // Switch to chats
    }

    public void showFeedPanel() {
        mainContent.removeAll();
        mainContent.add(leftScrollPane, BorderLayout.WEST);
        mainContent.add(feedScrollPane, BorderLayout.CENTER);
        mainContent.add(rightScrollPane, BorderLayout.EAST);

        feedPanel.refreshFeed();

        mainContent.revalidate();
        mainContent.repaint();
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            com.facebook.Database.Delete_Online();
            Main.current = null;
            parent.showLoginPanel();
        }
    }
}
