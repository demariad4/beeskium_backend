package com.dave.beeskium.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminServiceRequest {

    @NotBlank(message = "Il nome del servizio non può essere vuoto")
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Min(value = 0, message = "Il prezzo non può essere negativo")
    private BigDecimal price;

    @NotNull(message = "La durata è obbligatoria")
    @Min(value = 1, message = "La durata deve essere di almeno 1 minuto")
    private Integer durationMinutes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
