package com.pharmacy.repository;

import com.pharmacy.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {

    private static final String PATIENT_ID = "PAT001";
    private static final String PRESCRIPTION_ID = "RX001";
    private static final String UNKNOWN_ID = "ORD_UNKNOWN";

    private OrderRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = OrderRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        repo = OrderRepository.getInstance();
    }

    // ── generateId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("generateId produces IDs matching the ORDnnnn format")
    void generateId_firstCall_returnsOrdFormattedId() {
        // Arrange / Act
        String id = repo.generateId();

        // Assert
        assertThat(id).matches("ORD\\d{4,}");
    }

    @Test
    @DisplayName("generateId on successive calls produces distinct, incremented IDs")
    void generateId_successiveCalls_producesDistinctIncrementedIds() {
        // Arrange / Act
        String first = repo.generateId();
        String second = repo.generateId();

        // Assert
        assertNotEquals(first, second);
        int firstNum = Integer.parseInt(first.substring(3));
        int secondNum = Integer.parseInt(second.substring(3));
        assertEquals(1, secondNum - firstNum);
    }

    // ── addOrder / findById ────────────────────────────────────────────────

    @Test
    @DisplayName("addOrder then findById returns the stored order")
    void addOrder_findById_existingId_returnsSameObject() {
        // Arrange
        Order order = buildOrder("ORD9001", PATIENT_ID, PRESCRIPTION_ID, "PENDING");

        // Act
        repo.addOrder(order);
        Order found = repo.findById("ORD9001");

        // Assert
        assertNotNull(found);
        assertEquals("ORD9001", found.getId());
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
    @DisplayName("findByPatientId returns only orders belonging to the given patient")
    void findByPatientId_knownPatient_returnsOnlyMatchingOrders() {
        // Arrange
        repo.addOrder(buildOrder("ORD9101", PATIENT_ID, PRESCRIPTION_ID, "PENDING"));
        repo.addOrder(buildOrder("ORD9102", PATIENT_ID, PRESCRIPTION_ID, "PAID"));
        repo.addOrder(buildOrder("ORD9103", "OTHER_PAT", PRESCRIPTION_ID, "PENDING"));

        // Act
        List<Order> results = repo.findByPatientId(PATIENT_ID);

        // Assert
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(o -> PATIENT_ID.equals(o.getPatientId()));
    }

    @Test
    @DisplayName("findByPatientId with unknown patient returns empty list")
    void findByPatientId_unknownPatient_returnsEmptyList() {
        // Arrange / Act
        List<Order> results = repo.findByPatientId("UNKNOWN_PATIENT");

        // Assert
        assertThat(results).isEmpty();
    }

    // ── findByStatus ───────────────────────────────────────────────────────

    @Test
    @DisplayName("findByStatus returns only orders matching the requested status")
    void findByStatus_knownStatus_returnsMatchingOrders() {
        // Arrange
        repo.addOrder(buildOrder("ORD9201", PATIENT_ID, PRESCRIPTION_ID, "PAID"));
        repo.addOrder(buildOrder("ORD9202", PATIENT_ID, PRESCRIPTION_ID, "PENDING"));

        // Act
        List<Order> paid = repo.findByStatus("PAID");

        // Assert
        assertThat(paid).isNotEmpty();
        assertThat(paid).allMatch(o -> "PAID".equals(o.getStatus()));
    }

    @Test
    @DisplayName("findByStatus with unknown status returns empty list")
    void findByStatus_unknownStatus_returnsEmptyList() {
        // Arrange / Act
        List<Order> results = repo.findByStatus("UNKNOWN_STATUS");

        // Assert
        assertThat(results).isEmpty();
    }

    // ── findByPrescriptionId ───────────────────────────────────────────────

    @Test
    @DisplayName("findByPrescriptionId returns only orders linked to the given prescription")
    void findByPrescriptionId_knownPrescription_returnsMatchingOrders() {
        // Arrange
        repo.addOrder(buildOrder("ORD9301", PATIENT_ID, PRESCRIPTION_ID, "PENDING"));
        repo.addOrder(buildOrder("ORD9302", PATIENT_ID, "RX999", "PENDING"));

        // Act
        List<Order> results = repo.findByPrescriptionId(PRESCRIPTION_ID);

        // Assert
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(o -> PRESCRIPTION_ID.equals(o.getPrescriptionId()));
    }

    @Test
    @DisplayName("findByPrescriptionId with unknown prescription ID returns empty list")
    void findByPrescriptionId_unknownPrescriptionId_returnsEmptyList() {
        // Arrange / Act
        List<Order> results = repo.findByPrescriptionId("RX_DOES_NOT_EXIST");

        // Assert
        assertThat(results).isEmpty();
    }

    // ── updateOrder ────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateOrder persists status change on re-fetch")
    void updateOrder_statusChange_persistsOnReFetch() {
        // Arrange
        repo.addOrder(buildOrder("ORD9401", PATIENT_ID, PRESCRIPTION_ID, "PENDING"));
        Order updated = buildOrder("ORD9401", PATIENT_ID, PRESCRIPTION_ID, "PAID");

        // Act
        repo.updateOrder(updated);
        Order fetched = repo.findById("ORD9401");

        // Assert
        assertEquals("PAID", fetched.getStatus());
    }

    // ── deleteOrder ────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteOrder removes the entry so findById returns null")
    void deleteOrder_existingId_findByIdReturnsNull() {
        // Arrange
        repo.addOrder(buildOrder("ORD9501", PATIENT_ID, PRESCRIPTION_ID, "PENDING"));

        // Act
        repo.deleteOrder("ORD9501");

        // Assert
        assertNull(repo.findById("ORD9501"));
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Order buildOrder(String id, String patientId, String prescriptionId, String status) {
        return new Order(
                id, prescriptionId, "Test Patient", patientId,
                "MED001", "Amoxicillin", 10, new BigDecimal("15.99"),
                new Date(), status, "CASH", "no notes"
        );
    }
}
