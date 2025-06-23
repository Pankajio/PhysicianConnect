package physicianconnect.presentation.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ProfileImageUtil {

    public static ImageIcon getProfileIcon(String id, boolean isPhysician) {
        String prefix = isPhysician ? "p_" : "r_";
        String fileName = prefix + id + ".png";
        String resourcePath = "/profile_photos/" + fileName;

        try (InputStream is = ProfileImageUtil.class.getResourceAsStream(resourcePath)) {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                Image scaledImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            e.printStackTrace(); // You can also log this properly
        }

        // Fallback placeholder
        BufferedImage placeholder = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, 40, 40);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("👤", 10, 25); // You can customize this
        g2.dispose();
        return new ImageIcon(placeholder);
    }
}
