package com.dave.beeskium.service;

import com.dave.beeskium.dto.ReservationRequest;
import com.dave.beeskium.model.Reservation;
import com.dave.beeskium.model.Service;
import com.dave.beeskium.model.Staff;
import com.dave.beeskium.model.User;
import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.repository.ReservationRepository;
import com.dave.beeskium.repository.ServiceRepository;
import com.dave.beeskium.repository.StaffRepository;
import com.dave.beeskium.repository.BarbershopRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.DayOfWeek;

// necessario in quanto ho la classe service dello stess nome
@org.springframework.stereotype.Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;
    private final BarbershopRepository barbershopRepository;

    public ReservationService(ReservationRepository reservationRepository,
            UserService userService,
            StaffRepository staffRepository,
            ServiceRepository serviceRepository,
            BarbershopRepository barbershopRepository) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.staffRepository = staffRepository;
        this.serviceRepository = serviceRepository;
        this.barbershopRepository = barbershopRepository;
    }

    @Transactional
    public List<Reservation> getReservations(String email) {
        return reservationRepository.findByUserEmail(email);
    }

    @Transactional
    public List<Reservation> getUpcomingReservationsByBarbershop(String barbershopId) {
        return reservationRepository.findByBarbershop_SlugAndReservationDateAfterOrderByReservationDateAsc(
                barbershopId,
                LocalDateTime.now());
    }

    @Transactional
    public Reservation createReservation(String userEmail, ReservationRequest request) {
        if (request == null || request.getBarbershopId() == null || request.getBarbershopId().isBlank()) {
            throw new RuntimeException("Il barbershop è obbligatorio");
        }

        if (request.getStaffId() == null) {
            throw new RuntimeException("Devi selezionare un membro dello staff");
        }

        if (request.getServiceIds() == null || request.getServiceIds().isEmpty()) {
            throw new RuntimeException("Devi selezionare almeno un servizio");
        }

        if (request.getReservationDate() == null) {
            throw new RuntimeException("La data e ora sono obbligatorie");
        }
        if (request.getReservationDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Non è possibile prenotare di domenica");
        }

        Barbershop barbershop = barbershopRepository.findBySlug(request.getBarbershopId())
                .orElseThrow(() -> new RuntimeException("Barbershop non trovato"));

        User user = userService.findByEmail(userEmail);

        Staff staff = staffRepository.findByIdForUpdate(request.getStaffId())
        .orElseThrow(() -> new RuntimeException("Membro dello staff non trovato"));

        if (staff.getBarbershop() == null || staff.getBarbershop().getId() == null
                || !staff.getBarbershop().getId().equals(barbershop.getId())) {
            throw new RuntimeException("Il barbiere non appartiene al barbershop selezionato");
        }

        if (!staff.getIsActive())
            throw new RuntimeException("Membro dello staff non attivo");

        List<Service> services = serviceRepository.findAllById(request.getServiceIds());
        if (services.isEmpty() || services.size() != request.getServiceIds().size()) {
            throw new RuntimeException("Alcuni servizi non sono validi");
        }
        if (!services.stream().allMatch(service -> service.getBarbershop() != null
                && service.getBarbershop().getId() != null
                && service.getBarbershop().getId().equals(barbershop.getId()))) {
            throw new RuntimeException("Uno o più servizi non appartengono al barbershop selezionato");
        }

        // Calcolo prezzo e durata
        BigDecimal totalPrice = BigDecimal.ZERO;
        Integer totalDurationMinutes = 0;
        for (Service s : services) {
            totalPrice = totalPrice.add(s.getPrice());
            totalDurationMinutes += s.getDurationMinutes();
        }

        LocalTime openingTime = resolveOpeningTime(barbershop.getOpeningTime());
        LocalTime closingTime = resolveClosingTime(barbershop.getClosingTime());
        validateWithinOpeningWindow(request.getReservationDate(), totalDurationMinutes, openingTime, closingTime);

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
        reservation.setBarbershop(barbershop);
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

    public List<LocalTime> getAvailableTimeSlots(String barbershopId, Long staffId, LocalDate date,
            List<Long> serviceIds) {
        Barbershop barbershop = barbershopRepository.findBySlug(barbershopId)
                .orElseThrow(() -> new RuntimeException("Barbershop non trovato"));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Membro dello staff non trovato"));

        if (staff.getBarbershop() == null || staff.getBarbershop().getId() == null
                || !staff.getBarbershop().getId().equals(barbershop.getId())) {
            throw new RuntimeException("Il barbiere non appartiene al barbershop selezionato");
        }
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return List.of();
        }

        List<Service> services = serviceRepository.findAllById(serviceIds);
        if (serviceIds == null || serviceIds.isEmpty() || services.isEmpty()
                || services.size() != serviceIds.size()) {
            throw new RuntimeException("Devi selezionare almeno un servizio");
        }
        if (!services.stream().allMatch(service -> service.getBarbershop() != null
                && service.getBarbershop().getId() != null
                && service.getBarbershop().getId().equals(barbershop.getId()))) {
            throw new RuntimeException("Uno o più servizi non appartengono al barbershop selezionato");
        }

        int totalDurationMinutes = services.stream()
                .mapToInt(Service::getDurationMinutes)
                .sum();

        LocalTime openingTime = resolveOpeningTime(barbershop.getOpeningTime());
        LocalTime closingTime = resolveClosingTime(barbershop.getClosingTime());

        int slotIntervalMinutes = 15;

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

    private LocalTime resolveOpeningTime(LocalTime openingTime) {
        if (openingTime == null) {
            return LocalTime.of(8, 0);
        }
        return openingTime;
    }

    private LocalTime resolveClosingTime(LocalTime closingTime) {
        if (closingTime == null) {
            return LocalTime.of(20, 0);
        }
        return closingTime;
    }

    private void validateWithinOpeningWindow(
            LocalDateTime reservationStart,
            Integer totalDurationMinutes,
            LocalTime openingTime,
            LocalTime closingTime) {

        LocalDateTime openingDateTime = reservationStart.toLocalDate().atTime(openingTime);
        LocalDateTime closingDateTime = reservationStart.toLocalDate().atTime(closingTime);
        LocalDateTime reservationEnd = reservationStart.plusMinutes(totalDurationMinutes);

        if (!openingTime.isBefore(closingTime)) {
            throw new RuntimeException("Gli orari di apertura del barbershop non sono configurati correttamente.");
        }

        if (reservationStart.isBefore(openingDateTime) || reservationEnd.isAfter(closingDateTime)) {
            throw new RuntimeException(
                    "L'orario selezionato deve essere compreso negli orari di apertura del barbershop.");
        }
    }
}
