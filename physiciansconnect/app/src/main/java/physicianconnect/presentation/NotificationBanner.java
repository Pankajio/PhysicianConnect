package physicianconnect.presentation;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;

import physicianconnect.presentation.config.UITheme;

public class NotificationBanner extends JWindow {
    private static final int DISPLAY_TIME = 4000; // 4 seconds
    private static final int FADE_IN_TIME = 300; // 300ms fade in
    private static final int FADE_OUT_TIME = 300; // 300ms fade out
    private final Timer dismissTimer;
    private final Timer fadeInTimer;
    private final Timer fadeOutTimer;
    private final JLabel messageLabel;
    private ActionListener clickListener;
    private float opacity = 0.0f;

    public NotificationBanner(Window owner) {
        super(owner);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        setAlwaysOnTop(true);

        // Add a border and shadow
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
            new EmptyBorder(12, 20, 12, 20)
        ));
        setContentPane(contentPanel);

        // Message label
        messageLabel = new JLabel();
        messageLabel.setFont(UITheme.LABEL_FONT);
        messageLabel.setForeground(Color.BLACK);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Dismiss timer
        dismissTimer = new Timer(DISPLAY_TIME, e -> startFadeOut());
        dismissTimer.setRepeats(false);

        // Fade in timer
        fadeInTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.1f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    fadeInTimer.stop();
                }
                repaint();
            }
        });

        // Fade out timer
        fadeOutTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.1f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    fadeOutTimer.stop();
                    dismiss();
                }
                repaint();
            }
        });

        // Make banner clickable
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (clickListener != null) {
                    clickListener.actionPerformed(null);
                }
                startFadeOut();
            }
        });

        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                contentPanel.setBackground(new Color(245, 245, 245)); // Light gray on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                contentPanel.setBackground(Color.WHITE);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        super.paint(g2d);
        g2d.dispose();
    }

    public void show(String message, ActionListener onClick) {
        messageLabel.setText(message);
        this.clickListener = onClick;
        
        // Calculate position (top center of the screen)
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        
        pack();
        Dimension bannerSize = getSize();
        
        int x = rect.x + (rect.width - bannerSize.width) / 2;
        int y = rect.y + 50; // 50 pixels from top of screen
        
        setLocation(x, y);
        
        // Start fade in
        opacity = 0.0f;
        setVisible(true);
        fadeInTimer.start();
        dismissTimer.restart();
    }

    private void startFadeOut() {
        dismissTimer.stop();
        fadeOutTimer.start();
    }

    public void dismiss() {
        setVisible(false);
        dismissTimer.stop();
        fadeInTimer.stop();
        fadeOutTimer.stop();
    }
} 