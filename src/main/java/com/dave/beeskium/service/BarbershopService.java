package com.dave.beeskium.service;

import java.util.List;

import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.repository.BarbershopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BarbershopService {

    private final BarbershopRepository barbershopRepository;

    public BarbershopService(BarbershopRepository barbershopRepository) {
        this.barbershopRepository = barbershopRepository;
    }

    @Transactional(readOnly = true)
    public List<Barbershop> getAll() {
        return barbershopRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Barbershop getBySlug(String slug) {
        return barbershopRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Barbershop non trovato"));
    }
}
