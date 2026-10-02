package com.pharmacy.repository;

import com.pharmacy.model.Prescription;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for managing prescription data persistence.
 * 
 * This is a simple in-memory implementation using a ConcurrentHashMap
 * for thread-safe operations. In a production environment, this would
 * be replaced with a proper database implementation.
 */
public class PrescriptionRepository {
    
    private static PrescriptionRepository instance;
    private final Map<String, Prescription> prescriptions;

    private PrescriptionRepository() {
        this.prescriptions = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of the repository.
     * 
     * @return PrescriptionRepository instance
     */
    public static synchronized PrescriptionRepository getInstance() {
        if (instance == null) {
            instance = new PrescriptionRepository();
        }
        return instance;
    }

    /**
     * Finds all prescriptions in the system.
     * 
     * @return List of all prescriptions
     */
    public List<Prescription> findAll() {
        return new ArrayList<>(prescriptions.values());
    }

    /**
     * Finds a prescription by its unique identifier.
     * 
     * @param id Prescription ID
     * @return Optional containing the prescription if found, empty otherwise
     */
    public Optional<Prescription> findById(String id) {
        return Optional.ofNullable(prescriptions.get(id));
    }

    /**
     * Saves a prescription to the repository.
     * If the prescription already exists (same ID), it will be updated.
     * 
     * @param prescription Prescription to save
     * @return Saved prescription
     */
    public Prescription save(Prescription prescription) {
        prescriptions.put(prescription.getId(), prescription);
        return prescription;
    }

    /**
     * Deletes a prescription by its ID.
     * 
     * @param id Prescription ID to delete
     * @return true if prescription was deleted, false if not found
     */
    public boolean deleteById(String id) {
        return prescriptions.remove(id) != null;
    }

    /**
     * Checks if a prescription exists with the given ID.
     * 
     * @param id Prescription ID
     * @return true if prescription exists, false otherwise
     */
    public boolean existsById(String id) {
        return prescriptions.containsKey(id);
    }

    /**
     * Clears all prescriptions from the repository.
     * Useful for testing purposes.
     */
    public void clear() {
        prescriptions.clear();
    }

    /**
     * Gets the count of prescriptions in the repository.
     * 
     * @return Number of prescriptions
     */
    public long count() {
        return prescriptions.size();
    }
}

// Made with Bob
