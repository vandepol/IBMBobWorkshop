package com.pharmacy.api;

import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/medicines")
public class MedicineResource {
    
    private final MedicineRepository medicineRepo = MedicineRepository.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Medicine> getAllMedicines() {
        return medicineRepo.findAll();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedicineById(@PathParam("id") String id) {
        Medicine medicine = medicineRepo.findById(id);
        if (medicine == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Medicine not found\"}")
                .build();
        }
        return Response.ok(medicine).build();
    }
    
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Medicine> searchMedicines(@QueryParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            return medicineRepo.findAll();
        }
        return medicineRepo.searchByName(name);
    }
}

// Made with Bob