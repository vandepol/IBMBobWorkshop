package com.pharmacy.api;

import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardResourceTest {

    private static final String PRESCRIPTION_ID = "RX001";
    private static final String ORDER_ID        = "ORD5001";

    @Mock
    private PrescriptionRepository prescriptionRepo;

    @Mock
    private OrderRepository orderRepo;

    // -----------------------------------------------------------------------
    // Helper: build a minimal Prescription
    // -----------------------------------------------------------------------
    private Prescription buildPrescription(String id, String status) {
        return new Prescription(id, "John Smith", "P001", "Dr. A",
                "MED001", "Amoxicillin", 10, "1 daily",
                new Date(), new Date(), status, "");
    }

    // -----------------------------------------------------------------------
    // Helper: build a minimal Order
    // -----------------------------------------------------------------------
    private Order buildOrder(String id, String status) {
        return new Order(id, PRESCRIPTION_ID, "John Smith", "P001",
                "MED001", "Amoxicillin", 10, new BigDecimal("15.00"),
                new Date(), status, "CASH", "");
    }

    @BeforeEach
    void resetSingletons() throws Exception {
        // Reset PrescriptionRepository singleton
        java.lang.reflect.Field pField =
                PrescriptionRepository.class.getDeclaredField("instance");
        pField.setAccessible(true);
        pField.set(null, null);

        // Reset OrderRepository singleton
        java.lang.reflect.Field oField =
                OrderRepository.class.getDeclaredField("instance");
        oField.setAccessible(true);
        oField.set(null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getDashboardData_populatedRepos_returnsCorrectPendingAndTotalCounts() {
        // Arrange
        Prescription pending1 = buildPrescription("RX100", "PENDING");
        Prescription pending2 = buildPrescription("RX101", "PENDING");
        Prescription fulfilled = buildPrescription("RX102", "FULFILLED");

        Order pendingOrder = buildOrder("ORD5001", "PENDING");
        Order paidOrder    = buildOrder("ORD5002", "PAID");

        try (MockedStatic<PrescriptionRepository> pStatic =
                     mockStatic(PrescriptionRepository.class);
             MockedStatic<OrderRepository> oStatic =
                     mockStatic(OrderRepository.class)) {

            pStatic.when(PrescriptionRepository::getInstance).thenReturn(prescriptionRepo);
            oStatic.when(OrderRepository::getInstance).thenReturn(orderRepo);

            when(prescriptionRepo.findByStatus("PENDING")).thenReturn(List.of(pending1, pending2));
            when(prescriptionRepo.findAll()).thenReturn(List.of(pending1, pending2, fulfilled));
            when(orderRepo.findByStatus("PENDING")).thenReturn(List.of(pendingOrder));
            when(orderRepo.findAll()).thenReturn(List.of(pendingOrder, paidOrder));

            DashboardResource resource = new DashboardResource();

            // Act
            Map<String, Object> result = resource.getDashboardData();

            // Assert
            List<Prescription> pendingPrescriptions =
                    (List<Prescription>) result.get("pendingPrescriptions");
            List<Order> pendingOrders =
                    (List<Order>) result.get("pendingOrders");

            assertThat(pendingPrescriptions).hasSize(2);
            assertThat(pendingOrders).hasSize(1);
            assertThat(result.get("totalPrescriptions")).isEqualTo(3);
            assertThat(result.get("totalOrders")).isEqualTo(2);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void getDashboardData_emptyRepos_returnsZeroCounts() {
        // Arrange
        try (MockedStatic<PrescriptionRepository> pStatic =
                     mockStatic(PrescriptionRepository.class);
             MockedStatic<OrderRepository> oStatic =
                     mockStatic(OrderRepository.class)) {

            pStatic.when(PrescriptionRepository::getInstance).thenReturn(prescriptionRepo);
            oStatic.when(OrderRepository::getInstance).thenReturn(orderRepo);

            when(prescriptionRepo.findByStatus("PENDING")).thenReturn(List.of());
            when(prescriptionRepo.findAll()).thenReturn(List.of());
            when(orderRepo.findByStatus("PENDING")).thenReturn(List.of());
            when(orderRepo.findAll()).thenReturn(List.of());

            DashboardResource resource = new DashboardResource();

            // Act
            Map<String, Object> result = resource.getDashboardData();

            // Assert
            assertThat(((List<?>) result.get("pendingPrescriptions"))).isEmpty();
            assertThat(((List<?>) result.get("pendingOrders"))).isEmpty();
            assertThat(result.get("totalPrescriptions")).isEqualTo(0);
            assertThat(result.get("totalOrders")).isEqualTo(0);
        }
    }

    @Test
    void getDashboardData_allKeys_presentInResponse() {
        // Arrange
        try (MockedStatic<PrescriptionRepository> pStatic =
                     mockStatic(PrescriptionRepository.class);
             MockedStatic<OrderRepository> oStatic =
                     mockStatic(OrderRepository.class)) {

            pStatic.when(PrescriptionRepository::getInstance).thenReturn(prescriptionRepo);
            oStatic.when(OrderRepository::getInstance).thenReturn(orderRepo);

            when(prescriptionRepo.findByStatus("PENDING")).thenReturn(List.of());
            when(prescriptionRepo.findAll()).thenReturn(List.of());
            when(orderRepo.findByStatus("PENDING")).thenReturn(List.of());
            when(orderRepo.findAll()).thenReturn(List.of());

            DashboardResource resource = new DashboardResource();

            // Act
            Map<String, Object> result = resource.getDashboardData();

            // Assert
            assertThat(result).containsKeys(
                    "pendingPrescriptions",
                    "pendingOrders",
                    "totalPrescriptions",
                    "totalOrders");
        }
    }
}
