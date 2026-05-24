package com.dave.beeskium.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dave.beeskium.model.Service;
import com.dave.beeskium.service.ServicesService;

@RestController
@RequestMapping("/api")
public class ServicesController {

    private final ServicesService servicesService;

    public ServicesController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        List<Service> services = servicesService.getServices();
        return ResponseEntity.status(HttpStatus.FOUND).body(services);
    }

}
