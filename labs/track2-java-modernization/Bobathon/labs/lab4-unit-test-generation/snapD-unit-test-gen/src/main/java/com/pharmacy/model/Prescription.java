package com.pharmacy.model;

import java.io.Serializable;
import java.util.Date;

public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String patientName;
    private String patientId;
    private String doctorName;
    private String medicineId;
    private String medicineName;
    private int quantity;
    private String dosage;
    private Date prescriptionDate;
    private Date expiryDate;
    private String status; // PENDING, VALIDATED, FULFILLED, EXPIRED
    private String notes;
    
    public Prescription() {
    }
    
    public Prescription(String id, String patientName, String patientId, String doctorName,
                       String medicineId, String medicineName, int quantity, String dosage,
                       Date prescriptionDate, Date expiryDate, String status, String notes) {
        this.id = id;
        this.patientName = patientName;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dosage = dosage;
        this.prescriptionDate = prescriptionDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
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
    
    public Date getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(Date prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

// Made with Bob
