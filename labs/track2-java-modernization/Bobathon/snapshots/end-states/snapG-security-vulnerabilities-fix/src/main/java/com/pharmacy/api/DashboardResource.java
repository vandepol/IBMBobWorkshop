package com.pharmacy.api;

import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/dashboard")
public class DashboardResource {
    
    private final PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private final OrderRepository orderRepo = OrderRepository.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDashboardData() {
        List<Prescription> pendingPrescriptions = prescriptionRepo.findByStatus("PENDING");
        List<Order> pendingOrders = orderRepo.findByStatus("PENDING");
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("pendingPrescriptions", pendingPrescriptions);
        dashboard.put("pendingOrders", pendingOrders);
        dashboard.put("totalPrescriptions", prescriptionRepo.findAll().size());
        dashboard.put("totalOrders", orderRepo.findAll().size());
        
        return dashboard;
    }
}