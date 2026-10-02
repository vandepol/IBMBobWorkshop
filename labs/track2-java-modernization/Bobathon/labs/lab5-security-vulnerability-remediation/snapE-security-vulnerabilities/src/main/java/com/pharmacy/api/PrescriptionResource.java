package com.pharmacy.api;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.PrescriptionRepository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/prescriptions")
public class PrescriptionResource {
    
    private final PrescriptionRepository prescriptionRepo = PrescriptionRepository.getInstance();
    private final MedicineRepository medicineRepo = MedicineRepository.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepo.findAll();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrescriptionById(@PathParam("id") String id) {
        Prescription prescription = prescriptionRepo.findById(id);
        if (prescription == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Prescription not found\"}")
                .build();
        }
        return Response.ok(prescription).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPrescription(Map<String, Object> prescriptionData) {
        try {
            String medicineId = (String) prescriptionData.get("medicineId");
            Medicine medicine = medicineRepo.findById(medicineId);
            
            if (medicine == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid medicine ID\"}")
                    .build();
            }
            
            // Generate new prescription ID
            String id = prescriptionRepo.generateId();
            
            // Set dates
            Date prescriptionDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(prescriptionDate);
            cal.add(Calendar.DAY_OF_MONTH, 30);
            Date expiryDate = cal.getTime();
            
            // Create prescription
            Prescription prescription = new Prescription(
                id,
                (String) prescriptionData.get("patientName"),
                (String) prescriptionData.get("patientId"),
                (String) prescriptionData.get("doctorName"),
                medicineId,
                medicine.getName(),
                ((Number) prescriptionData.get("quantity")).intValue(),
                (String) prescriptionData.get("dosage"),
                prescriptionDate,
                expiryDate,
                "PENDING",
                (String) prescriptionData.getOrDefault("notes", "")
            );
            
            prescriptionRepo.addPrescription(prescription);
            
            return Response.status(Response.Status.CREATED)
                .entity(prescription)
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Invalid prescription data: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @PUT
    @Path("/{id}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validatePrescription(@PathParam("id") String id) {
        Prescription prescription = prescriptionRepo.findById(id);
        
        if (prescription == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Prescription not found\"}")
                .build();
        }
        
        if (!"PENDING".equals(prescription.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Prescription is not in PENDING status\"}")
                .build();
        }
        
        prescription.setStatus("VALIDATED");
        prescriptionRepo.updatePrescription(prescription);
        
        return Response.ok(prescription).build();
    }
}