package com.pharmacy.repository;

import com.pharmacy.model.Prescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PrescriptionRepositoryTest {

    private static final String PATIENT_ID = "P99001";
    private static final String UNKNOWN_PATIENT_ID = "P_UNKNOWN";
    private static final String UNKNOWN_ID = "RX_UNKNOWN";

    private PrescriptionRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = PrescriptionRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        repo = PrescriptionRepository.getInstance();
    }

    // ── generateId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("generateId produces IDs matching the RXnnn format")
    void generateId_firstCall_returnsRxFormattedId() {
        // Arrange / Act
        String id = repo.generateId();

        // Assert
        assertThat(id).matches("RX\\d{3,}");
    }

    @Test
    @DisplayName("generateId produces unique, strictly incremented IDs on successive calls")
    void generateId_successiveCalls_producesDistinctIncrementedIds() {
        // Arrange / Act
        String first = repo.generateId();
        String second = repo.generateId();

        // Assert
        assertNotEquals(first, second);
        int firstNum = Integer.parseInt(first.substring(2));
        int secondNum = Integer.parseInt(second.substring(2));
        assertEquals(1, secondNum - firstNum);
    }

    // ── addPrescription / findById ─────────────────────────────────────────

    @Test
    @DisplayName("addPrescription then findById returns the stored prescription")
    void addPrescription_findById_existingId_returnsSameObject() {
        // Arrange
        Prescription rx = buildPrescription("RXTEST01", PATIENT_ID, "PENDING");

        // Act
        repo.addPrescription(rx);
        Prescription found = repo.findById("RXTEST01");

        // Assert
        assertNotNull(found);
        assertEquals("RXTEST01", found.getId());
        assertEquals(PATIENT_ID, found.getPatientId());
    }

    @Test
    @DisplayName("findById with unknown ID returns null")
    void findById_unknownId_returnsNull() {
        // Arrange / Act / Assert
        assertNull(repo.findById(UNKNOWN_ID));
    }

    // ── findByPatientId ────────────────────────────────────────────────────

    @Test
    @DisplayName("findByPatientId returns only prescriptions for the given patient")
    void findByPatientId_knownPatient_returnsOnlyMatchingPrescriptions() {
        // Arrange
        repo.addPrescription(buildPrescription("RXA01", PATIENT_ID, "PENDING"));
        repo.addPrescription(buildPrescription("RXA02", PATIENT_ID, "VALIDATED"));
        repo.addPrescription(buildPrescription("RXA03", "P_OTHER", "PENDING"));

        // Act
        List<Prescription> results = repo.findByPatientId(PATIENT_ID);

        // Assert
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(p -> PATIENT_ID.equals(p.getPatientId()));
    }

    @Test
    @DisplayName("findByPatientId with unknown patient ID returns empty list")
    void findByPatientId_unknownPatient_returnsEmptyList() {
        // Arrange / Act
        List<Prescription> results = repo.findByPatientId(UNKNOWN_PATIENT_ID);

        // Assert
        assertThat(results).isEmpty();
    }

    // ── findByStatus ───────────────────────────────────────────────────────

    @Test
    @DisplayName("findByStatus returns only prescriptions matching the requested status")
    void findByStatus_knownStatus_returnsMatchingPrescriptions() {
        // Arrange
        repo.addPrescription(buildPrescription("RXS01", PATIENT_ID, "EXPIRED"));
        repo.addPrescription(buildPrescription("RXS02", PATIENT_ID, "PENDING"));

        // Act
        List<Prescription> expired = repo.findByStatus("EXPIRED");

        // Assert
        assertThat(expired).isNotEmpty();
        assertThat(expired).allMatch(p -> "EXPIRED".equals(p.getStatus()));
    }

    @Test
    @DisplayName("findByStatus with a status that no prescription has returns empty list")
    void findByStatus_noMatchingStatus_returnsEmptyList() {
        // Arrange / Act
        // The singleton is reset so no prescriptions have status "COLLECTED"
        List<Prescription> results = repo.findByStatus("COLLECTED");

        // Assert
        assertThat(results).isEmpty();
    }

    // ── updatePrescription ─────────────────────────────────────────────────

    @Test
    @DisplayName("updatePrescription persists status change on re-fetch")
    void updatePrescription_statusChange_persistsOnReFetch() {
        // Arrange
        repo.addPrescription(buildPrescription("RXUPD01", PATIENT_ID, "PENDING"));
        Prescription updated = buildPrescription("RXUPD01", PATIENT_ID, "VALIDATED");

        // Act
        repo.updatePrescription(updated);
        Prescription fetched = repo.findById("RXUPD01");

        // Assert
        assertEquals("VALIDATED", fetched.getStatus());
    }

    // ── deletePrescription ─────────────────────────────────────────────────

    @Test
    @DisplayName("deletePrescription removes the entry so findById returns null")
    void deletePrescription_existingId_findByIdReturnsNull() {
        // Arrange
        repo.addPrescription(buildPrescription("RXDEL01", PATIENT_ID, "PENDING"));

        // Act
        repo.deletePrescription("RXDEL01");

        // Assert
        assertNull(repo.findById("RXDEL01"));
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Prescription buildPrescription(String id, String patientId, String status) {
        return new Prescription(
                id, "Test Patient", patientId, "Dr. Test",
                "MED001", "Amoxicillin", 10, "1 daily",
                new Date(), new Date(), status, "no notes"
        );
    }
}
