package com.dave.beeskium.service;

import java.util.List;

import com.dave.beeskium.model.Service;
import com.dave.beeskium.repository.ServiceRepository;

import jakarta.transaction.Transactional;

@org.springframework.stereotype.Service
public class ServicesService {

    private final ServiceRepository serviceRepository;

    public ServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public List<Service> getServices() {
        return serviceRepository.findAll();
    }

}
