package com.pharmacy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String prescriptionId;
    private String patientName;
    private String patientId;
    private String medicineId;
    private String medicineName;
    private int quantity;
    private BigDecimal totalAmount;
    private Date orderDate;
    private String status; // PENDING, VALIDATED, PAID, COLLECTED, CANCELLED
    private String paymentMethod; // CASH, CREDIT_CARD, INSURANCE
    private String pharmacistNotes;
    
    public Order() {
    }
    
    public Order(String id, String prescriptionId, String patientName, String patientId,
                String medicineId, String medicineName, int quantity, BigDecimal totalAmount,
                Date orderDate, String status, String paymentMethod, String pharmacistNotes) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.patientName = patientName;
        this.patientId = patientId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.pharmacistNotes = pharmacistNotes;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPrescriptionId() {
        return prescriptionId;
    }
    
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
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
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPharmacistNotes() {
        return pharmacistNotes;
    }
    
    public void setPharmacistNotes(String pharmacistNotes) {
        this.pharmacistNotes = pharmacistNotes;
    }
}

// Made with Bob
