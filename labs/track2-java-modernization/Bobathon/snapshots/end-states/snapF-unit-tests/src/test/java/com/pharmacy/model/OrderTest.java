package com.pharmacy.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    // ── constants ──────────────────────────────────────────────────────────
    private static final String ORDER_ID          = "ORD0001";
    private static final String PRESCRIPTION_ID   = "RX001";
    private static final String PATIENT_NAME      = "Jane Doe";
    private static final String PATIENT_ID        = "PAT001";
    private static final String MEDICINE_ID       = "MED001";
    private static final String MEDICINE_NAME     = "Amoxicillin";
    private static final int    QUANTITY          = 2;
    private static final BigDecimal TOTAL_AMOUNT  = new BigDecimal("25.98");
    private static final String PAYMENT_METHOD    = "CASH";
    private static final String PHARMACIST_NOTES  = "Take with food";

    // ── default constructor ────────────────────────────────────────────────

    @Test
    void defaultConstructor_noArgs_shouldProduceNullAndZeroFields() {
        // Arrange / Act
        Order order = new Order();

        // Assert
        assertNull(order.getId());
        assertNull(order.getPrescriptionId());
        assertNull(order.getPatientName());
        assertNull(order.getPatientId());
        assertNull(order.getMedicineId());
        assertNull(order.getMedicineName());
        assertEquals(0, order.getQuantity());
        assertNull(order.getTotalAmount());
        assertNull(order.getOrderDate());
        assertNull(order.getStatus());
        assertNull(order.getPaymentMethod());
        assertNull(order.getPharmacistNotes());
    }

    // ── all-args constructor ───────────────────────────────────────────────

    @Test
    void allArgsConstructor_validArgs_shouldStoreAllValues() {
        // Arrange
        Date orderDate = new Date();

        // Act
        Order order = new Order(ORDER_ID, PRESCRIPTION_ID, PATIENT_NAME, PATIENT_ID,
                MEDICINE_ID, MEDICINE_NAME, QUANTITY, TOTAL_AMOUNT,
                orderDate, "PENDING", PAYMENT_METHOD, PHARMACIST_NOTES);

        // Assert
        assertEquals(ORDER_ID,        order.getId());
        assertEquals(PRESCRIPTION_ID, order.getPrescriptionId());
        assertEquals(PATIENT_NAME,    order.getPatientName());
        assertEquals(PATIENT_ID,      order.getPatientId());
        assertEquals(MEDICINE_ID,     order.getMedicineId());
        assertEquals(MEDICINE_NAME,   order.getMedicineName());
        assertEquals(QUANTITY,        order.getQuantity());
        assertEquals(TOTAL_AMOUNT,    order.getTotalAmount());
        assertEquals(orderDate,       order.getOrderDate());
        assertEquals("PENDING",       order.getStatus());
        assertEquals(PAYMENT_METHOD,  order.getPaymentMethod());
        assertEquals(PHARMACIST_NOTES, order.getPharmacistNotes());
    }

    // ── getter / setter symmetry ───────────────────────────────────────────

    @Test
    void setId_anyValue_shouldBeReturnedByGetId() {
        Order order = new Order();
        order.setId(ORDER_ID);
        assertEquals(ORDER_ID, order.getId());
    }

    @Test
    void setPrescriptionId_anyValue_shouldBeReturnedByGetPrescriptionId() {
        Order order = new Order();
        order.setPrescriptionId(PRESCRIPTION_ID);
        assertEquals(PRESCRIPTION_ID, order.getPrescriptionId());
    }

    @Test
    void setPatientName_anyValue_shouldBeReturnedByGetPatientName() {
        Order order = new Order();
        order.setPatientName(PATIENT_NAME);
        assertEquals(PATIENT_NAME, order.getPatientName());
    }

    @Test
    void setPatientId_anyValue_shouldBeReturnedByGetPatientId() {
        Order order = new Order();
        order.setPatientId(PATIENT_ID);
        assertEquals(PATIENT_ID, order.getPatientId());
    }

    @Test
    void setMedicineId_anyValue_shouldBeReturnedByGetMedicineId() {
        Order order = new Order();
        order.setMedicineId(MEDICINE_ID);
        assertEquals(MEDICINE_ID, order.getMedicineId());
    }

    @Test
    void setMedicineName_anyValue_shouldBeReturnedByGetMedicineName() {
        Order order = new Order();
        order.setMedicineName(MEDICINE_NAME);
        assertEquals(MEDICINE_NAME, order.getMedicineName());
    }

    @Test
    void setQuantity_anyValue_shouldBeReturnedByGetQuantity() {
        Order order = new Order();
        order.setQuantity(QUANTITY);
        assertEquals(QUANTITY, order.getQuantity());
    }

    @Test
    void setTotalAmount_anyValue_shouldBeReturnedByGetTotalAmount() {
        Order order = new Order();
        order.setTotalAmount(TOTAL_AMOUNT);
        assertEquals(TOTAL_AMOUNT, order.getTotalAmount());
    }

    @Test
    void setOrderDate_anyValue_shouldBeReturnedByGetOrderDate() {
        Order order = new Order();
        Date date = new Date();
        order.setOrderDate(date);
        assertEquals(date, order.getOrderDate());
    }

    @Test
    void setPaymentMethod_anyValue_shouldBeReturnedByGetPaymentMethod() {
        Order order = new Order();
        order.setPaymentMethod(PAYMENT_METHOD);
        assertEquals(PAYMENT_METHOD, order.getPaymentMethod());
    }

    @Test
    void setPharmacistNotes_anyValue_shouldBeReturnedByGetPharmacistNotes() {
        Order order = new Order();
        order.setPharmacistNotes(PHARMACIST_NOTES);
        assertEquals(PHARMACIST_NOTES, order.getPharmacistNotes());
    }

    // ── status lifecycle values ────────────────────────────────────────────

    @Test
    void setStatus_pendingValue_shouldBeReturnedByGetStatus() {
        Order order = new Order();
        order.setStatus("PENDING");
        assertEquals("PENDING", order.getStatus());
    }

    @Test
    void setStatus_validatedValue_shouldBeReturnedByGetStatus() {
        Order order = new Order();
        order.setStatus("VALIDATED");
        assertEquals("VALIDATED", order.getStatus());
    }

    @Test
    void setStatus_paidValue_shouldBeReturnedByGetStatus() {
        Order order = new Order();
        order.setStatus("PAID");
        assertEquals("PAID", order.getStatus());
    }

    @Test
    void setStatus_collectedValue_shouldBeReturnedByGetStatus() {
        Order order = new Order();
        order.setStatus("COLLECTED");
        assertEquals("COLLECTED", order.getStatus());
    }

    @Test
    void setStatus_cancelledValue_shouldBeReturnedByGetStatus() {
        Order order = new Order();
        order.setStatus("CANCELLED");
        assertEquals("CANCELLED", order.getStatus());
    }

    // ── BigDecimal precision ───────────────────────────────────────────────

    @Test
    void setTotalAmount_highPrecisionValue_shouldRoundTripWithoutLoss() {
        // Arrange
        BigDecimal precise = new BigDecimal("99.999999999999");
        Order order = new Order();

        // Act
        order.setTotalAmount(precise);

        // Assert
        assertEquals(0, precise.compareTo(order.getTotalAmount()));
    }
}
