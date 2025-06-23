package physicianconnect.logic.manager;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.interfaces.PhysicianPersistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PhysicianManager {

    private final PhysicianPersistence physicianDB;

    public PhysicianManager(PhysicianPersistence physicianDB) {
        this.physicianDB = physicianDB;
    }

    public void addPhysician(Physician physician) {
        if (physician == null)
            throw new IllegalArgumentException("Physician cannot be null.");
        if (physician.getId() == null || physician.getId().isBlank())
            throw new IllegalArgumentException("Physician ID cannot be null or blank.");
        if (physicianDB.getPhysicianById(physician.getId()) == null) {
            physicianDB.addPhysician(physician);
        }
    }

    public void removePhysician(String id) {
        physicianDB.deletePhysicianById(id);
    }

    public List<Physician> getAllPhysicians() {
        return Collections.unmodifiableList(physicianDB.getAllPhysicians());
    }

    public Physician getPhysicianById(String id) {
        return physicianDB.getPhysicianById(id);
    }

    public void deleteAll() {
        physicianDB.deleteAllPhysicians();
    }

    public Physician getPhysicianByEmail(String email) {
        return physicianDB.getAllPhysicians().stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public Physician login(String email, String password) {
        Physician physician = getPhysicianByEmail(email);
        if (physician != null && physician.getPassword().equals(password)) {
            return physician;
        }
        return null;
    }

    public void validateAndUpdatePhysician(
            Physician physician,
            String name,
            String specialty,
            String officeHours,
            String phone,
            String address,
            boolean notifyAppointments,
            boolean notifyBilling,
            boolean notifyMessages) {
        if (physician == null)
            throw new IllegalArgumentException("Physician cannot be null.");

        physician.setName(name);
        physician.setSpecialty(specialty);
        physician.setOfficeHours(officeHours);
        physician.setPhone(phone);
        physician.setOfficeAddress(address);
        physician.setNotifyAppointment(notifyAppointments);
        physician.setNotifyBilling(notifyBilling);
        physician.setNotifyMessages(notifyMessages);

        validateBasicInfo(physician);
        updatePhysician(physician);
    }

    private void updatePhysician(Physician physician) {
        if (physician == null || physician.getId() == null)
            throw new IllegalArgumentException("Physician or ID cannot be null.");

        physicianDB.updatePhysician(physician);
    }

    public void validateBasicInfo(Physician p) {
        if (p.getName() == null || p.getName().isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");

        if (p.getPhone() != null && !p.getPhone().isBlank() && !p.getPhone().matches("\\(\\d{3}\\) \\d{3}-\\d{4}"))
            throw new IllegalArgumentException("Phone must match (204) 123-4567 format.");

    }

    public void uploadProfilePhoto(String physicianId, InputStream photoStream) {
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
            File outputFile = outputDir.resolve("p_" + physicianId + ".png").toFile();
            ImageIO.write(resized, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile photo", e);
        }
    }

}
