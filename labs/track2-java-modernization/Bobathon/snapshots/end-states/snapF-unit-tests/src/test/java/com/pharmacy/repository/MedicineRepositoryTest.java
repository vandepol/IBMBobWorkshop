package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MedicineRepositoryTest {

    private static final String MED_ID = "T001";
    private static final String UNKNOWN_ID = "DOES_NOT_EXIST";

    private MedicineRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        Field instance = MedicineRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        repo = MedicineRepository.getInstance();
    }

    // ── addMedicine / findById ─────────────────────────────────────────────

    @Test
    @DisplayName("addMedicine then findById with the same ID returns the stored medicine")
    void addMedicine_findById_existingId_returnsSameObject() {
        // Arrange
        Medicine med = new Medicine(MED_ID, "Aspirin", "Pain reliever", new BigDecimal("5.99"), 100, "LabA");

        // Act
        repo.addMedicine(med);
        Medicine found = repo.findById(MED_ID);

        // Assert
        assertNotNull(found);
        assertEquals(MED_ID, found.getId());
        assertEquals("Aspirin", found.getName());
    }

    @Test
    @DisplayName("findById with unknown ID returns null")
    void findById_unknownId_returnsNull() {
        // Arrange / Act / Assert
        assertNull(repo.findById(UNKNOWN_ID));
    }

    // ── findAll ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll returns all added medicines")
    void findAll_afterAddingMedicines_returnsAllEntries() {
        // Arrange
        int initialSize = repo.findAll().size(); // sample data already populated
        repo.addMedicine(new Medicine("X001", "Med X", "", new BigDecimal("1.00"), 10, "Lab"));
        repo.addMedicine(new Medicine("X002", "Med Y", "", new BigDecimal("2.00"), 20, "Lab"));

        // Act
        List<Medicine> all = repo.findAll();

        // Assert
        assertEquals(initialSize + 2, all.size());
    }

    @Test
    @DisplayName("findAll returns a defensive copy — mutations do not affect the store")
    void findAll_mutatingReturnedList_doesNotAffectStore() {
        // Arrange
        int initialSize = repo.findAll().size();

        // Act
        List<Medicine> list = repo.findAll();
        list.clear();

        // Assert
        assertEquals(initialSize, repo.findAll().size());
    }

    // ── searchByName ───────────────────────────────────────────────────────

    @Test
    @DisplayName("searchByName with case-insensitive substring returns matching medicines")
    void searchByName_caseInsensitiveSubstring_returnsMatches() {
        // Arrange
        repo.addMedicine(new Medicine("S001", "Paracetamol 500mg", "", new BigDecimal("3.50"), 50, "Lab"));

        // Act
        List<Medicine> results = repo.searchByName("PARACETAMOL");

        // Assert
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(m -> m.getName().toLowerCase().contains("paracetamol"));
    }

    @Test
    @DisplayName("searchByName with no match returns empty list")
    void searchByName_noMatch_returnsEmptyList() {
        // Arrange / Act
        List<Medicine> results = repo.searchByName("zzz_nonexistent_zzz");

        // Assert
        assertThat(results).isEmpty();
    }

    // ── updateMedicine ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateMedicine persists new field values on subsequent findById")
    void updateMedicine_updatedFields_reflectedOnFetch() {
        // Arrange
        repo.addMedicine(new Medicine(MED_ID, "OldName", "desc", new BigDecimal("10.00"), 30, "Lab"));
        Medicine updated = new Medicine(MED_ID, "NewName", "desc", new BigDecimal("12.00"), 30, "Lab");

        // Act
        repo.updateMedicine(updated);
        Medicine fetched = repo.findById(MED_ID);

        // Assert
        assertEquals("NewName", fetched.getName());
        assertEquals(new BigDecimal("12.00"), fetched.getPrice());
    }

    // ── deleteMedicine ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteMedicine removes the entry so findById returns null")
    void deleteMedicine_existingId_findByIdReturnsNull() {
        // Arrange
        repo.addMedicine(new Medicine(MED_ID, "ToDelete", "", new BigDecimal("5.00"), 10, "Lab"));
        int sizeBefore = repo.findAll().size();

        // Act
        repo.deleteMedicine(MED_ID);

        // Assert
        assertNull(repo.findById(MED_ID));
        assertEquals(sizeBefore - 1, repo.findAll().size());
    }

    // ── updateStock ────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStock with sufficient quantity decrements stock and returns true")
    void updateStock_sufficientQuantity_returnsTrueAndDecrements() {
        // Arrange
        repo.addMedicine(new Medicine(MED_ID, "StockMed", "", new BigDecimal("10.00"), 50, "Lab"));

        // Act
        boolean result = repo.updateStock(MED_ID, 20);

        // Assert
        assertTrue(result);
        assertEquals(30, repo.findById(MED_ID).getStockQuantity());
    }

    @Test
    @DisplayName("updateStock with quantity equal to stock succeeds and leaves zero stock")
    void updateStock_exactStock_returnsTrueAndLeavesZero() {
        // Arrange
        repo.addMedicine(new Medicine(MED_ID, "StockMed", "", new BigDecimal("10.00"), 10, "Lab"));

        // Act
        boolean result = repo.updateStock(MED_ID, 10);

        // Assert
        assertTrue(result);
        assertEquals(0, repo.findById(MED_ID).getStockQuantity());
    }

    @Test
    @DisplayName("updateStock with quantity exceeding stock returns false and leaves stock unchanged")
    void updateStock_insufficientStock_returnsFalse() {
        // Arrange
        repo.addMedicine(new Medicine(MED_ID, "StockMed", "", new BigDecimal("10.00"), 5, "Lab"));

        // Act
        boolean result = repo.updateStock(MED_ID, 10);

        // Assert
        assertFalse(result);
        assertEquals(5, repo.findById(MED_ID).getStockQuantity());
    }

    @Test
    @DisplayName("updateStock on unknown medicine ID returns false")
    void updateStock_unknownMedicineId_returnsFalse() {
        // Arrange / Act
        boolean result = repo.updateStock(UNKNOWN_ID, 1);

        // Assert
        assertFalse(result);
    }
}
