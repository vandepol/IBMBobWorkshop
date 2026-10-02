package com.pharmacy.api;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.PrescriptionRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionResourceTest {

    private static final String PRESCRIPTION_ID = "RX001";
    private static final String MEDICINE_ID     = "MED001";
    private static final String PATIENT_NAME    = "John Smith";
    private static final String PATIENT_ID      = "P12345";
    private static final String DOCTOR_NAME     = "Dr. Sarah Johnson";

    private PrescriptionRepository mockPrescriptionRepo;
    private MedicineRepository     mockMedicineRepo;

    @BeforeEach
    void setUp() {
        mockPrescriptionRepo = mock(PrescriptionRepository.class);
        mockMedicineRepo     = mock(MedicineRepository.class);
    }

    // -------------------------------------------------------------------------
    // Helper: build the resource wiring both static factory stubs
    // -------------------------------------------------------------------------
    private PrescriptionResource buildResource(
            MockedStatic<PrescriptionRepository> prescStatic,
            MockedStatic<MedicineRepository> medStatic) {
        prescStatic.when(PrescriptionRepository::getInstance).thenReturn(mockPrescriptionRepo);
        medStatic.when(MedicineRepository::getInstance).thenReturn(mockMedicineRepo);
        return new PrescriptionResource();
    }

    // -------------------------------------------------------------------------
    // getAllPrescriptions
    // -------------------------------------------------------------------------

    @Test
    void getAllPrescriptions_repoReturnsList_returns200WithList() {
        Prescription p = buildPrescription(PRESCRIPTION_ID, "PENDING");
        List<Prescription> prescriptions = List.of(p);

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockPrescriptionRepo.findAll()).thenReturn(prescriptions);

            // Act
            List<Prescription> result = resource.getAllPrescriptions();

            // Assert
            assertEquals(1, result.size());
            assertEquals(PRESCRIPTION_ID, result.get(0).getId());
        }
    }

    // -------------------------------------------------------------------------
    // getPrescriptionById
    // -------------------------------------------------------------------------

    @Test
    void getPrescriptionById_existingId_returns200WithEntity() {
        Prescription p = buildPrescription(PRESCRIPTION_ID, "PENDING");

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockPrescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(p);

            // Act
            Response response = resource.getPrescriptionById(PRESCRIPTION_ID);

            // Assert
            assertEquals(200, response.getStatus());
            assertSame(p, response.getEntity());
        }
    }

    @Test
    void getPrescriptionById_unknownId_returns404() {
        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockPrescriptionRepo.findById("MISSING")).thenReturn(null);

            // Act
            Response response = resource.getPrescriptionById("MISSING");

            // Assert
            assertEquals(404, response.getStatus());
        }
    }

    // -------------------------------------------------------------------------
    // createPrescription
    // -------------------------------------------------------------------------

    @Test
    void createPrescription_validDataKnownMedicine_returns201WithPendingStatus() {
        Medicine med = new Medicine(MEDICINE_ID, "Amoxicillin", "", new BigDecimal("10.00"), 50, "Lab");

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockMedicineRepo.findById(MEDICINE_ID)).thenReturn(med);
            when(mockPrescriptionRepo.generateId()).thenReturn("RX1001");

            // Act
            Response response = resource.createPrescription(validPrescriptionData());

            // Assert
            assertEquals(201, response.getStatus());
            Prescription created = (Prescription) response.getEntity();
            assertEquals("PENDING", created.getStatus());
            assertEquals("RX1001", created.getId());
            assertEquals(MEDICINE_ID, created.getMedicineId());
            verify(mockPrescriptionRepo).addPrescription(created);
        }
    }

    @Test
    void createPrescription_unknownMedicineId_returns400() {
        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockMedicineRepo.findById(MEDICINE_ID)).thenReturn(null);

            // Act
            Response response = resource.createPrescription(validPrescriptionData());

            // Assert
            assertEquals(400, response.getStatus());
        }
    }

    @Test
    void createPrescription_missingRequiredField_returns400() {
        // quantity key is absent → NPE on ((Number) null).intValue()
        Map<String, Object> badData = new HashMap<>(validPrescriptionData());
        badData.remove("quantity");

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            Medicine med = new Medicine(MEDICINE_ID, "Amoxicillin", "", new BigDecimal("10.00"), 50, "Lab");
            when(mockMedicineRepo.findById(MEDICINE_ID)).thenReturn(med);
            when(mockPrescriptionRepo.generateId()).thenReturn("RX1002");

            // Act
            Response response = resource.createPrescription(badData);

            // Assert
            assertEquals(400, response.getStatus());
        }
    }

    @Test
    void createPrescription_expiryDateIsApproximately30DaysAfterToday() {
        Medicine med = new Medicine(MEDICINE_ID, "Amoxicillin", "", new BigDecimal("10.00"), 50, "Lab");

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockMedicineRepo.findById(MEDICINE_ID)).thenReturn(med);
            when(mockPrescriptionRepo.generateId()).thenReturn("RX1003");

            // Act
            Response response = resource.createPrescription(validPrescriptionData());

            // Assert
            assertEquals(201, response.getStatus());
            Prescription created = (Prescription) response.getEntity();
            long diffMs = created.getExpiryDate().getTime() - created.getPrescriptionDate().getTime();
            long diffDays = TimeUnit.MILLISECONDS.toDays(diffMs);
            assertTrue(diffDays >= 29, "Expiry should be at least 29 days after prescription date, got: " + diffDays);
        }
    }

    // -------------------------------------------------------------------------
    // validatePrescription
    // -------------------------------------------------------------------------

    @Test
    void validatePrescription_pendingPrescription_returns200WithValidatedStatus() {
        Prescription p = buildPrescription(PRESCRIPTION_ID, "PENDING");

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockPrescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(p);

            // Act
            Response response = resource.validatePrescription(PRESCRIPTION_ID);

            // Assert
            assertEquals(200, response.getStatus());
            Prescription result = (Prescription) response.getEntity();
            assertEquals("VALIDATED", result.getStatus());
            verify(mockPrescriptionRepo).updatePrescription(p);
        }
    }

    @Test
    void validatePrescription_notFound_returns404() {
        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockPrescriptionRepo.findById("MISSING")).thenReturn(null);

            // Act
            Response response = resource.validatePrescription("MISSING");

            // Assert
            assertEquals(404, response.getStatus());
        }
    }

    @Test
    void validatePrescription_statusNotPending_returns400() {
        Prescription p = buildPrescription(PRESCRIPTION_ID, "FULFILLED");

        try (MockedStatic<PrescriptionRepository> ps = mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> ms = mockStatic(MedicineRepository.class)) {

            PrescriptionResource resource = buildResource(ps, ms);
            when(mockPrescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(p);

            // Act
            Response response = resource.validatePrescription(PRESCRIPTION_ID);

            // Assert
            assertEquals(400, response.getStatus());
            verify(mockPrescriptionRepo, never()).updatePrescription(any());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Map<String, Object> validPrescriptionData() {
        Map<String, Object> data = new HashMap<>();
        data.put("medicineId",   MEDICINE_ID);
        data.put("patientName",  PATIENT_NAME);
        data.put("patientId",    PATIENT_ID);
        data.put("doctorName",   DOCTOR_NAME);
        data.put("quantity",     2);
        data.put("dosage",       "1 tablet twice daily");
        data.put("notes",        "Take with food");
        return data;
    }

    private Prescription buildPrescription(String id, String status) {
        return new Prescription(
            id, PATIENT_NAME, PATIENT_ID, DOCTOR_NAME,
            MEDICINE_ID, "Amoxicillin 500mg", 30,
            "1 tablet twice daily", new Date(), new Date(), status, ""
        );
    }
}
