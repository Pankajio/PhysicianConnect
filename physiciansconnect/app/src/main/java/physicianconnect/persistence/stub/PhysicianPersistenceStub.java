package physicianconnect.persistence.stub;

import physicianconnect.objects.Physician;
import physicianconnect.persistence.interfaces.PhysicianPersistence;

import java.util.*;

public class PhysicianPersistenceStub implements PhysicianPersistence {
    private Map<String, Physician> physicians;

    public PhysicianPersistenceStub(boolean seed) {
        physicians = new HashMap<>();
        if (seed) {
            addPhysician(new Physician("1", "Dr. Smith", "smith@hospital.com", "test123",
                    "Cardiology", "Mon-Fri 9am-5pm", true, false, true,
                    "(204) 123-4567", "123 Heart St."));

            addPhysician(new Physician("2", "Dr. Lee", "lee@clinic.org", "test123",
                    "Pediatrics", "Tue-Thu 10am-4pm", true, true, false,
                    "(204) 987-6543", "456 Child Ave."));
        }
    }

    @Override
    public void addPhysician(Physician physician) {
        if (physician == null || physician.getId() == null || physician.getId().isBlank()) {
            throw new IllegalArgumentException("Physician ID cannot be null or blank.");
        }

        String id = (physician.getId() == null || physician.getId().isBlank())
                ? UUID.randomUUID().toString()
                : physician.getId();

        if (!physicians.containsKey(id)) {
            physicians.put(id, new Physician(id, physician.getName(), physician.getEmail(), physician.getPassword()));
        }
    }

    @Override
    public List<Physician> getAllPhysicians() {
        return new ArrayList<>(physicians.values());
    }

    public Physician getPhysicianById(String id) {
        return physicians.get(id);
    }

    public void deletePhysicianById(String id) {
        physicians.remove(id);
    }

    public void deleteAllPhysicians() {
        physicians.clear();
    }

    @Override
    public void updatePhysician(Physician physician) {
        if (physician == null || physician.getId() == null || !physicians.containsKey(physician.getId())) {
            throw new IllegalArgumentException("Physician not found.");
        }
        physicians.put(physician.getId(), physician);
    }

    public void close() {
        physicians = null;
    }
}
