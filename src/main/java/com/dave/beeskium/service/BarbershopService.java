package com.dave.beeskium.service;

import java.util.List;
import java.util.Optional;
import java.time.LocalTime;

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
        if (!isValidOptionalUrl(request.getMapsUrl())) {
            throw new IllegalArgumentException("Formato URL maps non valido.");
        }

        if (!isValidOptionalUrl(request.getWhatsapp(), new String[] {
                "wa.me",
                "api.whatsapp.com",
                "web.whatsapp.com"
        })) {
            throw new IllegalArgumentException("Formato WhatsApp non valido.");
        }

        if (!isValidOptionalUrl(request.getInstagram(), new String[] {
                "instagram.com",
                "www.instagram.com"
        })) {
            throw new IllegalArgumentException("Formato Instagram non valido.");
        }

        barbershop.setSlug(request.getSlug());
        barbershop.setName(request.getName());
        barbershop.setAddress(request.getAddress());
        barbershop.setMapsUrl(request.getMapsUrl());
        barbershop.setWhatsapp(request.getWhatsapp());
        barbershop.setInstagram(request.getInstagram());
        barbershop.setPhone(request.getPhone());
        barbershop.setOpeningTime(resolveOpeningTime(request.getOpeningTime(), LocalTime.of(8, 0)));
        barbershop.setClosingTime(resolveClosingTime(request.getClosingTime(), LocalTime.of(20, 0)));

        if (!barbershop.getOpeningTime().isBefore(barbershop.getClosingTime())) {
            throw new IllegalArgumentException("L'orario di apertura deve essere precedente a quello di chiusura.");
        }
    }

    private LocalTime resolveOpeningTime(LocalTime candidate, LocalTime fallback) {
        if (candidate == null) {
            return fallback;
        }
        return candidate;
    }

    private LocalTime resolveClosingTime(LocalTime candidate, LocalTime fallback) {
        if (candidate == null) {
            return fallback;
        }
        return candidate;
    }

    private boolean isValidOptionalUrl(String rawUrl, String[] allowedHosts) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return true;
        }

        var parsed = parseOrNull(rawUrl);
        if (parsed == null) {
            return false;
        }

        return hasAllowedHost(parsed, allowedHosts);
    }

    private boolean isValidOptionalUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return true;
        }

        return parseOrNull(rawUrl) != null;
    }

    private boolean hasAllowedHost(java.net.URL parsed, String[] allowedHosts) {
        String host = parsed.getHost();
        for (String allowedHost : allowedHosts) {
            if (allowedHost.equalsIgnoreCase(host)) {
                return true;
            }
        }
        return false;
    }

    private java.net.URL parseOrNull(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }

        String trimmed = rawUrl.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return new java.net.URL(trimmed);
        } catch (Exception ignore) {
            try {
                return new java.net.URL("https://" + trimmed);
            } catch (Exception ignore2) {
                return null;
            }
        }
    }
}
