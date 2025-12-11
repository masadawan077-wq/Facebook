package com.facebook.gui;

import com.facebook.Database;
import com.facebook.Main;
import com.facebook.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Right Sidebar showing friends list and group chats
 * Features: Clickable friends to open chat, online indicators
 */
public class RightSidebar extends JPanel {

    private FacebookGUI parent;
    private HomePage homePage;
    private JPanel friendsListPanel;
    private JPanel groupChatsPanel;

    public RightSidebar(FacebookGUI parent, HomePage homePage) {
        this.parent = parent;
        this.homePage = homePage;
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initComponents();
    }

    private void initComponents() {
        // CONTACTS Header
        JPanel contactsHeader = new JPanel(new BorderLayout());
        contactsHeader.setBackground(Color.WHITE);
        contactsHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contactsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel contactsLabel = new JLabel("Contacts");
        contactsLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        contactsLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        // Search and options icons for contacts (visual only)
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        iconPanel.setBackground(Color.WHITE);
        JLabel searchIcon = new JLabel("🔍");
        JLabel optionsIcon = new JLabel("•••");
        iconPanel.add(searchIcon);
        iconPanel.add(optionsIcon);

        contactsHeader.add(contactsLabel, BorderLayout.WEST);
        contactsHeader.add(iconPanel, BorderLayout.EAST);

        add(contactsHeader);
        add(Box.createVerticalStrut(10));

        // Friends list
        friendsListPanel = new JPanel();
        friendsListPanel.setLayout(new BoxLayout(friendsListPanel, BoxLayout.Y_AXIS));
        friendsListPanel.setBackground(Color.WHITE);
        friendsListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadFriendsList();

        add(friendsListPanel);
        add(Box.createVerticalStrut(20));

        // Separator
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(new Color(219, 223, 231));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(sep2);
        add(Box.createVerticalStrut(15));

        // Group chats header
        JLabel groupChatsLabel = new JLabel("Group chats");
        groupChatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        groupChatsLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        groupChatsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(groupChatsLabel);
        add(Box.createVerticalStrut(15));

        // Group chats list
        groupChatsPanel = new JPanel();
        groupChatsPanel.setLayout(new BoxLayout(groupChatsPanel, BoxLayout.Y_AXIS));
        groupChatsPanel.setBackground(Color.WHITE);
        groupChatsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loadGroupChats();

        add(groupChatsPanel);

        // Create group chat button
        add(Box.createVerticalStrut(10));
        JPanel createGroupPanel = createCreateGroupChatButton();
        createGroupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(createGroupPanel);

        add(Box.createVerticalGlue());
    }

    private void loadFriendsList() {
        friendsListPanel.removeAll();

        ArrayList<String> friends = Database.Load_Friends(Main.current.getCredentials().getUsername());

        if (friends.isEmpty()) {
            JLabel noFriends = new JLabel("No friends yet");
            noFriends.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noFriends.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            friendsListPanel.add(noFriends);
        } else {
            for (String friendUsername : friends) {
                User friend = Database.LoadUser(friendUsername);
                if (friend != null) {
                    JPanel friendItem = createFriendItem(friend);
                    friendsListPanel.add(friendItem);
                }
            }
        }

        friendsListPanel.revalidate();
        friendsListPanel.repaint();
    }

