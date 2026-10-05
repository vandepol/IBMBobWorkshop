package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MedicineRepository {
    private static MedicineRepository instance;
    private final Map<String, Medicine> medicines;
    
    private MedicineRepository() {
        medicines = new ConcurrentHashMap<>();
        initializeSampleData();
    }
    
    public static synchronized MedicineRepository getInstance() {
        if (instance == null) {
            instance = new MedicineRepository();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        addMedicine(new Medicine("MED001", "Amoxicillin 500mg", 
            "Antibiotic for bacterial infections", new BigDecimal("15.99"), 100, "PharmaCorp"));
        addMedicine(new Medicine("MED002", "Lisinopril 10mg", 
            "Blood pressure medication", new BigDecimal("12.50"), 150, "HealthMeds"));
        addMedicine(new Medicine("MED003", "Metformin 850mg", 
            "Diabetes medication", new BigDecimal("18.75"), 200, "DiabetesCare"));
        addMedicine(new Medicine("MED004", "Atorvastatin 20mg", 
            "Cholesterol medication", new BigDecimal("22.00"), 80, "CardioHealth"));
        addMedicine(new Medicine("MED005", "Omeprazole 20mg", 
            "Acid reflux medication", new BigDecimal("14.25"), 120, "GastroMeds"));
        addMedicine(new Medicine("MED006", "Levothyroxine 50mcg", 
            "Thyroid medication", new BigDecimal("16.50"), 90, "EndoPharm"));
        addMedicine(new Medicine("MED007", "Albuterol Inhaler", 
            "Asthma relief", new BigDecimal("45.00"), 60, "RespiraCare"));
        addMedicine(new Medicine("MED008", "Ibuprofen 200mg", 
            "Pain reliever and anti-inflammatory", new BigDecimal("8.99"), 250, "PainRelief Inc"));
    }
    
    public void addMedicine(Medicine medicine) {
        medicines.put(medicine.getId(), medicine);
    }
    
    public Medicine findById(String id) {
        return medicines.get(id);
    }
    
    public List<Medicine> findAll() {
        return new ArrayList<>(medicines.values());
    }
    
    public List<Medicine> searchByName(String name) {
        return medicines.values().stream()
            .filter(m -> m.getName().toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public void updateMedicine(Medicine medicine) {
        medicines.put(medicine.getId(), medicine);
    }
    
    public void deleteMedicine(String id) {
        medicines.remove(id);
    }
    
    public boolean updateStock(String medicineId, int quantity) {
        Medicine medicine = medicines.get(medicineId);
        if (medicine != null && medicine.getStockQuantity() >= quantity) {
            medicine.setStockQuantity(medicine.getStockQuantity() - quantity);
            return true;
        }
        return false;
    }
}

// Made with Bob
