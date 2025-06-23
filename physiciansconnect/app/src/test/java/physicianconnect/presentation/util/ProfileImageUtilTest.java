package physicianconnect.presentation.util;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ProfileImageUtilTest {

    @Test
    void testGetProfileIcon_PhotoFileExists() throws Exception {
        // Arrange: create a dummy image file
        String id = "test";
        boolean isPhysician = true;
        String prefix = isPhysician ? "p_" : "r_";
        File dir = new File("src/main/resources/profile_photos");
        dir.mkdirs();
        File photoFile = new File(dir, prefix + id + ".png");

        // Write a small PNG file
        BufferedImage img = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.BLUE);
        g2.fillRect(0, 0, 40, 40);
        g2.dispose();
        javax.imageio.ImageIO.write(img, "png", photoFile);

        // Act
        ImageIcon icon = ProfileImageUtil.getProfileIcon(id, isPhysician);

        // Assert
        assertNotNull(icon);
        assertEquals(40, icon.getIconWidth());
        assertEquals(40, icon.getIconHeight());

        // Cleanup
        photoFile.delete();
    }
}