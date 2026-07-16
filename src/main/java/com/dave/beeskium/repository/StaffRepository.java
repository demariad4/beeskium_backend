package com.dave.beeskium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Staff;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByBarbershop_Slug(String slug);
    List<Staff> findByBarbershop_Id(Long barbershopId);

    void deleteByBarbershop_Id(Long barbershopId);
}
