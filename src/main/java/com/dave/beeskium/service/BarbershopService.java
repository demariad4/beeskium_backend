package com.dave.beeskium.service;

import java.util.List;
import java.util.Optional;

import com.dave.beeskium.dto.AdminBarbershopRequest;
import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.model.User;
import com.dave.beeskium.repository.BarbershopRepository;
import com.dave.beeskium.repository.ReservationRepository;
import com.dave.beeskium.repository.ServiceRepository;
import com.dave.beeskium.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BarbershopService {

    private final BarbershopRepository barbershopRepository;
    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final UserService userService;

    public BarbershopService(BarbershopRepository barbershopRepository,
            ReservationRepository reservationRepository,
            ServiceRepository serviceRepository,
            StaffRepository staffRepository,
            UserService userService) {
        this.barbershopRepository = barbershopRepository;
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
        this.staffRepository = staffRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Barbershop> getAll() {
        return barbershopRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Barbershop> getMyBarbershops(String ownerEmail) {
        return barbershopRepository.findByOwner_Email(ownerEmail);
    }

    @Transactional(readOnly = true)
    public Barbershop getBySlug(String slug) {
        return barbershopRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Barbershop non trovato"));
    }

    @Transactional(readOnly = true)
    public Optional<Barbershop> getMyBarbershop(String ownerEmail, Long barbershopId) {
        return barbershopRepository.findByIdAndOwner_Email(barbershopId, ownerEmail);
    }

    @Transactional
    public Barbershop createMyBarbershop(String ownerEmail, AdminBarbershopRequest request) {
        if (barbershopRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Uno shop con questo slug esiste già");
        }

        User owner = userService.findByEmail(ownerEmail);

        Barbershop barbershop = new Barbershop();
        applyRequest(barbershop, request);
        barbershop.setOwner(owner);

        return barbershopRepository.save(barbershop);
    }

    @Transactional
    public Barbershop updateMyBarbershop(String ownerEmail, Long barbershopId, AdminBarbershopRequest request) {
        Barbershop barbershop = getOwnedBarbershop(ownerEmail, barbershopId);
        if (!barbershop.getSlug().equals(request.getSlug())
                && barbershopRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Uno shop con questo slug esiste già");
        }

        applyRequest(barbershop, request);
        return barbershopRepository.save(barbershop);
    }

    @Transactional
    public void deleteMyBarbershop(String ownerEmail, Long barbershopId) {
        Barbershop barbershop = getOwnedBarbershop(ownerEmail, barbershopId);

        Long ownedBarbershopId = barbershop.getId();

        reservationRepository.deleteByBarbershop_Id(ownedBarbershopId);
        serviceRepository.deleteByBarbershop_Id(ownedBarbershopId);
        staffRepository.deleteByBarbershop_Id(ownedBarbershopId);

        barbershopRepository.delete(barbershop);
    }

    private Barbershop getOwnedBarbershop(String ownerEmail, Long barbershopId) {
        return barbershopRepository.findByIdAndOwner_Email(barbershopId, ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Non hai un barbershop associato a questo id"));
    }

    private void applyRequest(Barbershop barbershop, AdminBarbershopRequest request) {
        barbershop.setSlug(request.getSlug());
        barbershop.setName(request.getName());
        barbershop.setAddress(request.getAddress());
        barbershop.setMapsUrl(request.getMapsUrl());
        barbershop.setWhatsapp(request.getWhatsapp());
        barbershop.setInstagram(request.getInstagram());
        barbershop.setPhone(request.getPhone());
    }
}
