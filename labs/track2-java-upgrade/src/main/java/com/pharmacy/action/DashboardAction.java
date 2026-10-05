package com.pharmacy.action;

import com.opensymphony.xwork2.ActionSupport;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import java.util.List;

public class DashboardAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    
    private List<Prescription> pendingPrescriptions;
    private List<Order> pendingOrders;
    private int totalPrescriptions;
    private int totalOrders;
    
    private PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private OrderRepository orderRepo = OrderRepository.getInstance();
    
    public String execute() {
        // Get pending prescriptions
        pendingPrescriptions = prescriptionRepo.findByStatus("PENDING");
        
        // Get pending orders
        pendingOrders = orderRepo.findByStatus("PENDING");
        
        // Get totals
        totalPrescriptions = prescriptionRepo.findAll().size();
        totalOrders = orderRepo.findAll().size();
        
        return SUCCESS;
    }
    
    // Getters
    public List<Prescription> getPendingPrescriptions() {
        return pendingPrescriptions;
    }
    
    public List<Order> getPendingOrders() {
        return pendingOrders;
    }
    
    public int getTotalPrescriptions() {
        return totalPrescriptions;
    }
    
    public int getTotalOrders() {
        return totalOrders;
    }
}

// Made with Bob
