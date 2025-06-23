package physicianconnect.presentation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import physicianconnect.presentation.config.UITheme;

public class NotificationButton extends JPanel {
    private final JButton notificationButton;
    private final JLabel notificationLabel;
    private static final int BUTTON_HEIGHT = 35; // Fixed height for both button and counter

    public NotificationButton() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);

        // Create notification button
        notificationButton = new JButton("Alerts");
        notificationButton.setFont(UITheme.BUTTON_FONT);
        notificationButton.setBackground(UITheme.PRIMARY_COLOR);
        notificationButton.setForeground(Color.WHITE);
        notificationButton.setFocusPainted(false);
        notificationButton.setBorderPainted(false);
        notificationButton.setOpaque(true);
        notificationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationButton.setPreferredSize(new Dimension(2*BUTTON_HEIGHT, BUTTON_HEIGHT));
        notificationButton.setHorizontalAlignment(JButton.CENTER);
        notificationButton.setVerticalAlignment(JButton.CENTER);
        notificationButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create notification label
        notificationLabel = new JLabel();
        notificationLabel.setFont(UITheme.NOTIFICATION_FONT);
        notificationLabel.setForeground(UITheme.BACKGROUND_COLOR);
        notificationLabel.setBackground(UITheme.ERROR_COLOR);
        notificationLabel.setOpaque(true);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        notificationLabel.setVisible(false);
        notificationLabel.setPreferredSize(new Dimension(20, BUTTON_HEIGHT));
        notificationLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add hover effect to button only
        notificationButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                notificationButton.setBackground(UITheme.ACCENT_LIGHT_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                notificationButton.setBackground(UITheme.PRIMARY_COLOR);
            }
        });

        add(notificationButton);
        add(notificationLabel);
    }

    public void setOnAction(ActionListener listener) {
        notificationButton.addActionListener(listener);
    }

    public void updateNotificationCount(int count) {
        if (count > 0) {
            notificationLabel.setText(String.valueOf(count));
            notificationLabel.setVisible(true);
            // Ensure the label stays visible and is properly sized
            notificationLabel.setPreferredSize(new Dimension(
                Math.max(20, notificationLabel.getPreferredSize().width),
                BUTTON_HEIGHT
            ));
            notificationLabel.revalidate();
            notificationLabel.repaint();
            // Ensure the panel is also updated
            revalidate();
            repaint();
        } else {
            notificationLabel.setVisible(false);
            notificationLabel.setText("0");
            // Ensure the panel is updated when hiding the counter
            revalidate();
            repaint();
        }
    }
} 