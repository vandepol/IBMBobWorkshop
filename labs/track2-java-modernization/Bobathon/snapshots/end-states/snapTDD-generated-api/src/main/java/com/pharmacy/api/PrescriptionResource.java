package com.pharmacy.api;

import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * REST API Resource for managing pharmacy prescriptions.
 * 
 * This implementation is driven by Test Driven Development (TDD) methodology,
 * ensuring all functionality is validated by comprehensive unit tests.
 * 
 * Endpoints:
 * - GET /prescriptions - Retrieve all prescriptions
 * - GET /prescriptions/{id} - Get prescription by ID
 * - POST /prescriptions - Create new prescription
 * - PUT /prescriptions/{id}/validate - Validate a prescription
 * 
 * Business Rules:
 * - Prescriptions are created with PENDING status
 * - Expiry date is automatically set to 30 days from creation
 * - Only PENDING prescriptions can be validated
 * - Medicine ID must exist in the system
 * - All required fields must be provided
 */
@Path("/prescriptions")
public class PrescriptionResource {

    private static final Logger logger = LoggerFactory.getLogger(PrescriptionResource.class);

    // Constants for status values
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_VALIDATED = "VALIDATED";
    
    // Constants for error messages
    private static final String ERROR_PRESCRIPTION_NOT_FOUND = "Prescription not found";
    private static final String ERROR_NOT_PENDING_STATUS = "Prescription is not in PENDING status";
    private static final String ERROR_INVALID_MEDICINE_ID = "Invalid medicine ID";
    private static final String ERROR_FIELD_REQUIRED = "Invalid prescription data: %s is required";
    private static final String ERROR_QUANTITY_POSITIVE = "Invalid prescription data: quantity must be a positive number";
    
    // Constants for field names
    private static final String FIELD_PATIENT_NAME = "patientName";
    private static final String FIELD_PATIENT_ID = "patientId";
    private static final String FIELD_DOCTOR_NAME = "doctorName";
    private static final String FIELD_MEDICINE_ID = "medicineId";
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_DOSAGE = "dosage";
    private static final String FIELD_NOTES = "notes";
    
    // Constants for business logic
    private static final int EXPIRY_DAYS = 30;
    private static final String ID_PREFIX = "RX";

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    /**
     * Constructor with dependency injection for repositories.
     * Allows for easy mocking in unit tests.
     */
    public PrescriptionResource() {
        this.prescriptionRepository = PrescriptionRepository.getInstance();
        this.medicineRepository = MedicineRepository.getInstance();
    }

    /**
     * Constructor for testing with mock repositories.
     */
    public PrescriptionResource(PrescriptionRepository prescriptionRepository, 
                               MedicineRepository medicineRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicineRepository = medicineRepository;
    }

