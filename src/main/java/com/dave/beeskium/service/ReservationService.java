package com.dave.beeskium.service;

import com.dave.beeskium.dto.ReservationRequest;
import com.dave.beeskium.model.Reservation;
import com.dave.beeskium.model.Service;
import com.dave.beeskium.model.Staff;
import com.dave.beeskium.model.User;
import com.dave.beeskium.repository.ReservationRepository;
import com.dave.beeskium.repository.ServiceRepository;
import com.dave.beeskium.repository.StaffRepository;
import com.dave.beeskium.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

// necessario in quanto ho la classe service dello stess nome
@org.springframework.stereotype.Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;

    public ReservationService(ReservationRepository reservationRepository,
            UserRepository userRepository,
            StaffRepository staffRepository,
            ServiceRepository serviceRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public List<Reservation> getReservations(String email) {
        return reservationRepository.findByUserEmail(email);
    }

    @Transactional
    public Reservation createReservation(String userEmail, ReservationRequest request) {

        User user = userRepository.findByEmail(userEmail);

        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Membro dello staff non trovato"));

        if (!staff.getIsActive())
            throw new RuntimeException("Membro dello staff non attivo");

        List<Service> services = serviceRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new RuntimeException("Devi selezionare almeno un servizio");
        }

        // Calcolo prezzo e durata
        BigDecimal totalPrice = BigDecimal.ZERO;
        Integer totalDurationMinutes = 0;
        for (Service s : services) {
            totalPrice = totalPrice.add(s.getPrice());
            totalDurationMinutes += s.getDurationMinutes();
        }

        // Controllo se ci sono sovrapposizioni per lo stesso membro dello staff
        LocalDateTime newStart = request.getReservationDate();
        LocalDateTime newEnd = newStart.plusMinutes(totalDurationMinutes);

        List<Reservation> staffReservations = reservationRepository.findByStaffId(staff.getId());

        for (Reservation existing : staffReservations) {
            LocalDateTime existingStart = existing.getReservationDate();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getTotalDurationMinutes());

            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                throw new RuntimeException("Sovrapposizione orari! Il dipendente è occupato dalle "
                        + existingStart.toLocalTime() + " alle " + existingEnd.toLocalTime());
            }
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStaff(staff);
        reservation.setServices(services);
        reservation.setReservationDate(newStart);
        reservation.setTotalPrice(totalPrice);
        reservation.setTotalDurationMinutes(totalDurationMinutes);

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id, String userEmail) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata con ID: " + id));

        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Non sei autorizzato a eliminare questa prenotazione");
        }

        reservationRepository.delete(reservation);
    }

    public List<LocalTime> getAvailableTimeSlots(Long staffId, LocalDate date, List<Long> serviceIds) {

        List<Service> services = serviceRepository.findAllById(serviceIds);
        if (services.isEmpty()) {
            throw new RuntimeException("Devi selezionare almeno un servizio");
        }

        int totalDurationMinutes = services.stream()
                .mapToInt(Service::getDurationMinutes)
                .sum();

        LocalTime openingTime = LocalTime.of(8, 0);
        LocalTime closingTime = LocalTime.of(20, 0);

        int slotIntervalMinutes = 5;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Reservation> existingReservations = reservationRepository.findByStaffIdAndReservationDateBetween(staffId,
                startOfDay, endOfDay);

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime currentSlot = openingTime;

        while (currentSlot.plusMinutes(totalDurationMinutes).isBefore(closingTime) ||
                currentSlot.plusMinutes(totalDurationMinutes).equals(closingTime)) {

            LocalDateTime potentialStart = LocalDateTime.of(date, currentSlot);

            LocalDateTime potentialEnd = potentialStart.plusMinutes(totalDurationMinutes);

            boolean isOverlap = false;

            for (Reservation existing : existingReservations) {
                LocalDateTime existingStart = existing.getReservationDate();
                LocalDateTime existingEnd = existingStart.plusMinutes(existing.getTotalDurationMinutes());

                if (potentialStart.isBefore(existingEnd) && potentialEnd.isAfter(existingStart)) {
                    isOverlap = true;
                    break;
                }
            }

            if (!isOverlap && potentialStart.isAfter(LocalDateTime.now())) {
                availableSlots.add(currentSlot);
            }

            currentSlot = currentSlot.plusMinutes(slotIntervalMinutes);
        }

        return availableSlots;
    }
}