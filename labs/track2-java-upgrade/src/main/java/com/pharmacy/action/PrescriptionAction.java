package com.pharmacy.action;

import com.opensymphony.xwork2.ActionSupport;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.PrescriptionRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrescriptionAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    
    private String prescriptionId;
    private Prescription prescription;
    private List<Prescription> prescriptions;
    private List<Medicine> medicines;
    
    // Form fields
    private String patientName;
    private String patientId;
    private String doctorName;
    private String medicineId;
    private int quantity;
    private String dosage;
    private String notes;
    
    private PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private MedicineRepository medicineRepo = MedicineRepository.getInstance();
    
    public String list() {
        prescriptions = prescriptionRepo.findAll();
        return SUCCESS;
    }
    
    public String view() {
        if (prescriptionId != null) {
            prescription = prescriptionRepo.findById(prescriptionId);
            if (prescription == null) {
                addActionError("Prescription not found");
                return ERROR;
            }
        }
        return SUCCESS;
    }
    
    public String create() {
        medicines = medicineRepo.findAll();
        return SUCCESS;
    }
    
    public String save() {
        try {
            Medicine medicine = medicineRepo.findById(medicineId);
            if (medicine == null) {
                addActionError("Medicine not found");
                return ERROR;
            }
            
            // Create new prescription
            String newId = prescriptionRepo.generateId();
            Date prescDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(prescDate);
            cal.add(Calendar.DAY_OF_MONTH, 30);
            Date expiryDate = cal.getTime();
            
            Prescription newPrescription = new Prescription(
                newId, patientName, patientId, doctorName,
                medicineId, medicine.getName(), quantity, dosage,
                prescDate, expiryDate, "PENDING", notes
            );
            
            prescriptionRepo.addPrescription(newPrescription);
            addActionMessage("Prescription created successfully");
            
            return SUCCESS;
        } catch (Exception e) {
            addActionError("Error creating prescription: " + e.getMessage());
            return ERROR;
        }
    }
    
    public String validatePrescription() {
        if (prescriptionId != null) {
            prescription = prescriptionRepo.findById(prescriptionId);
            if (prescription != null) {
                prescription.setStatus("VALIDATED");
                prescriptionRepo.updatePrescription(prescription);
                addActionMessage("Prescription validated successfully");
                return SUCCESS;
            }
        }
        addActionError("Prescription not found");
        return ERROR;
    }
    
    // Getters and Setters
    public String getPrescriptionId() {
        return prescriptionId;
    }
    
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    
    public Prescription getPrescription() {
        return prescription;
    }
    
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }
    
    public List<Medicine> getMedicines() {
        return medicines;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getMedicineId() {
        return medicineId;
    }
    
    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

// Made with Bob
