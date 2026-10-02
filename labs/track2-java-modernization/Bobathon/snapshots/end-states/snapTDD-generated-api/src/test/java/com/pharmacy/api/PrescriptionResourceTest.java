package com.pharmacy.api;

import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit test suite for PrescriptionResource following TDD methodology.
 * Tests are generated from prescription-api-spec.yaml OpenAPI specification.
 * 
 * Test Coverage:
 * - GET /prescriptions - Retrieve all prescriptions
 * - GET /prescriptions/{id} - Get prescription by ID
 * - POST /prescriptions - Create new prescription
 * - PUT /prescriptions/{id}/validate - Validate prescription
 * 
 * Each endpoint is tested for:
 * - Success scenarios (happy path)
 * - Error scenarios (4xx responses)
 * - Edge cases and boundary conditions
 * - Data validation
 * - Business logic rules
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Prescription API Resource Tests")
public class PrescriptionResourceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private PrescriptionResource prescriptionResource;

    private Prescription validPrescription;
    private Map<String, Object> validPrescriptionRequest;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        // Set up valid prescription for testing
        validPrescription = new Prescription();
        validPrescription.setId("RX001");
        validPrescription.setPatientName("John Doe");
        validPrescription.setPatientId("P12345");
        validPrescription.setDoctorName("Dr. Sarah Smith");
        validPrescription.setMedicineId("M001");
        validPrescription.setMedicineName("Amoxicillin");
        validPrescription.setQuantity(30);
        validPrescription.setDosage("500mg twice daily");
        validPrescription.setPrescriptionDate(new Date());
        
        // Calculate expiry date (30 days from now)
        Calendar cal = Calendar.getInstance();
        cal.setTime(validPrescription.getPrescriptionDate());
        cal.add(Calendar.DAY_OF_MONTH, 30);
        validPrescription.setExpiryDate(cal.getTime());
        
        validPrescription.setStatus("PENDING");
        validPrescription.setNotes("Take with food");

        // Set up valid prescription request
        validPrescriptionRequest = new HashMap<>();
        validPrescriptionRequest.put("patientName", "John Doe");
        validPrescriptionRequest.put("patientId", "P12345");
        validPrescriptionRequest.put("doctorName", "Dr. Sarah Smith");
        validPrescriptionRequest.put("medicineId", "M001");
        validPrescriptionRequest.put("quantity", 30);
        validPrescriptionRequest.put("dosage", "500mg twice daily");
        validPrescriptionRequest.put("notes", "Take with food");
    }

    @Nested
    @DisplayName("GET /prescriptions - Get All Prescriptions")
    class GetAllPrescriptionsTests {

        @Test
        @DisplayName("Should return empty list when no prescriptions exist")
        void shouldReturnEmptyListWhenNoPrescriptions() {
            // Arrange
            when(prescriptionRepository.findAll()).thenReturn(new ArrayList<>());

            // Act
            List<Prescription> result = prescriptionResource.getAllPrescriptions();

            // Assert
            assertNotNull(result, "Result should not be null");
            assertTrue(result.isEmpty(), "Result should be empty list");
            verify(prescriptionRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return all prescriptions when prescriptions exist")
        void shouldReturnAllPrescriptionsWhenPrescriptionsExist() {
            // Arrange
            Prescription prescription2 = new Prescription();
            prescription2.setId("RX002");
            prescription2.setPatientName("Jane Smith");
            prescription2.setPatientId("P67890");
            prescription2.setDoctorName("Dr. Michael Johnson");
            prescription2.setMedicineId("M002");
            prescription2.setMedicineName("Ibuprofen");
            prescription2.setQuantity(60);
            prescription2.setDosage("200mg as needed");
            prescription2.setPrescriptionDate(new Date());
            prescription2.setExpiryDate(new Date());
            prescription2.setStatus("VALIDATED");
            prescription2.setNotes("");

            List<Prescription> prescriptions = Arrays.asList(validPrescription, prescription2);
            when(prescriptionRepository.findAll()).thenReturn(prescriptions);

            // Act
            List<Prescription> result = prescriptionResource.getAllPrescriptions();

            // Assert
            assertNotNull(result, "Result should not be null");
            assertEquals(2, result.size(), "Should return 2 prescriptions");
            assertEquals("RX001", result.get(0).getId());
            assertEquals("RX002", result.get(1).getId());
            verify(prescriptionRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return prescriptions with correct status values")
        void shouldReturnPrescriptionsWithCorrectStatusValues() {
            // Arrange
            List<Prescription> prescriptions = Arrays.asList(validPrescription);
            when(prescriptionRepository.findAll()).thenReturn(prescriptions);

            // Act
            List<Prescription> result = prescriptionResource.getAllPrescriptions();

            // Assert
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.size());
            assertTrue(result.get(0).getStatus().equals("PENDING") || 
                      result.get(0).getStatus().equals("VALIDATED"),
                      "Status should be either PENDING or VALIDATED");
        }
    }

    @Nested
    @DisplayName("GET /prescriptions/{id} - Get Prescription By ID")
    class GetPrescriptionByIdTests {

        @Test
        @DisplayName("Should return 200 and prescription when prescription exists")
        void shouldReturn200AndPrescriptionWhenExists() {
            // Arrange
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));

            // Act
            Response response = prescriptionResource.getPrescriptionById("RX001");

            // Assert
            assertEquals(200, response.getStatus(), "Should return 200 OK");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Prescription returnedPrescription = (Prescription) response.getEntity();
            assertEquals("RX001", returnedPrescription.getId());
            assertEquals("John Doe", returnedPrescription.getPatientName());
            verify(prescriptionRepository, times(1)).findById("RX001");
        }

        @Test
        @DisplayName("Should return 404 when prescription does not exist")
        void shouldReturn404WhenPrescriptionDoesNotExist() {
            // Arrange
            when(prescriptionRepository.findById("RX999")).thenReturn(Optional.empty());

            // Act
            Response response = prescriptionResource.getPrescriptionById("RX999");

            // Assert
            assertEquals(404, response.getStatus(), "Should return 404 Not Found");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertEquals("Prescription not found", error.get("error"));
            verify(prescriptionRepository, times(1)).findById("RX999");
        }

        @Test
        @DisplayName("Should return prescription with all required fields")
        void shouldReturnPrescriptionWithAllRequiredFields() {
            // Arrange
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));

            // Act
            Response response = prescriptionResource.getPrescriptionById("RX001");
            Prescription prescription = (Prescription) response.getEntity();

            // Assert
            assertNotNull(prescription.getId(), "ID should not be null");
            assertNotNull(prescription.getPatientName(), "Patient name should not be null");
            assertNotNull(prescription.getPatientId(), "Patient ID should not be null");
            assertNotNull(prescription.getDoctorName(), "Doctor name should not be null");
            assertNotNull(prescription.getMedicineId(), "Medicine ID should not be null");
            assertNotNull(prescription.getMedicineName(), "Medicine name should not be null");
            assertNotNull(prescription.getQuantity(), "Quantity should not be null");
            assertNotNull(prescription.getDosage(), "Dosage should not be null");
            assertNotNull(prescription.getPrescriptionDate(), "Prescription date should not be null");
            assertNotNull(prescription.getExpiryDate(), "Expiry date should not be null");
            assertNotNull(prescription.getStatus(), "Status should not be null");
        }
    }

    @Nested
    @DisplayName("POST /prescriptions - Create New Prescription")
    class CreatePrescriptionTests {

        @Test
        @DisplayName("Should return 201 and create prescription with valid data")
        void shouldReturn201AndCreatePrescriptionWithValidData() {
            // Arrange
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenReturn(validPrescription);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus(), "Should return 201 Created");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Prescription createdPrescription = (Prescription) response.getEntity();
            assertNotNull(createdPrescription.getId(), "ID should be auto-generated");
            assertEquals("John Doe", createdPrescription.getPatientName());
            assertEquals("PENDING", createdPrescription.getStatus(), "Initial status should be PENDING");
            assertNotNull(createdPrescription.getPrescriptionDate(), "Prescription date should be set");
            assertNotNull(createdPrescription.getExpiryDate(), "Expiry date should be calculated");
            verify(medicineRepository, times(1)).existsById("M001");
            verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        }

        @Test
        @DisplayName("Should set expiry date to 30 days from creation")
        void shouldSetExpiryDateTo30DaysFromCreation() {
            // Arrange
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
                Prescription prescription = invocation.getArgument(0);
                
                // Verify expiry date is 30 days from prescription date
                Calendar prescriptionCal = Calendar.getInstance();
                prescriptionCal.setTime(prescription.getPrescriptionDate());
                
                Calendar expiryCal = Calendar.getInstance();
                expiryCal.setTime(prescription.getExpiryDate());
                
                long diffInMillis = expiryCal.getTimeInMillis() - prescriptionCal.getTimeInMillis();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
                
                assertEquals(30, diffInDays, "Expiry date should be 30 days from prescription date");
                return prescription;
            });

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus());
        }

        @Test
        @DisplayName("Should return 400 when medicine ID does not exist")
        void shouldReturn400WhenMedicineIdDoesNotExist() {
            // Arrange
            when(medicineRepository.existsById("M999")).thenReturn(false);
            validPrescriptionRequest.put("medicineId", "M999");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertEquals("Invalid medicine ID", error.get("error"));
            verify(medicineRepository, times(1)).existsById("M999");
            verify(prescriptionRepository, never()).save(any(Prescription.class));
        }

        @Test
        @DisplayName("Should return 400 when patient name is missing")
        void shouldReturn400WhenPatientNameIsMissing() {
            // Arrange
            validPrescriptionRequest.remove("patientName");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("patientName") || 
                      error.get("error").contains("required"),
                      "Error should mention missing required field");
        }

        @Test
        @DisplayName("Should return 400 when patient ID is missing")
        void shouldReturn400WhenPatientIdIsMissing() {
            // Arrange
            validPrescriptionRequest.remove("patientId");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("patientId") || 
                      error.get("error").contains("required"),
                      "Error should mention missing required field");
        }

        @Test
        @DisplayName("Should return 400 when doctor name is missing")
        void shouldReturn400WhenDoctorNameIsMissing() {
            // Arrange
            validPrescriptionRequest.remove("doctorName");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("doctorName") || 
                      error.get("error").contains("required"),
                      "Error should mention missing required field");
        }

        @Test
        @DisplayName("Should return 400 when medicine ID is missing")
        void shouldReturn400WhenMedicineIdIsMissing() {
            // Arrange
            validPrescriptionRequest.remove("medicineId");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("medicineId") || 
                      error.get("error").contains("required"),
                      "Error should mention missing required field");
        }

        @Test
        @DisplayName("Should return 400 when quantity is missing")
        void shouldReturn400WhenQuantityIsMissing() {
            // Arrange
            validPrescriptionRequest.remove("quantity");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("quantity") || 
                      error.get("error").contains("required"),
                      "Error should mention missing required field");
        }

        @Test
        @DisplayName("Should return 400 when dosage is missing")
        void shouldReturn400WhenDosageIsMissing() {
            // Arrange
            validPrescriptionRequest.remove("dosage");

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("dosage") || 
                      error.get("error").contains("required"),
                      "Error should mention missing required field");
        }

        @Test
        @DisplayName("Should return 400 when quantity is not a positive number")
        void shouldReturn400WhenQuantityIsNotPositive() {
            // Arrange
            validPrescriptionRequest.put("quantity", 0);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("quantity") && 
                      error.get("error").contains("positive"),
                      "Error should mention quantity must be positive");
        }

        @Test
        @DisplayName("Should return 400 when quantity is negative")
        void shouldReturn400WhenQuantityIsNegative() {
            // Arrange
            validPrescriptionRequest.put("quantity", -5);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertTrue(error.get("error").contains("quantity") && 
                      error.get("error").contains("positive"),
                      "Error should mention quantity must be positive");
        }

        @Test
        @DisplayName("Should accept prescription without notes (optional field)")
        void shouldAcceptPrescriptionWithoutNotes() {
            // Arrange
            validPrescriptionRequest.remove("notes");
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenReturn(validPrescription);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus(), "Should return 201 Created even without notes");
            verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        }

        @Test
        @DisplayName("Should auto-generate unique prescription ID")
        void shouldAutoGenerateUniquePrescriptionId() {
            // Arrange
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
                Prescription prescription = invocation.getArgument(0);
                assertNotNull(prescription.getId(), "ID should be generated before save");
                assertTrue(prescription.getId().startsWith("RX"), "ID should start with RX prefix");
                return prescription;
            });

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus());
        }
    }

    @Nested
    @DisplayName("PUT /prescriptions/{id}/validate - Validate Prescription")
    class ValidatePrescriptionTests {

        @Test
        @DisplayName("Should return 200 and validate prescription when prescription is PENDING")
        void shouldReturn200AndValidatePrescriptionWhenPending() {
            // Arrange
            validPrescription.setStatus("PENDING");
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));
            when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
                Prescription prescription = invocation.getArgument(0);
                assertEquals("VALIDATED", prescription.getStatus(), "Status should be changed to VALIDATED");
                return prescription;
            });

            // Act
            Response response = prescriptionResource.validatePrescription("RX001");

            // Assert
            assertEquals(200, response.getStatus(), "Should return 200 OK");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Prescription validatedPrescription = (Prescription) response.getEntity();
            assertEquals("VALIDATED", validatedPrescription.getStatus(), "Status should be VALIDATED");
            verify(prescriptionRepository, times(1)).findById("RX001");
            verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        }

        @Test
        @DisplayName("Should return 404 when prescription does not exist")
        void shouldReturn404WhenPrescriptionDoesNotExist() {
            // Arrange
            when(prescriptionRepository.findById("RX999")).thenReturn(Optional.empty());

            // Act
            Response response = prescriptionResource.validatePrescription("RX999");

            // Assert
            assertEquals(404, response.getStatus(), "Should return 404 Not Found");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertEquals("Prescription not found", error.get("error"));
            verify(prescriptionRepository, times(1)).findById("RX999");
            verify(prescriptionRepository, never()).save(any(Prescription.class));
        }

        @Test
        @DisplayName("Should return 400 when prescription is already VALIDATED")
        void shouldReturn400WhenPrescriptionAlreadyValidated() {
            // Arrange
            validPrescription.setStatus("VALIDATED");
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));

            // Act
            Response response = prescriptionResource.validatePrescription("RX001");

            // Assert
            assertEquals(400, response.getStatus(), "Should return 400 Bad Request");
            assertNotNull(response.getEntity(), "Response entity should not be null");
            Map<String, String> error = (Map<String, String>) response.getEntity();
            assertEquals("Prescription is not in PENDING status", error.get("error"));
            verify(prescriptionRepository, times(1)).findById("RX001");
            verify(prescriptionRepository, never()).save(any(Prescription.class));
        }

        @Test
        @DisplayName("Should not modify other prescription fields during validation")
        void shouldNotModifyOtherFieldsDuringValidation() {
            // Arrange
            validPrescription.setStatus("PENDING");
            String originalPatientName = validPrescription.getPatientName();
            String originalDoctorName = validPrescription.getDoctorName();
            int originalQuantity = validPrescription.getQuantity();
            
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));
            when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
                Prescription prescription = invocation.getArgument(0);
                assertEquals(originalPatientName, prescription.getPatientName(), "Patient name should not change");
                assertEquals(originalDoctorName, prescription.getDoctorName(), "Doctor name should not change");
                assertEquals(originalQuantity, prescription.getQuantity(), "Quantity should not change");
                return prescription;
            });

            // Act
            Response response = prescriptionResource.validatePrescription("RX001");

            // Assert
            assertEquals(200, response.getStatus());
            Prescription result = (Prescription) response.getEntity();
            assertEquals(originalPatientName, result.getPatientName());
            assertEquals(originalDoctorName, result.getDoctorName());
            assertEquals(originalQuantity, result.getQuantity());
        }

        @Test
        @DisplayName("Should only allow validation from PENDING to VALIDATED status")
        void shouldOnlyAllowValidationFromPendingToValidated() {
            // Arrange
            validPrescription.setStatus("PENDING");
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));
            when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
                Prescription prescription = invocation.getArgument(0);
                assertEquals("VALIDATED", prescription.getStatus(), 
                    "Status should transition from PENDING to VALIDATED only");
                return prescription;
            });

            // Act
            Response response = prescriptionResource.validatePrescription("RX001");

            // Assert
            assertEquals(200, response.getStatus());
            verify(prescriptionRepository, times(1)).save(any(Prescription.class));
        }
    }

    @Nested
    @DisplayName("Business Logic and Edge Cases")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should handle prescription with minimum quantity (1)")
        void shouldHandlePrescriptionWithMinimumQuantity() {
            // Arrange
            validPrescriptionRequest.put("quantity", 1);
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenReturn(validPrescription);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus(), "Should accept minimum quantity of 1");
        }

        @Test
        @DisplayName("Should handle prescription with large quantity")
        void shouldHandlePrescriptionWithLargeQuantity() {
            // Arrange
            validPrescriptionRequest.put("quantity", 1000);
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenReturn(validPrescription);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus(), "Should accept large quantities");
        }

        @Test
        @DisplayName("Should handle empty notes field")
        void shouldHandleEmptyNotesField() {
            // Arrange
            validPrescriptionRequest.put("notes", "");
            when(medicineRepository.existsById("M001")).thenReturn(true);
            when(medicineRepository.getMedicineName("M001")).thenReturn("Amoxicillin");
            when(prescriptionRepository.save(any(Prescription.class))).thenReturn(validPrescription);

            // Act
            Response response = prescriptionResource.createPrescription(validPrescriptionRequest);

            // Assert
            assertEquals(201, response.getStatus(), "Should accept empty notes");
        }

        @Test
        @DisplayName("Should preserve all prescription data through validation")
        void shouldPreserveAllDataThroughValidation() {
            // Arrange
            validPrescription.setStatus("PENDING");
            when(prescriptionRepository.findById("RX001")).thenReturn(Optional.of(validPrescription));
            when(prescriptionRepository.save(any(Prescription.class))).thenReturn(validPrescription);

            // Act
            Response response = prescriptionResource.validatePrescription("RX001");

            // Assert
            assertEquals(200, response.getStatus());
            Prescription result = (Prescription) response.getEntity();
            assertEquals(validPrescription.getId(), result.getId());
            assertEquals(validPrescription.getPatientName(), result.getPatientName());
            assertEquals(validPrescription.getPatientId(), result.getPatientId());
            assertEquals(validPrescription.getDoctorName(), result.getDoctorName());
            assertEquals(validPrescription.getMedicineId(), result.getMedicineId());
            assertEquals(validPrescription.getMedicineName(), result.getMedicineName());
            assertEquals(validPrescription.getQuantity(), result.getQuantity());
            assertEquals(validPrescription.getDosage(), result.getDosage());
            assertEquals(validPrescription.getNotes(), result.getNotes());
        }
    }
}

// Made with Bob
