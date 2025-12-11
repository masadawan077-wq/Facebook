package com.facebook.gui;

import com.facebook.Database;
import com.facebook.Main;
import com.facebook.Message;
import com.facebook.Group_chat;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Chat Dialog for Group Chats
 */
public class GroupChatDialog extends JDialog {

    private Group_chat groupChat;
    private JPanel messagesPanel;
    private ModernTextField messageField;
    private JScrollPane scrollPane;
    private ArrayList<Message> messages;
    private Timer refreshTimer;

    public GroupChatDialog(FacebookGUI parent, Group_chat groupChat) {
        super(parent, "Group Chat", false);
        this.groupChat = groupChat;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        setSize(450, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadMessages();
        startRefreshTimer();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopRefreshTimer();
            }
        });

        // Drag support
        MouseAdapter ma = new MouseAdapter() {
            private int x, y;

            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }

            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - x, getLocation().y + e.getY() - y);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 15, 12, 15));
        header.setPreferredSize(new Dimension(0, 70));

        // Window Controls
        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowControls.setOpaque(false);
        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(FacebookGUI.FB_ERROR);
            }

            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            }
        });
        windowControls.add(closeBtn);

        // Group Icon
        JLabel groupIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 5, 30, 30);
                g2.setColor(FacebookGUI.FB_GREEN);
                g2.fillOval(10, 5, 30, 30);
                g2.dispose();
            }
        };
        groupIcon.setPreferredSize(new Dimension(45, 40));

        // Group Info
        JPanel groupInfo = new JPanel();
        groupInfo.setLayout(new BoxLayout(groupInfo, BoxLayout.Y_AXIS));
        groupInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(groupChat.getGroupName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel metaLabel = new JLabel(groupChat.getMembers().size() + " members");
        metaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        metaLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        groupInfo.add(nameLabel);
        groupInfo.add(metaLabel);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(groupIcon);
        leftHeader.add(groupInfo);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(windowControls, BorderLayout.EAST);

        // Messages area
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);
        messagesPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        messageField = new ModernTextField("Type a message...");
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        AnimatedButton sendBtn = new AnimatedButton("Send", FacebookGUI.FB_BLUE, FacebookGUI.FB_BLUE_HOVER);
        sendBtn.setPreferredSize(new Dimension(80, 45));
        sendBtn.addActionListener(e -> sendMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadMessages() {
        messagesPanel.removeAll();
        messages = Database.Load_ALLMessages(groupChat.getFolder_path());

        if (messages.isEmpty()) {
            JLabel noMessages = new JLabel("No messages yet. Say hello!");
            noMessages.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noMessages.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            noMessages.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesPanel.add(Box.createVerticalStrut(100));
            messagesPanel.add(noMessages);
        } else {
            for (Message msg : messages) {
                messagesPanel.add(createMessageItem(msg));
                messagesPanel.add(Box.createVerticalStrut(8));
            }
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createMessageItem(Message msg) {
        boolean isSentByMe = msg.getSender().equals(Main.current.getFirstname());

        JPanel container = new JPanel(new FlowLayout(isSentByMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

        JPanel contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setOpaque(false);

        if (!isSentByMe) {
            JLabel senderLabel = new JLabel(msg.getSender());
            senderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            senderLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            senderLabel.setBorder(new EmptyBorder(0, 5, 2, 0));
            contentWrapper.add(senderLabel);
        }

        RoundedPanel bubble = new RoundedPanel(18, false);
        bubble.setBackground(isSentByMe ? FacebookGUI.FB_BLUE : new Color(240, 242, 245));
        bubble.setBorder(new EmptyBorder(10, 15, 10, 15));
        bubble.setLayout(new BorderLayout());

        JTextArea messageArea = new JTextArea(msg.getContent());
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageArea.setForeground(isSentByMe ? Color.WHITE : FacebookGUI.FB_TEXT_PRIMARY);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setOpaque(false);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);

        int maxWidth = 280;
        FontMetrics fm = messageArea.getFontMetrics(messageArea.getFont());
        int textWidth = fm.stringWidth(msg.getContent()) + 30;
        int preferredWidth = Math.min(Math.max(textWidth, 50), maxWidth);
        messageArea.setSize(new Dimension(preferredWidth, Short.MAX_VALUE));

        if (textWidth > maxWidth) {
            messageArea.setColumns(25);
        }

        bubble.add(messageArea, BorderLayout.CENTER);
        contentWrapper.add(bubble);
        container.add(contentWrapper);
        return container;
    }

    private void sendMessage() {
        String content = messageField.getText().trim();
        if (!content.isEmpty()) {
            Message msg = new Message(content, Main.current.getFirstname());
            Database.WriteMessage(groupChat.getFolder_path(), msg);

            // Notifications for all other members
            for (String member : groupChat.getMembers()) {
                if (!member.equals(Main.current.getCredentials().getUsername())) {
                    Database.Write_Notification(member, Main.Input_NotificationM());
                }
            }

            messageField.setText("");
            loadMessages();
        }
    }

    private void startRefreshTimer() {
        refreshTimer = new Timer(2000, e -> {
            ArrayList<Message> newMessages = Database.Load_ALLMessages(groupChat.getFolder_path());
            if (newMessages.size() > messages.size()) {
                loadMessages();
            }
        });
        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null)
            refreshTimer.stop();
    }
}
