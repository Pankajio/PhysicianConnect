package physicianconnect.presentation.config;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import javafx.scene.Cursor;

public final class UITheme {
    // ─────────── Color Palette ───────────
    public static final Color PRIMARY_COLOR = new Color(0x1976D2);
    public static final Color ACCENT_LIGHT_COLOR = new Color(0x63AFF3);
    public static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    public static final Color ERROR_COLOR = new Color(0xD32F2F);
    public static final Color SUCCESS_COLOR = new Color(0x388E3C);
    public static final Color TEXT_COLOR = new Color(0x212121);
    public static final Color POSITIVE_COLOR = new Color(0x4CAF50);
    public static final Color DESTRUCTIVE_COLOR = new Color(0xF44336);
    public static final Color SELECTION_COLOR = new Color(0x1E2937);
    public static final Color SUCCESS_BUTTON_COLOR = new Color(0x388E3C);
    public static final Color ERROR_BUTTON_COLOR = new Color(0xD32F2F);
    public static final Color CANCEL_BUTTON_COLOR = new Color(0x787C82);

    // ─────────── Fonts ───────────
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font TEXTFIELD_FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font MESSAGE_BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 24);
    public static final Font NOTIFICATION_FONT = new Font("Segoe UI", Font.BOLD, 10);

    // ─────────── Hover Effect for Buttons ───────────
    public static void applyHoverEffect(final JButton btn) {
        final Color originalBg = btn.getBackground();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ACCENT_LIGHT_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(originalBg);
            }
        });
    }


    // Prevent instantiation
    private UITheme() { }
}
