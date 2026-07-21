package com.dave.beeskium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Staff;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByBarbershop_Slug(String slug);
    List<Staff> findByBarbershop_Id(Long barbershopId);

    void deleteByBarbershop_Id(Long barbershopId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Staff s WHERE s.id = :staffId")
    Optional<Staff> findByIdForUpdate(@Param("staffId") Long staffId);
}