    /**
     * GET /prescriptions
     * Retrieves all prescriptions in the system.
     * 
     * @return List of all prescriptions (empty list if none exist)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Prescription> getAllPrescriptions() {
        logger.info("Retrieving all prescriptions");
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        logger.info("Retrieved {} prescriptions", prescriptions.size());
        return prescriptions;
    }

    /**
     * GET /prescriptions/{id}
     * Retrieves a specific prescription by its unique identifier.
     * 
     * @param id Unique prescription identifier
     * @return Response with prescription data (200) or error (404)
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrescriptionById(@PathParam("id") String id) {
        logger.info("Retrieving prescription with ID: {}", id);
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        
        if (prescription.isPresent()) {
            logger.info("Prescription found: {}", id);
            return Response.ok(prescription.get()).build();
        } else {
            logger.warn("Prescription not found: {}", id);
            return createErrorResponse(Response.Status.NOT_FOUND, ERROR_PRESCRIPTION_NOT_FOUND);
        }
    }

    /**
     * POST /prescriptions
     * Creates a new prescription with automatic field generation.
     * 
     * Automatic Processing:
     * - Generates unique prescription ID (RX prefix)
     * - Sets prescription date to current date/time
     * - Calculates expiry date (30 days from creation)
     * - Sets initial status to PENDING
     * - Retrieves medicine name from medicine ID
     * 
     * Validation:
     * - All required fields must be present
     * - Medicine ID must exist in the system
     * - Quantity must be a positive number
     * 
     * @param prescriptionData Map containing prescription request data
     * @return Response with created prescription (201) or error (400)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPrescription(Map<String, Object> prescriptionData) {
        logger.info("Creating new prescription for patient: {}", prescriptionData.get(FIELD_PATIENT_NAME));
        
        try {
            // Validate required fields
            validateRequiredFields(prescriptionData);
            
            // Validate quantity
            validateQuantity(prescriptionData);
            
            // Validate medicine ID exists
            String medicineId = (String) prescriptionData.get(FIELD_MEDICINE_ID);
            if (!medicineRepository.existsById(medicineId)) {
                logger.warn("Invalid medicine ID attempted: {}", medicineId);
                return createErrorResponse(Response.Status.BAD_REQUEST, ERROR_INVALID_MEDICINE_ID);
            }
            
            // Create new prescription
            Prescription prescription = new Prescription();
            
            // Auto-generate ID
            prescription.setId(generatePrescriptionId());
            
            // Set provided fields
            prescription.setPatientName((String) prescriptionData.get(FIELD_PATIENT_NAME));
            prescription.setPatientId((String) prescriptionData.get(FIELD_PATIENT_ID));
            prescription.setDoctorName((String) prescriptionData.get(FIELD_DOCTOR_NAME));
            prescription.setMedicineId(medicineId);
            prescription.setMedicineName(medicineRepository.getMedicineName(medicineId));
            prescription.setQuantity(getIntegerValue(prescriptionData.get(FIELD_QUANTITY)));
            prescription.setDosage((String) prescriptionData.get(FIELD_DOSAGE));
            prescription.setNotes((String) prescriptionData.getOrDefault(FIELD_NOTES, ""));
            
            // Set automatic fields
            Date prescriptionDate = new Date();
            prescription.setPrescriptionDate(prescriptionDate);
            prescription.setExpiryDate(calculateExpiryDate(prescriptionDate));
            prescription.setStatus(STATUS_PENDING);
            
            // Save prescription
            Prescription savedPrescription = prescriptionRepository.save(prescription);
            
            logger.info("Prescription created successfully with ID: {} for patient: {}",
                savedPrescription.getId(), savedPrescription.getPatientName());
            
            return Response.status(Response.Status.CREATED)
                          .entity(savedPrescription)
                          .build();
                          
        } catch (ValidationException e) {
            logger.error("Validation failed for prescription creation: {}", e.getMessage());
            return createErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating prescription: {}", e.getMessage(), e);
            return createErrorResponse(Response.Status.BAD_REQUEST,
                "Invalid prescription data: " + e.getMessage());
        }
    }

    /**
     * PUT /prescriptions/{id}/validate
     * Validates a prescription, changing its status from PENDING to VALIDATED.
     * 
     * Requirements:
     * - Prescription must exist
     * - Prescription must be in PENDING status
     * 
     * @param id Unique prescription identifier
     * @return Response with validated prescription (200) or error (400/404)
     */
    @PUT
    @Path("/{id}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validatePrescription(@PathParam("id") String id) {
        logger.info("Validating prescription with ID: {}", id);
        
        Optional<Prescription> prescriptionOpt = prescriptionRepository.findById(id);
        
        if (!prescriptionOpt.isPresent()) {
            logger.warn("Cannot validate - prescription not found: {}", id);
            return createErrorResponse(Response.Status.NOT_FOUND, ERROR_PRESCRIPTION_NOT_FOUND);
        }
        
        Prescription prescription = prescriptionOpt.get();
        
        if (!STATUS_PENDING.equals(prescription.getStatus())) {
            logger.warn("Cannot validate - prescription {} is not in PENDING status. Current status: {}",
                id, prescription.getStatus());
            return createErrorResponse(Response.Status.BAD_REQUEST, ERROR_NOT_PENDING_STATUS);
        }
        
        // Update status to VALIDATED
        prescription.setStatus(STATUS_VALIDATED);
        Prescription validatedPrescription = prescriptionRepository.save(prescription);
        
        logger.info("Prescription validated successfully: {} for patient: {}",
            id, validatedPrescription.getPatientName());
        
        return Response.ok(validatedPrescription).build();
    }

    /**
     * Validates that all required fields are present in the prescription data.
     * 
     * @param data Prescription request data
     * @throws ValidationException if any required field is missing
     */
    private void validateRequiredFields(Map<String, Object> data) throws ValidationException {
        String[] requiredFields = {
            FIELD_PATIENT_NAME, FIELD_PATIENT_ID, FIELD_DOCTOR_NAME,
            FIELD_MEDICINE_ID, FIELD_QUANTITY, FIELD_DOSAGE
        };
        
        for (String field : requiredFields) {
            if (!data.containsKey(field) || data.get(field) == null) {
                throw new ValidationException(String.format(ERROR_FIELD_REQUIRED, field));
            }
        }
    }

    /**
     * Validates that quantity is a positive number.
     *
     * @param data Prescription request data
     * @throws ValidationException if quantity is not positive
     */
    private void validateQuantity(Map<String, Object> data) throws ValidationException {
        Object quantityObj = data.get(FIELD_QUANTITY);
        int quantity = getIntegerValue(quantityObj);
        
        if (quantity <= 0) {
            throw new ValidationException(ERROR_QUANTITY_POSITIVE);
        }
    }

    /**
     * Converts an object to integer value.
     * Handles both Integer and Number types.
     * 
     * @param value Object to convert
     * @return Integer value
     */
    private int getIntegerValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new IllegalArgumentException("Invalid quantity value");
        }
    }

    /**
     * Calculates expiry date as 30 days from the given start date.
     * 
     * @param startDate Starting date for calculation
     * @return Date 30 days after start date
     */
    private Date calculateExpiryDate(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, EXPIRY_DAYS);
        return calendar.getTime();
    }

    /**
     * Generates a unique prescription ID with RX prefix.
     * Format: RX + timestamp + random number
     *
     * @return Unique prescription ID
     */
    private String generatePrescriptionId() {
        return ID_PREFIX + System.currentTimeMillis() +
               String.format("%03d", new Random().nextInt(1000));
    }

    /**
     * Creates a standardized error response.
     * 
     * @param status HTTP status code
     * @param message Error message
     * @return Response with error details
     */
    private Response createErrorResponse(Response.Status status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return Response.status(status).entity(error).build();
    }

    /**
     * Custom exception for validation errors.
     */
    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}

// Made with Bob
