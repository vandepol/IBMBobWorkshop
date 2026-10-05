package com.pharmacy.action;

import com.opensymphony.xwork2.ActionSupport;
import com.pharmacy.model.Medicine;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    
    private String orderId;
    private String prescriptionId;
    private Order order;
    private List<Order> orders;
    private String paymentMethod;
    private String pharmacistNotes;
    
    private OrderRepository orderRepo = OrderRepository.getInstance();
    private PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private MedicineRepository medicineRepo = MedicineRepository.getInstance();
    
    public String list() {
        orders = orderRepo.findAll();
        return SUCCESS;
    }
    
    public String view() {
        if (orderId != null) {
            order = orderRepo.findById(orderId);
            if (order == null) {
                addActionError("Order not found");
                return ERROR;
            }
        }
        return SUCCESS;
    }
    
    public String createFromPrescription() {
        try {
            if (prescriptionId == null) {
                addActionError("Prescription ID is required");
                return ERROR;
            }
            
            Prescription prescription = prescriptionRepo.findById(prescriptionId);
            if (prescription == null) {
                addActionError("Prescription not found");
                return ERROR;
            }
            
            if (!"VALIDATED".equals(prescription.getStatus())) {
                addActionError("Prescription must be validated before creating an order");
                return ERROR;
            }
            
            Medicine medicine = medicineRepo.findById(prescription.getMedicineId());
            if (medicine == null) {
                addActionError("Medicine not found");
                return ERROR;
            }
            
            // Check stock availability
            if (medicine.getStockQuantity() < prescription.getQuantity()) {
                addActionError("Insufficient stock. Available: " + medicine.getStockQuantity());
                return ERROR;
            }
            
            // Calculate total amount
            BigDecimal totalAmount = medicine.getPrice().multiply(new BigDecimal(prescription.getQuantity()));
            
            // Create order
            String newOrderId = orderRepo.generateId();
            Order newOrder = new Order(
                newOrderId,
                prescription.getId(),
                prescription.getPatientName(),
                prescription.getPatientId(),
                prescription.getMedicineId(),
                prescription.getMedicineName(),
                prescription.getQuantity(),
                totalAmount,
                new Date(),
                "PENDING",
                null,
                null
            );
            
            orderRepo.addOrder(newOrder);
            addActionMessage("Order created successfully. Order ID: " + newOrderId);
            
            return SUCCESS;
        } catch (Exception e) {
            addActionError("Error creating order: " + e.getMessage());
            return ERROR;
        }
    }
    
    public String processPayment() {
        try {
            if (orderId == null) {
                addActionError("Order ID is required");
                return ERROR;
            }
            
            order = orderRepo.findById(orderId);
            if (order == null) {
                addActionError("Order not found");
                return ERROR;
            }
            
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                addActionError("Payment method is required");
                return ERROR;
            }
            
            // Mock payment processing
            order.setPaymentMethod(paymentMethod);
            order.setStatus("PAID");
            order.setPharmacistNotes(pharmacistNotes);
            orderRepo.updateOrder(order);
            
            // Update prescription status
            Prescription prescription = prescriptionRepo.findById(order.getPrescriptionId());
            if (prescription != null) {
                prescription.setStatus("FULFILLED");
                prescriptionRepo.updatePrescription(prescription);
            }
            
            // Update medicine stock
            medicineRepo.updateStock(order.getMedicineId(), order.getQuantity());
            
            addActionMessage("Payment processed successfully");
            return SUCCESS;
        } catch (Exception e) {
            addActionError("Error processing payment: " + e.getMessage());
            return ERROR;
        }
    }
    
    public String collect() {
        try {
            if (orderId == null) {
                addActionError("Order ID is required");
                return ERROR;
            }
            
            order = orderRepo.findById(orderId);
            if (order == null) {
                addActionError("Order not found");
                return ERROR;
            }
            
            if (!"PAID".equals(order.getStatus())) {
                addActionError("Order must be paid before collection");
                return ERROR;
            }
            
            order.setStatus("COLLECTED");
            orderRepo.updateOrder(order);
            
            addActionMessage("Order marked as collected");
            return SUCCESS;
        } catch (Exception e) {
            addActionError("Error marking order as collected: " + e.getMessage());
            return ERROR;
        }
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getPrescriptionId() {
        return prescriptionId;
    }
    
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public List<Order> getOrders() {
        return orders;
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
