package com.facebook.gui.components;

import com.facebook.gui.FacebookGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class MessageDialog extends JDialog {
    public enum Type {
        INFO, ERROR, SUCCESS, WARNING
    }

    public MessageDialog(Window parent, String title, String message, Type type) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparent for rounded corners

        RoundedPanel contentPanel = new RoundedPanel(12);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel iconLabel = new JLabel(getIcon(type));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(FacebookGUI.FB_TEXT_PRIMARY);

        header.add(iconLabel, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);

        // Body
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        msgArea.setForeground(FacebookGUI.FB_TEXT_SECONDARY);
        msgArea.setWrapStyleWord(true);
        msgArea.setLineWrap(true);
        msgArea.setEditable(false);
        msgArea.setOpaque(false);
        msgArea.setColumns(25); // Set preferred width
        msgArea.setBorder(new EmptyBorder(15, 0, 25, 0));

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);

        Color btnColor = type == Type.ERROR ? FacebookGUI.FB_ERROR : FacebookGUI.FB_BLUE;
        Color btnHover = type == Type.ERROR ? new Color(200, 48, 63) : FacebookGUI.FB_BLUE_HOVER;

        AnimatedButton okBtn = new AnimatedButton("OK", btnColor, btnHover);
        okBtn.setPreferredSize(new Dimension(80, 36));
        okBtn.addActionListener(e -> dispose());
        footer.add(okBtn);

        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(msgArea, BorderLayout.CENTER);
        contentPanel.add(footer, BorderLayout.SOUTH);

        add(contentPanel);
        pack();
        setLocationRelativeTo(parent);

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

    private String getIcon(Type type) {
        return switch (type) {
            case INFO -> "ℹ️";
            case ERROR -> "❌";
            case SUCCESS -> "✅";
            case WARNING -> "⚠️";
        };
    }

    public static void show(Component parent, String title, String message, Type type) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        new MessageDialog(window, title, message, type).setVisible(true);
    }
}
