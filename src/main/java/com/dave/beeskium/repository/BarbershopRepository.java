package com.dave.beeskium.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Barbershop;

@Repository
public interface BarbershopRepository extends JpaRepository<Barbershop, Long> {
    List<Barbershop> findByOwner_Email(String email);

    Optional<Barbershop> findByIdAndOwner_Email(Long id, String email);

    Optional<Barbershop> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
