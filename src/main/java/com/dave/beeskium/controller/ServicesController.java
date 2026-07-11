package com.dave.beeskium.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.model.Service;
import com.dave.beeskium.model.Staff;
import com.dave.beeskium.service.BarbershopService;
import com.dave.beeskium.service.ServicesService;
import com.dave.beeskium.service.StaffService;

@RestController
@RequestMapping("/api")
public class ServicesController {

    private final ServicesService servicesService;
    private final BarbershopService barbershopService;
    private final StaffService staffService;

    public ServicesController(ServicesService servicesService, BarbershopService barbershopService,
            StaffService staffService) {
        this.servicesService = servicesService;
        this.barbershopService = barbershopService;
        this.staffService = staffService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        List<Service> services = servicesService.getServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/barbershops")
    public ResponseEntity<List<Barbershop>> getBarbershops() {
        List<Barbershop> barbershops = barbershopService.getAll();
        return ResponseEntity.ok(barbershops);
    }

    @GetMapping("/barbershops/{barbershopId}")
    public ResponseEntity<Barbershop> getBarbershop(@PathVariable String barbershopId) {
        Barbershop barbershop = barbershopService.getBySlug(barbershopId);
        return ResponseEntity.ok(barbershop);
    }

    @GetMapping("/barbershops/{barbershopId}/services")
    public ResponseEntity<List<Service>> getServicesByBarbershop(@PathVariable String barbershopId) {
        List<Service> services = servicesService.getServicesByBarbershop(barbershopId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/barbershops/{barbershopId}/staff")
    public ResponseEntity<List<Staff>> getStaffByBarbershop(@PathVariable String barbershopId) {
        List<Staff> staffMembers = staffService.getByBarbershop(barbershopId);
        return ResponseEntity.ok(staffMembers);
    }
}