    private JPanel createFriendItem(User friend) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(6, 8, 6, 8));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Profile circle with online indicator
        JLabel profileCircle = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Draw circle
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 0, 36, 36);

                // Draw initial
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String initial = friend.getFirstname().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (36 - fm.stringWidth(initial)) / 2;
                int y = (36 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, x, y);

                // Online indicator
                boolean isOnline = Database.Check_Online(friend.getCredentials().getUsername());
                if (isOnline) {
                    g2.setColor(new Color(66, 183, 42));
                    g2.fillOval(26, 26, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(26, 26, 10, 10);
                }

                g2.dispose();
            }
        };
        profileCircle.setPreferredSize(new Dimension(36, 36));

        // Name label
        JLabel nameLabel = new JLabel(friend.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nameLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);

        // Message Button (Small, minmalistic green)
        // Use AnimatedButton for rounding
        com.facebook.gui.components.AnimatedButton msgBtn = new com.facebook.gui.components.AnimatedButton("Message",
                FacebookGUI.FB_GREEN, FacebookGUI.FB_GREEN_HOVER);
        msgBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        msgBtn.setPreferredSize(new Dimension(70, 24)); // Compact
        msgBtn.setCornerRadius(12); // Round
        msgBtn.setMargin(new Insets(0, 0, 0, 0));

        // msgBtn:
        msgBtn.addActionListener(e -> {
            homePage.openChatWithFriend(friend.getCredentials().getUsername());
        });

        panel.add(profileCircle, BorderLayout.WEST);

        panel.add(nameLabel, BorderLayout.CENTER);

        // To ensure the button doesn't stretch vertically in Borderlayout.EAST, wrap
        // it.
        JPanel eastWrapper = new JPanel(new GridBagLayout());
        eastWrapper.setOpaque(false);
        eastWrapper.add(msgBtn);

        panel.add(eastWrapper, BorderLayout.EAST);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(240, 242, 245));
                eastWrapper.setBackground(new Color(240, 242, 245)); // Ensure wrapper matches
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
                eastWrapper.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Clicking row also opens chat, consistent with previous behavior?
                // Or maybe remove row click if button exists?
                // User said "message button ... like everything else".
                // Usually entire row is clickable. Making button explicit is fine.
                homePage.openChatWithFriend(friend.getCredentials().getUsername());
            }
        });

        return panel;
    }

    private void loadGroupChats() {
        groupChatsPanel.removeAll();

        // Load group chats from inbox
        ArrayList<com.facebook.Chat> chats = Database.LoadInbox();
        int groupCount = 0;

        for (com.facebook.Chat chat : chats) {
            if (chat instanceof com.facebook.Group_chat) {
                com.facebook.Group_chat gc = (com.facebook.Group_chat) chat;
                JPanel groupItem = createGroupChatItem(gc);
                groupChatsPanel.add(groupItem);
                groupCount++;
            }
        }

        if (groupCount == 0) {
            JLabel noGroups = new JLabel("No group chats");
            noGroups.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noGroups.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            groupChatsPanel.add(noGroups);
        }

        groupChatsPanel.revalidate();
        groupChatsPanel.repaint();
    }

    private JPanel createGroupChatItem(com.facebook.Group_chat groupChat) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(6, 8, 6, 8));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Group icon (multiple circles)
        JLabel groupIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw overlapping circles
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 6, 24, 24);
                g2.setColor(FacebookGUI.FB_GREEN);
                g2.fillOval(12, 6, 24, 24);

                g2.dispose();
            }
        };
        groupIcon.setPreferredSize(new Dimension(36, 36));

        // Group name
        JLabel nameLabel = new JLabel(groupChat.getGroupName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nameLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);

        panel.add(groupIcon, BorderLayout.WEST);
        panel.add(nameLabel, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(240, 242, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                homePage.openChatWithGroup(groupChat);
            }
        });

        return panel;
    }

    private void showCreateGroupDialog() {
        JDialog dialog = new JDialog(parent, "Create Group Chat", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel("Create Group");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // Name & Description
        JPanel fieldsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        fieldsPanel.setBackground(Color.WHITE);

        com.facebook.gui.components.ModernTextField nameField = new com.facebook.gui.components.ModernTextField(
                "Group Name");
        com.facebook.gui.components.ModernTextField descField = new com.facebook.gui.components.ModernTextField(
                "Description");

        fieldsPanel.add(nameField);
        fieldsPanel.add(descField);

        // Friend Selector
        JPanel friendsList = new JPanel();
        friendsList.setLayout(new BoxLayout(friendsList, BoxLayout.Y_AXIS));
        friendsList.setBackground(Color.WHITE);

        ArrayList<String> selectedFriends = new ArrayList<>();
        ArrayList<String> myFriends = Database.Load_Friends(Main.current.getCredentials().getUsername());

        for (String f : myFriends) {
            JCheckBox cb = new JCheckBox(Database.LoadUser(f).getFullName());
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            cb.setBackground(Color.WHITE);
            cb.setFocusPainted(false);
            cb.addItemListener(e -> {
                if (cb.isSelected())
                    selectedFriends.add(f);
                else
                    selectedFriends.remove(f);
            });
            friendsList.add(cb);
        }

        JScrollPane scroll = new JScrollPane(friendsList);
        scroll.setBorder(BorderFactory.createTitledBorder("Add Members"));
        scroll.setBackground(Color.WHITE);

        // Create Button
        com.facebook.gui.components.AnimatedButton createBtn = new com.facebook.gui.components.AnimatedButton(
                "Create Group", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        createBtn.setPreferredSize(new Dimension(0, 45));
        createBtn.addActionListener(e -> {
            String gName = nameField.getText().trim();
            String gDesc = descField.getText().trim();

            if (gName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Group name is required!");
                return;
            }
            if (selectedFriends.size() < 1) { // Min 2 ppl (me + 1)
                JOptionPane.showMessageDialog(dialog, "Select at least 1 friend.");
                return;
            }

            // Logic from CLI Page.Create_Group_Chat
            ArrayList<String> members = new ArrayList<>(selectedFriends);
            members.add(Main.current.getCredentials().getUsername()); // Add self

            com.facebook.Group_chat newGroup = new com.facebook.Group_chat(gName, gDesc, members);

            // distribute to all members
            for (String m : members) {
                Database.WriteChat(m, newGroup);
            }

            JOptionPane.showMessageDialog(dialog, "Group Created!");
            dialog.dispose();
            refresh(); // Reload sidebar
        });

        panel.add(title, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 10));
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(fieldsPanel, BorderLayout.NORTH);
        centerWrapper.add(scroll, BorderLayout.CENTER);

        panel.add(centerWrapper, BorderLayout.CENTER);
        panel.add(createBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JPanel createCreateGroupChatButton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(6, 8, 6, 8));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel plusIcon = new JLabel("+");
        plusIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        plusIcon.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        JLabel textLabel = new JLabel("Create group chat");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        panel.add(plusIcon);
        panel.add(textLabel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(240, 242, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showCreateGroupDialog();
            }
        });

        return panel;
    }

    public void refresh() {
        loadFriendsList();
        loadGroupChats();
    }

}
