package physicianconnect.persistence.interfaces;

import physicianconnect.objects.Physician;

import java.util.List;

public interface PhysicianPersistence {
    void addPhysician(Physician physician);

    void deletePhysicianById(String id);

    void deleteAllPhysicians();

    List<Physician> getAllPhysicians();

    Physician getPhysicianById(String id);

    void updatePhysician(Physician physician);
}
