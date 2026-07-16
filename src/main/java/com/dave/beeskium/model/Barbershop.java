package com.dave.beeskium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.dave.beeskium.utils.LocalTimeStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalTime;

@Entity
@Table(name = "barbershops")
public class Barbershop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Lo slug del barbershop non può essere vuoto")
    @Size(max = 80)
    @Column(nullable = false, unique = true)
    private String slug;

    @NotBlank(message = "Il nome del barbershop non può essere vuoto")
    @Size(max = 120)
    @Column(nullable = false)
    private String name;

    private String address;
    private String mapsUrl;
    private String whatsapp;
    private String instagram;
    private String phone;

    @Convert(converter = LocalTimeStringConverter.class)
    @Column(name = "opening_time", nullable = true, length = 5)
    private LocalTime openingTime;

    @Convert(converter = LocalTimeStringConverter.class)
    @Column(name = "closing_time", nullable = true, length = 5)
    private LocalTime closingTime;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    public Barbershop() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapsUrl() {
        return mapsUrl;
    }

    public void setMapsUrl(String mapsUrl) {
        this.mapsUrl = mapsUrl;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
