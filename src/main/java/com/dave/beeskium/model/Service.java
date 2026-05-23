package com.dave.beeskium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome del servizio non può essere vuoto")
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Min(value = 0, message = "Il prezzo non può essere negativo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "La durata è obbligatoria")
    @Min(value = 1, message = "La durata deve essere di almeno 1 minuto")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    public Service() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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