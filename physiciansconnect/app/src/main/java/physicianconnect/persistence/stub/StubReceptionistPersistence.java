package physicianconnect.persistence.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

public class StubReceptionistPersistence implements ReceptionistPersistence {
    private final List<Receptionist> receptionists;

    public StubReceptionistPersistence() {
        receptionists = new ArrayList<>();
        // Add a test receptionist
        receptionists.add(new Receptionist("0", "Test Receptionist", "test@email.com", "password"));
    }

    @Override
    public void addReceptionist(Receptionist receptionist) {
        receptionists.add(receptionist);
    }

    @Override
    public Receptionist getReceptionistById(String id) {
        return receptionists.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Receptionist getReceptionistByEmail(String email) {
        return receptionists.stream()
                .filter(r -> r.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Receptionist> getAllReceptionists() {
        return new ArrayList<>(receptionists);
    }

    @Override
    public List<String> getAllReceptionistIds() {
        return receptionists.stream()
                .map(Receptionist::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void updateReceptionist(Receptionist receptionist) {
        for (int i = 0; i < receptionists.size(); i++) {
            if (receptionists.get(i).getId().equals(receptionist.getId())) {
                receptionists.set(i, receptionist);
                break;
            }
        }
    }

    @Override
    public void deleteReceptionist(String id) {
        receptionists.removeIf(r -> r.getId().equals(id));
    }
} 