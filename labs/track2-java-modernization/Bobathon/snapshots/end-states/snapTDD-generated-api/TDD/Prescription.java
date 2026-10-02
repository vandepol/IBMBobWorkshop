package com.pharmacy.model;

import java.util.Date;

/**
 * Prescription model representing a pharmacy prescription.
 * 
 * This model is designed based on the OpenAPI specification and
 * supports the complete prescription lifecycle from creation to validation.
 */
public class Prescription {
    
    private String id;
    private String patientName;
    private String patientId;
    private String doctorName;
    private String medicineId;
    private String medicineName;
    private Integer quantity;
    private String dosage;
    private Date prescriptionDate;
    private Date expiryDate;
    private String status; // PENDING or VALIDATED
    private String notes;

    // Default constructor
    public Prescription() {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
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

    @Override
    public String toString() {
        return "Prescription{" +
                "id='" + id + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", medicineId='" + medicineId + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", quantity=" + quantity +
                ", dosage='" + dosage + '\'' +
                ", prescriptionDate=" + prescriptionDate +
                ", expiryDate=" + expiryDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}

// Made with Bob
