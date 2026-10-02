package com.pharmacy.api;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/orders")
public class OrderResource {
    
    private final OrderRepository orderRepo = OrderRepository.getInstance();
    private final PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private final MedicineRepository medicineRepo = MedicineRepository.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("id") String id) {
        Order order = orderRepo.findById(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Order not found\"}")
                .build();
        }
        return Response.ok(order).build();
    }
    
    @POST
    @Path("/from-prescription")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrderFromPrescription(Map<String, String> requestData) {
        try {
            String prescriptionId = requestData.get("prescriptionId");
            String paymentMethod = requestData.getOrDefault("paymentMethod", "CASH");
            
            Prescription prescription = prescriptionRepo.findById(prescriptionId);
            
            if (prescription == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Prescription not found\"}")
                    .build();
            }
            
            if (!"VALIDATED".equals(prescription.getStatus())) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Prescription must be validated before creating an order\"}")
                    .build();
            }
            
            // Get medicine to calculate total
            Medicine medicine = medicineRepo.findById(prescription.getMedicineId());
            if (medicine == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Medicine not found\"}")
                    .build();
            }
            
            // Check stock
            if (medicine.getStockQuantity() < prescription.getQuantity()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Insufficient stock\"}")
                    .build();
            }
            
            // Calculate total
            BigDecimal totalAmount = medicine.getPrice()
                .multiply(new BigDecimal(prescription.getQuantity()));
            
            // Create order
            String orderId = orderRepo.generateId();
            Order order = new Order(
                orderId,
                prescriptionId,
                prescription.getPatientName(),
                prescription.getPatientId(),
                prescription.getMedicineId(),
                prescription.getMedicineName(),
                prescription.getQuantity(),
                totalAmount,
                new Date(),
                "PENDING",
                paymentMethod,
                ""
            );
            
            orderRepo.addOrder(order);
            
            // Update prescription status
            prescription.setStatus("FULFILLED");
            prescriptionRepo.updatePrescription(prescription);
            
            return Response.status(Response.Status.CREATED)
                .entity(order)
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Failed to create order: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @PUT
    @Path("/{id}/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processPayment(@PathParam("id") String id, Map<String, String> paymentData) {
        Order order = orderRepo.findById(id);
        
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Order not found\"}")
                .build();
        }
        
        if (!"PENDING".equals(order.getStatus()) && !"VALIDATED".equals(order.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Order cannot be paid in current status\"}")
                .build();
        }
        
        String paymentMethod = paymentData.get("paymentMethod");
        if (paymentMethod != null) {
            order.setPaymentMethod(paymentMethod);
        }
        
        order.setStatus("PAID");
        orderRepo.updateOrder(order);
        
        // Update stock
        medicineRepo.updateStock(order.getMedicineId(), order.getQuantity());
        
        return Response.ok(order).build();
    }
    
    @PUT
    @Path("/{id}/collect")
    @Produces(MediaType.APPLICATION_JSON)
    public Response collectOrder(@PathParam("id") String id) {
        Order order = orderRepo.findById(id);
        
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Order not found\"}")
                .build();
        }
        
        if (!"PAID".equals(order.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Order must be paid before collection\"}")
                .build();
        }
        
        order.setStatus("COLLECTED");
        orderRepo.updateOrder(order);
        
        return Response.ok(order).build();
    }
}

// Made with Bob