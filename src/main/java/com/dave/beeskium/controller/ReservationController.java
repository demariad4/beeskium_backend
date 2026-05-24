package com.dave.beeskium.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Reservation> reserve(@RequestBody ReservationRequest request, Principal principal) {

        String userEmail = principal.getName();

        Reservation reservation = reservationService.createReservation(userEmail, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationReply>> getReservations(Principal principal) {
        List<Reservation> reservations = reservationService.getReservations(principal.getName());
        List<ReservationReply> ret = new LinkedList<>();

        for (Reservation r : reservations) {
            List<Long> serviceIds = r.getServices().stream()
                    .map(Service::getId)
                    .toList();

            ret.add(new ReservationReply(r.getReservationDate(), r.getStaff().getId(), serviceIds, r.getTotalPrice(),
                    r.getTotalDurationMinutes()));
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(ret);
    }

}