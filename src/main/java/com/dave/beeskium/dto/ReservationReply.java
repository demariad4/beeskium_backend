package com.dave.beeskium.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationReply {

    private LocalDateTime reservationDate;
    private Long staffId;
    private List<Long> serviceIds;
    private BigDecimal totalPrice;
    private Integer totalDurationMinutes;

    public ReservationReply(LocalDateTime reservationDate, Long staffId, List<Long> serviceIds, BigDecimal totalPrice,
            Integer totalDurationMinutes) {
        this.reservationDate = reservationDate;
        this.staffId = staffId;
        this.serviceIds = serviceIds;
        this.totalPrice = totalPrice;
        this.totalDurationMinutes = totalDurationMinutes;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

}