package com.pharmacy.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PrescriptionTest {

    // ── constants ──────────────────────────────────────────────────────────
    private static final String PRESCRIPTION_ID = "RX001";
    private static final String PATIENT_NAME    = "John Smith";
    private static final String PATIENT_ID      = "PAT001";
    private static final String DOCTOR_NAME     = "Dr. Evans";
    private static final String MEDICINE_ID     = "MED001";
    private static final String MEDICINE_NAME   = "Amoxicillin";
    private static final int    QUANTITY        = 30;
    private static final String DOSAGE          = "500mg twice daily";
    private static final String NOTES           = "Take with food";

    // ── default constructor ────────────────────────────────────────────────

    @Test
    void defaultConstructor_noArgs_shouldProduceNullAndZeroFields() {
        // Arrange / Act
        Prescription prescription = new Prescription();

        // Assert
        assertNull(prescription.getId());
        assertNull(prescription.getPatientName());
        assertNull(prescription.getPatientId());
        assertNull(prescription.getDoctorName());
        assertNull(prescription.getMedicineId());
        assertNull(prescription.getMedicineName());
        assertEquals(0, prescription.getQuantity());
        assertNull(prescription.getDosage());
        assertNull(prescription.getPrescriptionDate());
        assertNull(prescription.getExpiryDate());
        assertNull(prescription.getStatus());
        assertNull(prescription.getNotes());
    }

    // ── all-args constructor ───────────────────────────────────────────────

    @Test
    void allArgsConstructor_validArgs_shouldStoreAllValues() {
        // Arrange
        Date prescriptionDate = new Date();
        Date expiryDate       = new Date(prescriptionDate.getTime() + 30L * 24 * 60 * 60 * 1000);

        // Act
        Prescription prescription = new Prescription(
                PRESCRIPTION_ID, PATIENT_NAME, PATIENT_ID, DOCTOR_NAME,
                MEDICINE_ID, MEDICINE_NAME, QUANTITY, DOSAGE,
                prescriptionDate, expiryDate, "PENDING", NOTES);

        // Assert
        assertEquals(PRESCRIPTION_ID, prescription.getId());
        assertEquals(PATIENT_NAME,    prescription.getPatientName());
        assertEquals(PATIENT_ID,      prescription.getPatientId());
        assertEquals(DOCTOR_NAME,     prescription.getDoctorName());
        assertEquals(MEDICINE_ID,     prescription.getMedicineId());
        assertEquals(MEDICINE_NAME,   prescription.getMedicineName());
        assertEquals(QUANTITY,        prescription.getQuantity());
        assertEquals(DOSAGE,          prescription.getDosage());
        assertEquals(prescriptionDate, prescription.getPrescriptionDate());
        assertEquals(expiryDate,      prescription.getExpiryDate());
        assertEquals("PENDING",       prescription.getStatus());
        assertEquals(NOTES,           prescription.getNotes());
    }

    // ── getter / setter symmetry ───────────────────────────────────────────

    @Test
    void setId_anyValue_shouldBeReturnedByGetId() {
        Prescription prescription = new Prescription();
        prescription.setId(PRESCRIPTION_ID);
        assertEquals(PRESCRIPTION_ID, prescription.getId());
    }

    @Test
    void setPatientName_anyValue_shouldBeReturnedByGetPatientName() {
        Prescription prescription = new Prescription();
        prescription.setPatientName(PATIENT_NAME);
        assertEquals(PATIENT_NAME, prescription.getPatientName());
    }

    @Test
    void setPatientId_anyValue_shouldBeReturnedByGetPatientId() {
        Prescription prescription = new Prescription();
        prescription.setPatientId(PATIENT_ID);
        assertEquals(PATIENT_ID, prescription.getPatientId());
    }

    @Test
    void setDoctorName_anyValue_shouldBeReturnedByGetDoctorName() {
        Prescription prescription = new Prescription();
        prescription.setDoctorName(DOCTOR_NAME);
        assertEquals(DOCTOR_NAME, prescription.getDoctorName());
    }

    @Test
    void setMedicineId_anyValue_shouldBeReturnedByGetMedicineId() {
        Prescription prescription = new Prescription();
        prescription.setMedicineId(MEDICINE_ID);
        assertEquals(MEDICINE_ID, prescription.getMedicineId());
    }

    @Test
    void setMedicineName_anyValue_shouldBeReturnedByGetMedicineName() {
        Prescription prescription = new Prescription();
        prescription.setMedicineName(MEDICINE_NAME);
        assertEquals(MEDICINE_NAME, prescription.getMedicineName());
    }

    @Test
    void setQuantity_anyValue_shouldBeReturnedByGetQuantity() {
        Prescription prescription = new Prescription();
        prescription.setQuantity(QUANTITY);
        assertEquals(QUANTITY, prescription.getQuantity());
    }

    @Test
    void setDosage_anyValue_shouldBeReturnedByGetDosage() {
        Prescription prescription = new Prescription();
        prescription.setDosage(DOSAGE);
        assertEquals(DOSAGE, prescription.getDosage());
    }

    @Test
    void setNotes_anyValue_shouldBeReturnedByGetNotes() {
        Prescription prescription = new Prescription();
        prescription.setNotes(NOTES);
        assertEquals(NOTES, prescription.getNotes());
    }

    // ── status lifecycle values ────────────────────────────────────────────

    @Test
    void setStatus_pendingValue_shouldBeReturnedByGetStatus() {
        Prescription prescription = new Prescription();
        prescription.setStatus("PENDING");
        assertEquals("PENDING", prescription.getStatus());
    }

    @Test
    void setStatus_validatedValue_shouldBeReturnedByGetStatus() {
        Prescription prescription = new Prescription();
        prescription.setStatus("VALIDATED");
        assertEquals("VALIDATED", prescription.getStatus());
    }

    @Test
    void setStatus_fulfilledValue_shouldBeReturnedByGetStatus() {
        Prescription prescription = new Prescription();
        prescription.setStatus("FULFILLED");
        assertEquals("FULFILLED", prescription.getStatus());
    }

    @Test
    void setStatus_expiredValue_shouldBeReturnedByGetStatus() {
        Prescription prescription = new Prescription();
        prescription.setStatus("EXPIRED");
        assertEquals("EXPIRED", prescription.getStatus());
    }

    // ── date field independence ────────────────────────────────────────────

    @Test
    void setPrescriptionDate_anyValue_shouldBeReturnedByGetPrescriptionDate() {
        Prescription prescription = new Prescription();
        Date prescriptionDate = new Date();
        prescription.setPrescriptionDate(prescriptionDate);
        assertEquals(prescriptionDate, prescription.getPrescriptionDate());
    }

    @Test
    void setExpiryDate_anyValue_shouldBeReturnedByGetExpiryDate() {
        Prescription prescription = new Prescription();
        Date expiryDate = new Date();
        prescription.setExpiryDate(expiryDate);
        assertEquals(expiryDate, prescription.getExpiryDate());
    }

    @Test
    void prescriptionDateAndExpiryDate_setIndependently_shouldBeStoredIndependently() {
        // Arrange
        Prescription prescription = new Prescription();
        Date prescriptionDate = new Date(1_000_000L);
        Date expiryDate       = new Date(2_000_000L);

        // Act
        prescription.setPrescriptionDate(prescriptionDate);
        prescription.setExpiryDate(expiryDate);

        // Assert
        assertNotEquals(prescription.getPrescriptionDate(), prescription.getExpiryDate());
        assertEquals(prescriptionDate, prescription.getPrescriptionDate());
        assertEquals(expiryDate,       prescription.getExpiryDate());
    }
}
