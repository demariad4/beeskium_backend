package com.dave.beeskium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dave.beeskium.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStaffId(Long staffId);

    List<Reservation> findByUserEmail(String email);

    List<Reservation> findByStaffIdAndReservationDateBetween(Long staffId, LocalDateTime start, LocalDateTime end);

    List<Reservation> findByBarbershop_SlugAndReservationDateAfterOrderByReservationDateAsc(String slug, LocalDateTime now);
}
