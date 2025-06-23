package physicianconnect.presentation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import physicianconnect.presentation.config.UITheme;

public class MessageButton extends JPanel {
    private final JButton messageButton;
    private final JLabel notificationLabel;
    private static final int BUTTON_HEIGHT = 35; // Fixed height for both button and counter

    public MessageButton() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);

        // Create message button
        messageButton = new JButton("Messages");
        messageButton.setFont(UITheme.BUTTON_FONT);
        messageButton.setBackground(UITheme.PRIMARY_COLOR);
        messageButton.setForeground(Color.WHITE);
        messageButton.setFocusPainted(false);
        messageButton.setBorderPainted(false);
        messageButton.setOpaque(true);
        messageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        messageButton.setPreferredSize(new Dimension(3*BUTTON_HEIGHT, BUTTON_HEIGHT));
        messageButton.setHorizontalAlignment(JButton.CENTER);
        messageButton.setVerticalAlignment(JButton.CENTER);
        messageButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Create notification label
        notificationLabel = new JLabel();
        notificationLabel.setFont(UITheme.NOTIFICATION_FONT);
        notificationLabel.setForeground(UITheme.BACKGROUND_COLOR);
        notificationLabel.setBackground(UITheme.ERROR_COLOR);
        notificationLabel.setOpaque(true);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        notificationLabel.setVisible(false);
        notificationLabel.setPreferredSize(new Dimension(20, BUTTON_HEIGHT));
        notificationLabel.setHorizontalAlignment(JLabel.CENTER);
        notificationLabel.setVerticalAlignment(JLabel.CENTER);

        add(messageButton);
        add(notificationLabel);

        // Add hover effect
        UITheme.applyHoverEffect(messageButton);
    }

    public void setOnAction(ActionListener listener) {
        messageButton.addActionListener(listener);
    }

    public void updateNotificationCount(int count) {
        if (count > 0) {
            notificationLabel.setText(String.valueOf(count));
            notificationLabel.setVisible(true);
        } else {
            notificationLabel.setVisible(false);
        }
    }
}
