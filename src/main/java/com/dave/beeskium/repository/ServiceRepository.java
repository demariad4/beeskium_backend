package com.dave.beeskium.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByBarbershop_Slug(String slug);
    List<Service> findByBarbershop_Id(Long barbershopId);

    void deleteByBarbershop_Id(Long barbershopId);
}
