package com.dave.beeskium.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationRequest {

    private String barbershopId;
    private LocalDateTime reservationDate;
    private Long staffId;
    private List<Long> serviceIds;

    public String getBarbershopId() {
        return barbershopId;
    }

    public void setBarbershopId(String barbershopId) {
        this.barbershopId = barbershopId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }
}
