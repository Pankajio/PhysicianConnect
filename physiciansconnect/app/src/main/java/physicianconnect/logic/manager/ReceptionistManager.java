package physicianconnect.logic.manager;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class ReceptionistManager {
    private final ReceptionistPersistence receptionistDB;

    public ReceptionistManager(ReceptionistPersistence receptionistDB) {
        this.receptionistDB = receptionistDB;
    }

    public void addReceptionist(Receptionist receptionist) {
        if (receptionist == null)
            throw new IllegalArgumentException("Receptionist cannot be null.");
        if (receptionist.getId() == null || receptionist.getId().isBlank())
            throw new IllegalArgumentException("Receptionist ID cannot be null or blank.");
        if (receptionistDB.getReceptionistById(receptionist.getId()) == null) {
            receptionistDB.addReceptionist(receptionist);
        }
    }

    public List<Receptionist> getAllReceptionists() {
        return Collections.unmodifiableList(receptionistDB.getAllReceptionists());
    }

    public Receptionist getReceptionistById(String id) {
        return receptionistDB.getReceptionistById(id);
    }

    public Receptionist getReceptionistByEmail(String email) {
        return receptionistDB.getAllReceptionists().stream()
                .filter(r -> r.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public Receptionist login(String email, String password) {
        Receptionist receptionist = getReceptionistByEmail(email);
        if (receptionist != null && receptionist.getPassword().equals(password)) {
            return receptionist;
        }
        return null;
    }

    public void validateAndUpdateReceptionist(Receptionist receptionist, String name, boolean notifyAppt,
            boolean notifyBill, boolean notifyMsg) {
        if (receptionist == null)
            throw new IllegalArgumentException("Receptionist cannot be null.");

        receptionist.setName(name);
        receptionist.setNotifyAppointment(notifyAppt);
        receptionist.setNotifyBilling(notifyBill);
        receptionist.setNotifyMessages(notifyMsg);

        validateBasicInfo(receptionist);
        updateReceptionist(receptionist);
    }

    private void updateReceptionist(Receptionist receptionist) {
        if (receptionist == null || receptionist.getId() == null)
            throw new IllegalArgumentException("Receptionist or ID cannot be null.");

        receptionistDB.updateReceptionist(receptionist);
    }

    public void validateBasicInfo(Receptionist r) {
        if (r.getName() == null || r.getName().isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");

        if (r.getEmail() == null || r.getEmail().isBlank() || !r.getEmail().matches("^\\S+@\\S+\\.\\S+$"))
            throw new IllegalArgumentException("Invalid email format.");
    }

    public void uploadProfilePhoto(String receptionistId, InputStream photoStream) {
        try {
            BufferedImage original = ImageIO.read(photoStream);
            if (original == null)
                throw new IOException("Invalid image format.");

            int width = original.getWidth();
            int height = original.getHeight();
            float scale = Math.min(200f / width, 200f / height);
            int newW = Math.round(width * scale);
            int newH = Math.round(height * scale);

            BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(original, 0, 0, newW, newH, null);
            g2d.dispose();

            Path outputDir = Paths.get("src/main/resources/profile_photos").toAbsolutePath();
            Files.createDirectories(outputDir);
            File outputFile = outputDir.resolve("r_" + receptionistId + ".png").toFile();
            ImageIO.write(resized, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile photo", e);
        }
    }

}