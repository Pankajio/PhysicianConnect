package physicianconnect.persistence.stub;

import physicianconnect.objects.Receptionist;
import physicianconnect.persistence.interfaces.ReceptionistPersistence;

import java.util.*;

public class ReceptionistPersistenceStub implements ReceptionistPersistence {
    private Map<String, Receptionist> receptionists;

    public ReceptionistPersistenceStub() {
        this(true);
    }

    public ReceptionistPersistenceStub(boolean seed) {
        receptionists = new HashMap<>();
        if (seed) {
            addReceptionist(new Receptionist("r1", "Receptionist One", "reception1@example.com", "password"));
            addReceptionist(new Receptionist("r2", "Receptionist Two", "reception2@example.com", "password"));
        }
    }

    @Override
    public Receptionist getReceptionistById(String id) {
        return receptionists.get(id);
    }

    @Override
    public Receptionist getReceptionistByEmail(String email) {
        for (Receptionist r : receptionists.values()) {
            if (r.getEmail().equalsIgnoreCase(email)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public void addReceptionist(Receptionist receptionist) {
        if (receptionist == null || receptionist.getId() == null || receptionist.getId().isBlank()) {
            throw new IllegalArgumentException("Receptionist ID cannot be null or blank.");
        }
        String id = receptionist.getId();
        if (!receptionists.containsKey(id)) {
            receptionists.put(id, receptionist);
        }
    }

    @Override
    public List<Receptionist> getAllReceptionists() {
        return new ArrayList<>(receptionists.values());
    }

    public void deleteAllReceptionists() {
        receptionists.clear();
    }

    @Override
    public void updateReceptionist(Receptionist receptionist) {
        if (receptionist == null || receptionist.getId() == null || !receptionists.containsKey(receptionist.getId())) {
            throw new IllegalArgumentException("Receptionist not found.");
        }
        receptionists.put(receptionist.getId(), receptionist);
    }

    @Override
    public void deleteReceptionist(String id) {
        receptionists.remove(id);
    }

    @Override
    public List<String> getAllReceptionistIds() {
        return new ArrayList<>(receptionists.keySet());
    }

    public void close() {
        receptionists = null;
    }
}