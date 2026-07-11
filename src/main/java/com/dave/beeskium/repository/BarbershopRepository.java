package com.dave.beeskium.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Barbershop;

@Repository
public interface BarbershopRepository extends JpaRepository<Barbershop, Long> {
    Optional<Barbershop> findBySlug(String slug);
}
