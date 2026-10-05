package com.pharmacy.repository;

import com.pharmacy.model.Prescription;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PrescriptionRepository {
    private static PrescriptionRepository instance;
    private final Map<String, Prescription> prescriptions;
    private final AtomicInteger idCounter;
    
    private PrescriptionRepository() {
        prescriptions = new ConcurrentHashMap<>();
        idCounter = new AtomicInteger(1000);
        initializeSampleData();
    }
    
    public static synchronized PrescriptionRepository getInstance() {
        if (instance == null) {
            instance = new PrescriptionRepository();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        Calendar cal = Calendar.getInstance();
        
        // Prescription 1
        cal.setTime(new Date());
        Date prescDate1 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date expiryDate1 = cal.getTime();
        
        addPrescription(new Prescription("RX001", "John Smith", "P12345", 
            "Dr. Sarah Johnson", "MED001", "Amoxicillin 500mg", 30, 
            "1 tablet twice daily", prescDate1, expiryDate1, "PENDING", 
            "Take with food"));
        
        // Prescription 2
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -2);
        Date prescDate2 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 32);
        Date expiryDate2 = cal.getTime();
        
        addPrescription(new Prescription("RX002", "Mary Johnson", "P12346", 
            "Dr. Michael Chen", "MED003", "Metformin 850mg", 60, 
            "1 tablet twice daily with meals", prescDate2, expiryDate2, "VALIDATED", 
            "Monitor blood sugar levels"));
        
        // Prescription 3
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date prescDate3 = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 35);
        Date expiryDate3 = cal.getTime();
        
        addPrescription(new Prescription("RX003", "Robert Davis", "P12347", 
            "Dr. Emily White", "MED007", "Albuterol Inhaler", 2, 
            "2 puffs as needed for breathing difficulty", prescDate3, expiryDate3, "FULFILLED", 
            "Use spacer device"));
    }
    
    public String generateId() {
        return "RX" + String.format("%03d", idCounter.incrementAndGet());
    }
    
    public void addPrescription(Prescription prescription) {
        prescriptions.put(prescription.getId(), prescription);
    }
    
    public Prescription findById(String id) {
        return prescriptions.get(id);
    }
    
    public List<Prescription> findAll() {
        return new ArrayList<>(prescriptions.values());
    }
    
    public List<Prescription> findByPatientId(String patientId) {
        return prescriptions.values().stream()
            .filter(p -> p.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }
    
    public List<Prescription> findByStatus(String status) {
        return prescriptions.values().stream()
            .filter(p -> p.getStatus().equals(status))
            .collect(Collectors.toList());
    }
    
    public void updatePrescription(Prescription prescription) {
        prescriptions.put(prescription.getId(), prescription);
    }
    
    public void deletePrescription(String id) {
        prescriptions.remove(id);
    }
}

// Made with Bob
