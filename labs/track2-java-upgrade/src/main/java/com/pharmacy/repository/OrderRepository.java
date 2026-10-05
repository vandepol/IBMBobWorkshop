package com.pharmacy.repository;

import com.pharmacy.model.Order;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OrderRepository {
    private static OrderRepository instance;
    private final Map<String, Order> orders;
    private final AtomicInteger idCounter;
    
    private OrderRepository() {
        orders = new ConcurrentHashMap<>();
        idCounter = new AtomicInteger(5000);
        initializeSampleData();
    }
    
    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        // Sample orders will be created when prescriptions are processed
        // This method can be used to add test data if needed
    }
    
    public String generateId() {
        return "ORD" + String.format("%04d", idCounter.incrementAndGet());
    }
    
    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }
    
    public Order findById(String id) {
        return orders.get(id);
    }
    
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
    
    public List<Order> findByPatientId(String patientId) {
        return orders.values().stream()
            .filter(o -> o.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }
    
    public List<Order> findByStatus(String status) {
        return orders.values().stream()
            .filter(o -> o.getStatus().equals(status))
            .collect(Collectors.toList());
    }
    
    public List<Order> findByPrescriptionId(String prescriptionId) {
        return orders.values().stream()
            .filter(o -> o.getPrescriptionId().equals(prescriptionId))
            .collect(Collectors.toList());
    }
    
    public void updateOrder(Order order) {
        orders.put(order.getId(), order);
    }
    
    public void deleteOrder(String id) {
        orders.remove(id);
    }
}

// Made with Bob
