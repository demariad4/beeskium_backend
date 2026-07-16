package com.dave.beeskium.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.dave.beeskium.dto.AdminServiceRequest;
import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.model.Service;
import com.dave.beeskium.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class ServicesService {

    private final ServiceRepository serviceRepository;
    private final BarbershopService barbershopService;

    public ServicesService(ServiceRepository serviceRepository, BarbershopService barbershopService) {
        this.serviceRepository = serviceRepository;
        this.barbershopService = barbershopService;
    }

    @Transactional
    public List<Service> getServices() {
        return serviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Service> getServicesByBarbershop(String barbershopSlug) {
        return serviceRepository.findByBarbershop_Slug(barbershopSlug);
    }

    @Transactional(readOnly = true)
    public List<Service> getServicesByOwnerBarbershop(String ownerEmail, Long barbershopId) {
        Barbershop barbershop = getOwnerBarbershop(ownerEmail, barbershopId);
        return serviceRepository.findByBarbershop_Id(barbershop.getId());
    }

    @Transactional
    public Service createForOwnerBarbershop(String ownerEmail, Long barbershopId, AdminServiceRequest request) {
        Barbershop barbershop = getOwnerBarbershop(ownerEmail, barbershopId);

        Service service = new Service();
        applyRequest(service, request);
        service.setBarbershop(barbershop);

        return serviceRepository.save(service);
    }

    @Transactional
    public Service updateForOwnerBarbershop(String ownerEmail, Long barbershopId, Long serviceId, AdminServiceRequest request) {
        Service service = getOwnedService(ownerEmail, barbershopId, serviceId);
        applyRequest(service, request);
        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteForOwnerBarbershop(String ownerEmail, Long barbershopId, Long serviceId) {
        Service service = getOwnedService(ownerEmail, barbershopId, serviceId);
        serviceRepository.delete(service);
    }

    @Transactional(readOnly = true)
    public Service getForOwnerBarbershop(String ownerEmail, Long barbershopId, Long serviceId) {
        return getOwnedService(ownerEmail, barbershopId, serviceId);
    }

    private void applyRequest(Service service, AdminServiceRequest request) {
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
    }

    private Barbershop getOwnerBarbershop(String ownerEmail, Long barbershopId) {
        return barbershopService.getMyBarbershop(ownerEmail, barbershopId)
                .orElseThrow(() -> new IllegalArgumentException("Prima crea un barbershop."));
    }

    private Service getOwnedService(String ownerEmail, Long barbershopId, Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Servizio non trovato."));

        Barbershop barbershop = getOwnerBarbershop(ownerEmail, barbershopId);
        if (service.getBarbershop() == null || !barbershop.getId().equals(service.getBarbershop().getId())) {
            throw new IllegalArgumentException("Questo servizio non appartiene al tuo barbershop.");
        }

        return service;
    }
}
