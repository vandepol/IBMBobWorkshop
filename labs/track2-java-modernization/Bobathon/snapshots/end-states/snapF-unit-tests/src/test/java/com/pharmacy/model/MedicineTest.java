package com.pharmacy.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MedicineTest {

    // ── constants ──────────────────────────────────────────────────────────
    private static final String MEDICINE_ID    = "MED001";
    private static final String MEDICINE_NAME  = "Amoxicillin";
    private static final String DESCRIPTION    = "Antibiotic";
    private static final BigDecimal PRICE      = new BigDecimal("12.99");
    private static final int STOCK             = 100;
    private static final String MANUFACTURER   = "Pfizer";

    // ── default constructor ────────────────────────────────────────────────

    @Test
    void defaultConstructor_noArgs_shouldProduceNullAndZeroFields() {
        // Arrange / Act
        Medicine medicine = new Medicine();

        // Assert
        assertNull(medicine.getId());
        assertNull(medicine.getName());
        assertNull(medicine.getDescription());
        assertNull(medicine.getPrice());
        assertEquals(0, medicine.getStockQuantity());
        assertNull(medicine.getManufacturer());
    }

    // ── all-args constructor ───────────────────────────────────────────────

    @Test
    void allArgsConstructor_validArgs_shouldStoreAllValues() {
        // Arrange / Act
        Medicine medicine = new Medicine(MEDICINE_ID, MEDICINE_NAME, DESCRIPTION, PRICE, STOCK, MANUFACTURER);

        // Assert
        assertEquals(MEDICINE_ID,   medicine.getId());
        assertEquals(MEDICINE_NAME, medicine.getName());
        assertEquals(DESCRIPTION,   medicine.getDescription());
        assertEquals(PRICE,         medicine.getPrice());
        assertEquals(STOCK,         medicine.getStockQuantity());
        assertEquals(MANUFACTURER,  medicine.getManufacturer());
    }

    // ── getter / setter symmetry ───────────────────────────────────────────

    @Test
    void setId_anyValue_shouldBeReturnedByGetId() {
        Medicine medicine = new Medicine();
        medicine.setId(MEDICINE_ID);
        assertEquals(MEDICINE_ID, medicine.getId());
    }

    @Test
    void setName_anyValue_shouldBeReturnedByGetName() {
        Medicine medicine = new Medicine();
        medicine.setName(MEDICINE_NAME);
        assertEquals(MEDICINE_NAME, medicine.getName());
    }

    @Test
    void setDescription_anyValue_shouldBeReturnedByGetDescription() {
        Medicine medicine = new Medicine();
        medicine.setDescription(DESCRIPTION);
        assertEquals(DESCRIPTION, medicine.getDescription());
    }

    @Test
    void setPrice_anyValue_shouldBeReturnedByGetPrice() {
        Medicine medicine = new Medicine();
        medicine.setPrice(PRICE);
        assertEquals(PRICE, medicine.getPrice());
    }

    @Test
    void setStockQuantity_anyValue_shouldBeReturnedByGetStockQuantity() {
        Medicine medicine = new Medicine();
        medicine.setStockQuantity(STOCK);
        assertEquals(STOCK, medicine.getStockQuantity());
    }

    @Test
    void setManufacturer_anyValue_shouldBeReturnedByGetManufacturer() {
        Medicine medicine = new Medicine();
        medicine.setManufacturer(MANUFACTURER);
        assertEquals(MANUFACTURER, medicine.getManufacturer());
    }

    // ── BigDecimal precision ───────────────────────────────────────────────

    @Test
    void setPrice_highPrecisionBigDecimal_shouldRoundTripWithoutLoss() {
        // Arrange
        BigDecimal highPrecision = new BigDecimal("9.999999999999999");
        Medicine medicine = new Medicine();

        // Act
        medicine.setPrice(highPrecision);

        // Assert
        assertEquals(0, highPrecision.compareTo(medicine.getPrice()));
    }

    @Test
    void setPrice_zeroBigDecimal_shouldReturnZero() {
        Medicine medicine = new Medicine();
        medicine.setPrice(BigDecimal.ZERO);
        assertEquals(0, BigDecimal.ZERO.compareTo(medicine.getPrice()));
    }
}
