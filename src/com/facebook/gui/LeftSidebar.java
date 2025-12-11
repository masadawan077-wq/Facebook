package com.facebook.gui;

import com.facebook.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Left Sidebar with menu options
 * Features: Profile, Meta AI, Friends, Memories, Saved, Groups, Reels,
 * Marketplace, Feeds, Events
 */
public class LeftSidebar extends JPanel {

    private HomePage homePage;

    public LeftSidebar(FacebookGUI parent, HomePage homePage) {
        this.homePage = homePage;
        setBackground(FacebookGUI.FB_BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 8, 15, 8));
        initComponents();
    }

    private void initComponents() {
        // User profile
        add(createMenuItem(
                createProfileIcon(Main.current.getFirstname()),
                Main.current.getFirstname() + " " + Main.current.getLastname(),
                e -> {
                    // TODO: Navigate to profile
                }));

        // Friends
        add(createMenuItem("👥", "Friends", e -> homePage.showFriendsPanel()));

        // Messages (New)
        add(createMenuItem("💬", "Messages", e -> homePage.showMessages()));

        // Groups
        add(createMenuItem("👨‍👩‍👧‍👦", "Groups", e -> {
        }));

        // Feeds
        add(createMenuItem("📰", "Feeds", e -> homePage.showFeedPanel()));

        // Saved
        add(createMenuItem("🔖", "Saved", e -> {
        }));

        // Games (Replaces 8 ball pool and others)
        add(createMenuItem("🎮", "Games", e -> homePage.openGamesDialog()));

        add(Box.createVerticalGlue());
    }

    private JPanel createMenuItem(String icon, String text, ActionListener action) {
        return createMenuItem(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                g2.setColor(FacebookGUI.FB_TEXT_PRIMARY);
                g2.drawString(icon, x, y + 18);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 36;
            }

            @Override
            public int getIconHeight() {
                return 36;
            }
        }, text, action);
    }

    private JPanel createMenuItem(Icon icon, String text, ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52)); // Taller items
        panel.setOpaque(true);
        panel.setBackground(FacebookGUI.FB_BACKGROUND);
        panel.setBorder(new EmptyBorder(8, 12, 8, 12)); // More padding
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Bolder text
        textLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(228, 230, 235)); // Darker hover
                // Add rounded corners visually if possible, but panel is square.
                // For simplicity in Swing, we keep background.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(FacebookGUI.FB_BACKGROUND);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.actionPerformed(new java.awt.event.ActionEvent(panel, 0, ""));
                }
            }
        });

        return panel;
    }

    private Icon createProfileIcon(String name) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                g2.setColor(FacebookGUI.FB_BLUE);
                g2.fillOval(x, y, 36, 36);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initial = name.substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (36 - fm.stringWidth(initial)) / 2;
                int textY = y + (36 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initial, textX, textY);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 36;
            }

            @Override
            public int getIconHeight() {
                return 36;
            }
        };
    }
}
