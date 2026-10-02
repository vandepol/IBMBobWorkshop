package com.pharmacy.action;

import com.opensymphony.xwork2.ActionSupport;
import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import java.util.List;

public class MedicineAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    
    private String medicineId;
    private Medicine medicine;
    private List<Medicine> medicines;
    private String searchQuery;
    
    private MedicineRepository medicineRepo = MedicineRepository.getInstance();
    
    public String list() {
        medicines = medicineRepo.findAll();
        return SUCCESS;
    }
    
    public String view() {
        if (medicineId != null) {
            medicine = medicineRepo.findById(medicineId);
            if (medicine == null) {
                addActionError("Medicine not found");
                return ERROR;
            }
        }
        return SUCCESS;
    }
    
    public String search() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            medicines = medicineRepo.searchByName(searchQuery);
        } else {
            medicines = medicineRepo.findAll();
        }
        return SUCCESS;
    }
    
    // Getters and Setters
    public String getMedicineId() {
        return medicineId;
    }
    
    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }
    
    public Medicine getMedicine() {
        return medicine;
    }
    
    public List<Medicine> getMedicines() {
        return medicines;
    }
    
    public String getSearchQuery() {
        return searchQuery;
    }
    
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}

// Made with Bob
