package com.dave.beeskium.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dave.beeskium.dto.ReservationReply;
import com.dave.beeskium.dto.ReservationRequest;
import com.dave.beeskium.model.Reservation;
import com.dave.beeskium.model.Service;
import com.dave.beeskium.service.ReservationService;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationReply> reserve(@RequestBody ReservationRequest request, Principal principal) {

        String userEmail = principal.getName();

        Reservation reservation = reservationService.createReservation(userEmail, request);

        List<Long> serviceIds = reservation.getServices().stream()
                .map(Service::getId)
                .toList();

        String barbershopId = reservation.getBarbershop() != null ? reservation.getBarbershop().getSlug() : "";

        ReservationReply reply = new ReservationReply(
                reservation.getId(),
                barbershopId,
                reservation.getReservationDate(),
                reservation.getStaff().getId(),
                serviceIds, reservation.getTotalPrice(), reservation.getTotalDurationMinutes());

        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationReply>> getReservations(Principal principal) {
        List<Reservation> reservations = reservationService.getReservations(principal.getName());
        List<ReservationReply> ret = new LinkedList<>();

        for (Reservation r : reservations) {
            String barbershopId = r.getBarbershop() != null ? r.getBarbershop().getSlug() : "";
            List<Long> serviceIds = r.getServices().stream()
                    .map(Service::getId)
                    .toList();

            ret.add(new ReservationReply(r.getId(), barbershopId, r.getReservationDate(),
                    r.getStaff().getId(), serviceIds, r.getTotalPrice(),
                    r.getTotalDurationMinutes()));
        }
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/barbershops/{barbershopId}/reservations")
    public ResponseEntity<List<ReservationReply>> getUpcomingBarbershopReservations(
            @PathVariable String barbershopId) {

        List<Reservation> reservations = reservationService.getUpcomingReservationsByBarbershop(barbershopId);

        List<ReservationReply> ret = new LinkedList<>();

        for (Reservation r : reservations) {
            String barbershopSlug = r.getBarbershop() != null ? r.getBarbershop().getSlug() : "";
            List<Long> serviceIds = r.getServices().stream().map(Service::getId).toList();

            ret.add(new ReservationReply(r.getId(), barbershopSlug, r.getReservationDate(),
                    r.getStaff().getId(), serviceIds, r.getTotalPrice(),
                    r.getTotalDurationMinutes()));
        }

        return ResponseEntity.ok(ret);
    }

    @DeleteMapping("/deleteReservation/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, Principal principal) {

        String userEmail = principal.getName();
        reservationService.deleteReservation(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availabilities")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam String barbershopId,
            @RequestParam Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<Long> serviceIds) {

        List<LocalTime> availableSlots = reservationService.getAvailableTimeSlots(barbershopId, staffId, date,
                serviceIds);
        return ResponseEntity.ok(availableSlots);
    }
}
