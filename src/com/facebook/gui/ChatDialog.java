package com.facebook.gui;

import com.facebook.Database;
import com.facebook.Main;
import com.facebook.Message;
import com.facebook.User;
import com.facebook.DM_chat;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Chat Dialog for messaging friends
 * Features: Real-time messaging, message history, send messages
 */
public class ChatDialog extends JDialog {

    private String friendUsername;
    private User friend;
    private DM_chat chat;
    private JPanel messagesPanel;
    private ModernTextField messageField;
    private JScrollPane scrollPane;
    private ArrayList<Message> messages;
    private Timer refreshTimer;

    public ChatDialog(FacebookGUI parent, String friendUsername) {
        super(parent, "Chat", false);
        this.friendUsername = friendUsername;
        this.friend = Database.LoadUser(friendUsername);

        if (friend == null) {
            dispose();
            return;
        }

        // Load or create chat
        loadChat();

        setUndecorated(true); // Modern look
        setBackground(new Color(0, 0, 0, 0)); // Transparent for shapes

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

    private void loadChat() {
        // Check if chat exists
        ArrayList<com.facebook.Chat> chats = Database.LoadInbox();
        for (com.facebook.Chat c : chats) {
            if (c instanceof DM_chat) {
                DM_chat dm = (DM_chat) c;
                if (dm.getR_username().equals(friendUsername)) {
                    chat = dm;
                    return;
                }
            }
        }

        // Create new chat if doesn't exist
        String currUsername = Main.current.getCredentials().getUsername();
        chat = new DM_chat(currUsername, friendUsername);
        DM_chat chat2 = new DM_chat(friendUsername, currUsername);
        Database.WriteChat(currUsername, chat);
        Database.WriteChat(friendUsername, chat2);
    }

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Custom Header with Close Button
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(12, 15, 12, 15));
        header.setPreferredSize(new Dimension(0, 70));

        // Window Controls
        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowControls.setOpaque(false);
        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger
        closeBtn.setForeground(Color.BLACK); // High visibility
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

        // Friend profile in header
        JLabel profileCircle = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(0, 0, 40, 40);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String initial = friend.getFirstname().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (40 - fm.stringWidth(initial)) / 2;
                int y = (40 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, x, y);

                // Online indicator
                if (Database.Check_Online(friendUsername)) {
                    g2.setColor(new Color(66, 183, 42));
                    g2.fillOval(28, 28, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(28, 28, 12, 12);
                }

                g2.dispose();
            }
        };
        profileCircle.setPreferredSize(new Dimension(40, 40));

        // Friend info
        JPanel friendInfo = new JPanel();
        friendInfo.setLayout(new BoxLayout(friendInfo, BoxLayout.Y_AXIS));
        friendInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(friend.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel statusLabel = new JLabel(Database.Check_Online(friendUsername) ? "Active now" : "Offline");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);

        friendInfo.add(nameLabel);
        friendInfo.add(statusLabel);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(profileCircle);
        leftHeader.add(friendInfo);

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
        messages = Database.Load_ALLMessages(chat.getFolder_path());

        if (messages.isEmpty()) {
            JLabel noMessages = new JLabel("No messages yet. Start the conversation!");
            noMessages.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noMessages.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            noMessages.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesPanel.add(Box.createVerticalStrut(100));
            messagesPanel.add(noMessages);
        } else {
            for (Message msg : messages) {
                JPanel messageItem = createMessageItem(msg);
                messagesPanel.add(messageItem);
                messagesPanel.add(Box.createVerticalStrut(8));
            }
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();

        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createMessageItem(Message msg) {
        boolean isSentByMe = msg.getSender().equals(Main.current.getFirstname());

        JPanel container = new JPanel(new FlowLayout(isSentByMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000)); // Allow dynamic height

        RoundedPanel bubble = new RoundedPanel(18, false); // No shadow for chat bubbles
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

        // Calculate size ensuring it doesn't get too wide or too narrow
        int maxWidth = 280;
        FontMetrics fm = messageArea.getFontMetrics(messageArea.getFont());
        int textWidth = fm.stringWidth(msg.getContent()) + 30; // buffer

        int preferredWidth = Math.min(Math.max(textWidth, 50), maxWidth);
        // We need to set columns/size to force wrapping at maxWidth if needed
        messageArea.setSize(new Dimension(preferredWidth, Short.MAX_VALUE));

        // This is a bit hacky in Swing without proper layout manager tweaking
        // but let's try setting preferred size on the JTextArea
        // If content is long, we cap width, height grows.
        // If content is short, width is small.

        if (textWidth > maxWidth) {
            messageArea.setColumns(25); // Approx chars for 280px
        }

        bubble.add(messageArea, BorderLayout.CENTER);

        container.add(bubble);
        return container;
    }

    private void sendMessage() {
        String content = messageField.getText().trim();
        if (!content.isEmpty()) {
            Message msg = new Message(content, Main.current.getFirstname());
            Database.WriteMessage(chat.getFolder_path(), msg);
            Database.Write_Notification(friendUsername, Main.Input_NotificationM());

            messageField.setText("");
            loadMessages();
        }
    }

    private void startRefreshTimer() {
        refreshTimer = new Timer(2000, e -> {
            // Check for new messages
            ArrayList<Message> newMessages = Database.Load_ALLMessages(chat.getFolder_path());
            if (newMessages.size() > messages.size()) {
                loadMessages();
            }
        });
        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
}
