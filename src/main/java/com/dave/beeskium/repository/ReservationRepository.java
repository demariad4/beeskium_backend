package com.dave.beeskium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dave.beeskium.model.Reservation;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStaffId(Long staffId);

    List<Reservation> findByUserEmail(String email);
}
