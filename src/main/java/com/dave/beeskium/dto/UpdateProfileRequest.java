package com.dave.beeskium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Il nome non può essere vuoto")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Il numero non può essere vuoto")
    @Size(max = 20)
    private String phone;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
