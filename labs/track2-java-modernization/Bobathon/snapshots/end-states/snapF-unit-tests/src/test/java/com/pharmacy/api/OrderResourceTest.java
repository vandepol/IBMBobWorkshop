package com.pharmacy.api;

import com.pharmacy.model.Medicine;
import com.pharmacy.model.Order;
import com.pharmacy.model.Prescription;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.PrescriptionRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResourceTest {

    private static final String ORDER_ID        = "ORD5001";
    private static final String PRESCRIPTION_ID = "RX001";
    private static final String MEDICINE_ID     = "MED001";
    private static final String PATIENT_ID      = "P001";
    private static final String PATIENT_NAME    = "John Smith";

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private PrescriptionRepository prescriptionRepo;

    @Mock
    private MedicineRepository medicineRepo;

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Medicine buildMedicine(int stock) {
        return new Medicine(MEDICINE_ID, "Amoxicillin 500mg", "Antibiotic",
                new BigDecimal("15.99"), stock, "PharmaCorp");
    }

    private Prescription buildPrescription(String status, int quantity) {
        return new Prescription(PRESCRIPTION_ID, PATIENT_NAME, PATIENT_ID,
                "Dr. A", MEDICINE_ID, "Amoxicillin 500mg", quantity,
                "1 daily", new Date(), new Date(), status, "");
    }

    private Order buildOrder(String status) {
        return new Order(ORDER_ID, PRESCRIPTION_ID, PATIENT_NAME, PATIENT_ID,
                MEDICINE_ID, "Amoxicillin 500mg", 10,
                new BigDecimal("159.90"), new Date(), status, "CASH", "");
    }

    /** Open all three MockedStatic resources and pass the configured resource to a lambda. */
    private void withMocks(java.util.function.Consumer<OrderResource> test) {
        try (MockedStatic<OrderRepository> oStatic =
                     mockStatic(OrderRepository.class);
             MockedStatic<PrescriptionRepository> pStatic =
                     mockStatic(PrescriptionRepository.class);
             MockedStatic<MedicineRepository> mStatic =
                     mockStatic(MedicineRepository.class)) {

            oStatic.when(OrderRepository::getInstance).thenReturn(orderRepo);
            pStatic.when(PrescriptionRepository::getInstance).thenReturn(prescriptionRepo);
            mStatic.when(MedicineRepository::getInstance).thenReturn(medicineRepo);

            test.accept(new OrderResource());
        }
    }

    // -----------------------------------------------------------------------
    // getAllOrders
    // -----------------------------------------------------------------------

    @Test
    void getAllOrders_repoReturnsList_returns200WithList() {
        // Arrange
        List<Order> orders = List.of(buildOrder("PENDING"));

        withMocks(resource -> {
            when(orderRepo.findAll()).thenReturn(orders);

            // Act
            List<Order> result = resource.getAllOrders();

            // Assert
            assertThat(result).hasSize(1);
        });
    }

    // -----------------------------------------------------------------------
    // getOrderById
    // -----------------------------------------------------------------------

    @Test
    void getOrderById_existingId_returns200WithOrder() {
        // Arrange
        Order order = buildOrder("PENDING");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            Response response = resource.getOrderById(ORDER_ID);

            // Assert
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getEntity()).isEqualTo(order);
        });
    }

    @Test
    void getOrderById_unknownId_returns404() {
        withMocks(resource -> {
            when(orderRepo.findById("MISSING")).thenReturn(null);

            // Act
            Response response = resource.getOrderById("MISSING");

            // Assert
            assertThat(response.getStatus()).isEqualTo(404);
        });
    }

    // -----------------------------------------------------------------------
    // createOrderFromPrescription
    // -----------------------------------------------------------------------

    @Test
    void createOrderFromPrescription_validPrescriptionAndStock_returns201WithOrder() {
        // Arrange
        Prescription prescription = buildPrescription("VALIDATED", 10);
        Medicine medicine = buildMedicine(50);

        Map<String, String> body = new HashMap<>();
        body.put("prescriptionId", PRESCRIPTION_ID);
        body.put("paymentMethod", "CASH");

        withMocks(resource -> {
            when(prescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(prescription);
            when(medicineRepo.findById(MEDICINE_ID)).thenReturn(medicine);
            when(orderRepo.generateId()).thenReturn(ORDER_ID);

            // Act
            Response response = resource.createOrderFromPrescription(body);

            // Assert
            assertThat(response.getStatus()).isEqualTo(201);
            Order created = (Order) response.getEntity();
            assertThat(created.getPrescriptionId()).isEqualTo(PRESCRIPTION_ID);
            assertThat(created.getStatus()).isEqualTo("PENDING");
        });
    }

    @Test
    void createOrderFromPrescription_validPrescription_prescriptionStatusBecomesFullfilled() {
        // Arrange
        Prescription prescription = buildPrescription("VALIDATED", 10);
        Medicine medicine = buildMedicine(50);

        Map<String, String> body = new HashMap<>();
        body.put("prescriptionId", PRESCRIPTION_ID);

        withMocks(resource -> {
            when(prescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(prescription);
            when(medicineRepo.findById(MEDICINE_ID)).thenReturn(medicine);
            when(orderRepo.generateId()).thenReturn(ORDER_ID);

            // Act
            resource.createOrderFromPrescription(body);

            // Assert
            ArgumentCaptor<Prescription> captor = ArgumentCaptor.forClass(Prescription.class);
            verify(prescriptionRepo).updatePrescription(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("FULFILLED");
        });
    }

    @Test
    void createOrderFromPrescription_validData_totalEqualsUnitPriceTimesQuantity() {
        // Arrange – price £15.99, qty 10 → total £159.90
        Prescription prescription = buildPrescription("VALIDATED", 10);
        Medicine medicine = buildMedicine(50);

        Map<String, String> body = new HashMap<>();
        body.put("prescriptionId", PRESCRIPTION_ID);

        withMocks(resource -> {
            when(prescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(prescription);
            when(medicineRepo.findById(MEDICINE_ID)).thenReturn(medicine);
            when(orderRepo.generateId()).thenReturn(ORDER_ID);

            // Act
            Response response = resource.createOrderFromPrescription(body);

            // Assert
            Order created = (Order) response.getEntity();
            BigDecimal expected = new BigDecimal("15.99").multiply(new BigDecimal(10));
            assertThat(created.getTotalAmount()).isEqualByComparingTo(expected);
        });
    }

    @Test
    void createOrderFromPrescription_prescriptionNotFound_returns404() {
        // Arrange
        Map<String, String> body = Map.of("prescriptionId", "MISSING");

        withMocks(resource -> {
            when(prescriptionRepo.findById("MISSING")).thenReturn(null);

            // Act
            Response response = resource.createOrderFromPrescription(body);

            // Assert
            assertThat(response.getStatus()).isEqualTo(404);
        });
    }

    @Test
    void createOrderFromPrescription_prescriptionNotValidated_returns400() {
        // Arrange
        Prescription prescription = buildPrescription("PENDING", 10);

        Map<String, String> body = Map.of("prescriptionId", PRESCRIPTION_ID);

        withMocks(resource -> {
            when(prescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(prescription);

            // Act
            Response response = resource.createOrderFromPrescription(body);

            // Assert
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }

    @Test
    void createOrderFromPrescription_medicineNotFound_returns400() {
        // Arrange
        Prescription prescription = buildPrescription("VALIDATED", 10);

        Map<String, String> body = Map.of("prescriptionId", PRESCRIPTION_ID);

        withMocks(resource -> {
            when(prescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(prescription);
            when(medicineRepo.findById(MEDICINE_ID)).thenReturn(null);

            // Act
            Response response = resource.createOrderFromPrescription(body);

            // Assert
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }

    @Test
    void createOrderFromPrescription_insufficientStock_returns400() {
        // Arrange – quantity 10, stock only 5
        Prescription prescription = buildPrescription("VALIDATED", 10);
        Medicine medicine = buildMedicine(5);

        Map<String, String> body = Map.of("prescriptionId", PRESCRIPTION_ID);

        withMocks(resource -> {
            when(prescriptionRepo.findById(PRESCRIPTION_ID)).thenReturn(prescription);
            when(medicineRepo.findById(MEDICINE_ID)).thenReturn(medicine);

            // Act
            Response response = resource.createOrderFromPrescription(body);

            // Assert
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }

    // -----------------------------------------------------------------------
    // processPayment
    // -----------------------------------------------------------------------

    @Test
    void processPayment_pendingOrder_returns200AndStatusPaid() {
        // Arrange
        Order order = buildOrder("PENDING");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            Map<String, String> paymentData = Map.of("paymentMethod", "CREDIT_CARD");

            // Act
            Response response = resource.processPayment(ORDER_ID, paymentData);

            // Assert
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(order.getStatus()).isEqualTo("PAID");
        });
    }

    @Test
    void processPayment_validatedOrder_returns200AndStatusPaid() {
        // Arrange
        Order order = buildOrder("VALIDATED");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            Response response = resource.processPayment(ORDER_ID, new HashMap<>());

            // Assert
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(order.getStatus()).isEqualTo("PAID");
        });
    }

    @Test
    void processPayment_pendingOrder_callsUpdateStockWithCorrectArgs() {
        // Arrange
        Order order = buildOrder("PENDING");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            resource.processPayment(ORDER_ID, new HashMap<>());

            // Assert
            verify(medicineRepo).updateStock(MEDICINE_ID, 10);
        });
    }

    @Test
    void processPayment_orderNotFound_returns404() {
        withMocks(resource -> {
            when(orderRepo.findById("MISSING")).thenReturn(null);

            // Act
            Response response = resource.processPayment("MISSING", new HashMap<>());

            // Assert
            assertThat(response.getStatus()).isEqualTo(404);
        });
    }

    @Test
    void processPayment_alreadyPaidOrder_returns400() {
        // Arrange
        Order order = buildOrder("PAID");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            Response response = resource.processPayment(ORDER_ID, new HashMap<>());

            // Assert
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }

    @Test
    void processPayment_collectedOrder_returns400() {
        // Arrange
        Order order = buildOrder("COLLECTED");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            Response response = resource.processPayment(ORDER_ID, new HashMap<>());

            // Assert
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }

    // -----------------------------------------------------------------------
    // collectOrder
    // -----------------------------------------------------------------------

    @Test
    void collectOrder_paidOrder_returns200AndStatusCollected() {
        // Arrange
        Order order = buildOrder("PAID");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            Response response = resource.collectOrder(ORDER_ID);

            // Assert
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(order.getStatus()).isEqualTo("COLLECTED");
        });
    }

    @Test
    void collectOrder_orderNotFound_returns404() {
        withMocks(resource -> {
            when(orderRepo.findById("MISSING")).thenReturn(null);

            // Act
            Response response = resource.collectOrder("MISSING");

            // Assert
            assertThat(response.getStatus()).isEqualTo(404);
        });
    }

    @Test
    void collectOrder_notPaidOrder_returns400() {
        // Arrange
        Order order = buildOrder("PENDING");

        withMocks(resource -> {
            when(orderRepo.findById(ORDER_ID)).thenReturn(order);

            // Act
            Response response = resource.collectOrder(ORDER_ID);

            // Assert
            assertThat(response.getStatus()).isEqualTo(400);
        });
    }
}
