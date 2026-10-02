package com.pharmacy.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for managing medicine data.
 * 
 * This is a simple in-memory implementation that stores medicine information.
 * In a production environment, this would be replaced with a proper database
 * implementation.
 */
public class MedicineRepository {
    
    private static MedicineRepository instance;
    private final Map<String, String> medicines; // medicineId -> medicineName

    private MedicineRepository() {
        this.medicines = new ConcurrentHashMap<>();
        initializeSampleData();
    }

    /**
     * Gets the singleton instance of the repository.
     * 
     * @return MedicineRepository instance
     */
    public static synchronized MedicineRepository getInstance() {
        if (instance == null) {
            instance = new MedicineRepository();
        }
        return instance;
    }

    /**
     * Initializes the repository with sample medicine data.
     */
    private void initializeSampleData() {
        medicines.put("M001", "Amoxicillin");
        medicines.put("M002", "Ibuprofen");
        medicines.put("M003", "Lisinopril");
        medicines.put("M004", "Metformin");
        medicines.put("M005", "Atorvastatin");
        medicines.put("M006", "Amlodipine");
        medicines.put("M007", "Omeprazole");
        medicines.put("M008", "Losartan");
        medicines.put("M009", "Gabapentin");
        medicines.put("M010", "Hydrochlorothiazide");
    }

    /**
     * Checks if a medicine exists with the given ID.
     * 
     * @param medicineId Medicine ID to check
     * @return true if medicine exists, false otherwise
     */
    public boolean existsById(String medicineId) {
        return medicines.containsKey(medicineId);
    }

    /**
     * Gets the name of a medicine by its ID.
     * 
     * @param medicineId Medicine ID
     * @return Medicine name, or null if not found
     */
    public String getMedicineName(String medicineId) {
        return medicines.get(medicineId);
    }

    /**
     * Adds a new medicine to the repository.
     * 
     * @param medicineId Medicine ID
     * @param medicineName Medicine name
     */
    public void addMedicine(String medicineId, String medicineName) {
        medicines.put(medicineId, medicineName);
    }

    /**
     * Gets all medicines in the repository.
     * 
     * @return Map of medicine IDs to names
     */
    public Map<String, String> getAllMedicines() {
        return new HashMap<>(medicines);
    }

    /**
     * Removes a medicine from the repository.
     * 
     * @param medicineId Medicine ID to remove
     * @return true if medicine was removed, false if not found
     */
    public boolean removeMedicine(String medicineId) {
        return medicines.remove(medicineId) != null;
    }

    /**
     * Clears all medicines from the repository.
     * Useful for testing purposes.
     */
    public void clear() {
        medicines.clear();
    }

    /**
     * Gets the count of medicines in the repository.
     * 
     * @return Number of medicines
     */
    public long count() {
        return medicines.size();
    }
}

// Made with Bob
