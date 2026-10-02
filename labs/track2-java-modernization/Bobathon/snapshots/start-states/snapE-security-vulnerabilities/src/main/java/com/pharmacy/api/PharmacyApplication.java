package com.pharmacy.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class PharmacyApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Register all REST resource classes
        classes.add(DashboardResource.class);
        classes.add(MedicineResource.class);
        classes.add(PrescriptionResource.class);
        classes.add(OrderResource.class);
        
        // Register CORS filter
        classes.add(CorsFilter.class);
        
        return classes;
    }
}