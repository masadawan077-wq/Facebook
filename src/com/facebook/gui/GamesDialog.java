package com.facebook.gui;

import com.facebook.Game;
import com.facebook.Main;
import com.facebook.TicTacToe;
import com.facebook.Hangman;
import com.facebook.SnakeGame;
import com.facebook.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Games Dialog showing available games from CLI
 * Features: TicTacToe, Hangman, Snake Game
 */
public class GamesDialog extends JDialog {

    private FacebookGUI parent;
    private ArrayList<Game> games;

    public GamesDialog(FacebookGUI parent) {
        super(parent, "Games", true);
        this.parent = parent;
        this.games = Main.Get_ALL_games();

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();

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

    private JPanel contentPanel;
    private String currentTab = "Games";
    private JPanel tabsPanel;

    // ... (Constructor remains same)

    private void initComponents() {
        RoundedPanel mainPanel = new RoundedPanel(15);
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 25, 25, 25));

        // Header with Close
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("🎮 Play Games");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);

        titlePanel.add(titleLabel);

        // Close Button
        JLabel closeBtn = new JLabel("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
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

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        closePanel.add(closeBtn);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(closePanel, BorderLayout.EAST);

        // Tabs
        tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabsPanel.setBackground(Color.WHITE);

        tabsPanel.add(createTabButton("Games"));
        tabsPanel.add(createTabButton("Invites"));

        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setBackground(Color.WHITE);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(tabsPanel, BorderLayout.CENTER);

        // Content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        showGames(); // Default view
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
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, FacebookGUI.FB_BLUE));
        } else {
            btn.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        }

        btn.addActionListener(e -> {
            currentTab = name;
            updateTabs();
            if (name.equals("Games"))
                showGames();
            else
                showInvites();
        });
        return btn;
    }

    private void updateTabs() {
        for (Component c : tabsPanel.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b.getText().equals(currentTab)) {
                    b.setForeground(FacebookGUI.FB_BLUE);
                    b.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, FacebookGUI.FB_BLUE));
                } else {
                    b.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
                    b.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
                }
            }
        }
    }

    private void showGames() {
        contentPanel.removeAll();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            JPanel gameCard = createGameCard(game);
            contentPanel.add(gameCard);
            if (i < games.size() - 1) {
                contentPanel.add(Box.createVerticalStrut(15));
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showInvites() {
        contentPanel.removeAll();
        ArrayList<com.facebook.Game_Invite> invites = com.facebook.Database.Load_Game_Invites();

        if (invites.isEmpty()) {
            JLabel empty = new JLabel("No game invites pending.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(50));
            contentPanel.add(empty);
        } else {
            for (com.facebook.Game_Invite invite : invites) {
                contentPanel.add(createInviteCard(invite));
                contentPanel.add(Box.createVerticalStrut(10));
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createInviteCard(com.facebook.Game_Invite invite) {
        RoundedPanel card = new RoundedPanel(10);
        card.setBackground(new Color(248, 249, 250));
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Icon
        JLabel icon = new JLabel(getGameIcon(invite.getGame()));
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(new JLabel("Game Invite: " + invite.getGame()));

        com.facebook.User sender = com.facebook.Database.LoadUser(invite.getSender()); // Need to get sender
        // Note: invite.getSender() needs to work.
        String senderName = (sender != null) ? sender.getFullName() : invite.getSender();

        JLabel sub = new JLabel("From: " + senderName);
        sub.setForeground(Color.GRAY);
        info.add(sub);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        AnimatedButton accept = new AnimatedButton("Accept", FacebookGUI.FB_GREEN, FacebookGUI.FB_GREEN_HOVER);
        accept.setPreferredSize(new Dimension(80, 30));
        accept.addActionListener(e -> {
            // Logic to launch game
            com.facebook.Database.Delete_Game_invite(invite);
            // Launch logic - need Game object.
            // We have game name. Loop through games list to find match
            for (Game g : games) {
                if (g.getName().equalsIgnoreCase(invite.getGame())) {
                    launchGame(g);
                    break;
                }
            }
        });

        AnimatedButton decline = new AnimatedButton("Decline", new Color(228, 230, 235), new Color(210, 213, 218));
        decline.setForeground(Color.BLACK);
        decline.setPreferredSize(new Dimension(80, 30));
        decline.addActionListener(e -> {
            com.facebook.Database.Delete_Game_invite(invite);
            showInvites();
        });

        actions.add(accept);
        actions.add(decline);

        card.add(icon, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);

        return card;
    }

    private JPanel createGameCard(Game game) {
        // ... (Existing implementation, no changes needed inside)
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(new Color(248, 249, 250));
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Game icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, FacebookGUI.FB_BLUE,
                        70, 70, new Color(100, 150, 255));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, 70, 70, 12, 12);

                // Draw game icon
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
                String icon = getGameIcon(game.getName());
                FontMetrics fm = g2.getFontMetrics();
                int x = (70 - fm.stringWidth(icon)) / 2;
                int y = (70 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(icon, x, y);

                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(70, 70));

        // Game info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(game.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(getGameDescription(game.getName()));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel playersLabel = new JLabel("👥 Multiplayer • 🎯 Challenge friends");
        playersLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        playersLabel.setForeground(new Color(150, 150, 150));
        playersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(playersLabel);

        // Play button
        AnimatedButton playBtn = new AnimatedButton("Play", FacebookGUI.FB_GREEN, FacebookGUI.FB_GREEN_HOVER);
        playBtn.setPreferredSize(new Dimension(100, 40));
        playBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        playBtn.addActionListener(e -> launchGame(game));

        card.add(iconLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(playBtn, BorderLayout.EAST);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            private Color originalColor = card.getBackground();

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 245, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(originalColor);
            }
        });

        return card;
    }

    private String getGameIcon(String gameName) {
        String name = gameName.toUpperCase();
        if (name.contains("TIC"))
            return "⭕";
        if (name.contains("HANG"))
            return "💭";
        if (name.contains("SNAKE"))
            return "🐍";
        return "🎮";
    }

    private String getGameDescription(String gameName) {
        String name = gameName.toUpperCase();
        if (name.contains("TIC"))
            return "Classic X and O game - First to 3 in a row wins!";
        if (name.contains("HANG"))
            return "Guess the word before hangman is complete!";
        if (name.contains("SNAKE"))
            return "Control the snake and eat food to grow!";
        return "Have fun playing!";
    }

    private void launchGame(Game game) {
        dispose();

        // Launch based on game name
        SwingUtilities.invokeLater(() -> {
            try {
                String gameName = game.getName().toUpperCase();

                JOptionPane.showMessageDialog(parent,
                        "Launching " + game.getName() + "...",
                        "Game Launch",
                        JOptionPane.INFORMATION_MESSAGE);

                // Launch the game in a separate thread
                new Thread(() -> {
                    try {
                        if (gameName.contains("TIC")) {
                            TicTacToe ticTacToe = new TicTacToe();
                            ticTacToe.Game_launch();
                        } else if (gameName.contains("HANG")) {
                            Hangman hangman = new Hangman();
                            hangman.Game_launch();
                        } else if (gameName.contains("SNAKE")) {
                            SnakeGame snake = new SnakeGame();
                            snake.Game_launch();
                        } else {
                            // Fallback try simple launch if allows
                            // But we only know these 3 for now.
                        }
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parent,
                                "Error launching game: " + e.getMessage(),
                                "Game Error",
                                JOptionPane.ERROR_MESSAGE));
                    }
                }).start();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to launch game: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
