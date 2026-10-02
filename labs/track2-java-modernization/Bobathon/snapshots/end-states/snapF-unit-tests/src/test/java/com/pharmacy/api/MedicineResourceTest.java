package com.pharmacy.api;

import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineResourceTest {

    private static final String MEDICINE_ID = "MED001";

    @Mock
    private MedicineRepository medicineRepo;

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------
    private Medicine buildMedicine(String id) {
        return new Medicine(id, "Amoxicillin 500mg", "Antibiotic",
                new BigDecimal("15.99"), 100, "PharmaCorp");
    }

    // -----------------------------------------------------------------------
    // getAllMedicines
    // -----------------------------------------------------------------------

    @Test
    void getAllMedicines_repoReturnsList_returns200WithList() {
        // Arrange
        List<Medicine> medicines = List.of(buildMedicine(MEDICINE_ID));

        try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
            mock.when(MedicineRepository::getInstance).thenReturn(medicineRepo);
            when(medicineRepo.findAll()).thenReturn(medicines);

            MedicineResource resource = new MedicineResource();

            // Act
            List<Medicine> result = resource.getAllMedicines();

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(MEDICINE_ID);
        }
    }

    // -----------------------------------------------------------------------
    // getMedicineById
    // -----------------------------------------------------------------------

    @Test
    void getMedicineById_existingId_returns200WithMedicine() {
        // Arrange
        Medicine medicine = buildMedicine(MEDICINE_ID);

        try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
            mock.when(MedicineRepository::getInstance).thenReturn(medicineRepo);
            when(medicineRepo.findById(MEDICINE_ID)).thenReturn(medicine);

            MedicineResource resource = new MedicineResource();

            // Act
            Response response = resource.getMedicineById(MEDICINE_ID);

            // Assert
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getEntity()).isEqualTo(medicine);
        }
    }

    @Test
    void getMedicineById_unknownId_returns404() {
        // Arrange
        try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
            mock.when(MedicineRepository::getInstance).thenReturn(medicineRepo);
            when(medicineRepo.findById("MISSING")).thenReturn(null);

            MedicineResource resource = new MedicineResource();

            // Act
            Response response = resource.getMedicineById("MISSING");

            // Assert
            assertThat(response.getStatus()).isEqualTo(404);
        }
    }

    // -----------------------------------------------------------------------
    // searchMedicines
    // -----------------------------------------------------------------------

    @Test
    void searchMedicines_nonBlankName_returnsMedicinesFromSearchByName() {
        // Arrange
        Medicine medicine = buildMedicine(MEDICINE_ID);
        List<Medicine> filtered = List.of(medicine);

        try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
            mock.when(MedicineRepository::getInstance).thenReturn(medicineRepo);
            when(medicineRepo.searchByName("Amox")).thenReturn(filtered);

            MedicineResource resource = new MedicineResource();

            // Act
            List<Medicine> result = resource.searchMedicines("Amox");

            // Assert
            assertThat(result).isEqualTo(filtered);
            verify(medicineRepo).searchByName("Amox");
            verify(medicineRepo, never()).findAll();
        }
    }

    @Test
    void searchMedicines_nullName_returnsFindAllList() {
        // Arrange
        List<Medicine> all = List.of(buildMedicine(MEDICINE_ID));

        try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
            mock.when(MedicineRepository::getInstance).thenReturn(medicineRepo);
            when(medicineRepo.findAll()).thenReturn(all);

            MedicineResource resource = new MedicineResource();

            // Act
            List<Medicine> result = resource.searchMedicines(null);

            // Assert
            assertThat(result).isEqualTo(all);
            verify(medicineRepo, never()).searchByName(any());
        }
    }

    @Test
    void searchMedicines_blankName_returnsFindAllList() {
        // Arrange
        List<Medicine> all = List.of(buildMedicine(MEDICINE_ID));

        try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
            mock.when(MedicineRepository::getInstance).thenReturn(medicineRepo);
            when(medicineRepo.findAll()).thenReturn(all);

            MedicineResource resource = new MedicineResource();

            // Act
            List<Medicine> result = resource.searchMedicines("   ");

            // Assert
            assertThat(result).isEqualTo(all);
            verify(medicineRepo, never()).searchByName(any());
        }
    }
}
