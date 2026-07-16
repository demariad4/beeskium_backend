package com.dave.beeskium.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminStaffRequest {

    @NotBlank(message = "Il nome dello staff non può essere vuoto")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Il cognome dello staff non può essere vuoto")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "La mail non può essere vuota")
    @Email(message = "Formato mail dello staff non valido")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Il numero non può essere vuoto")
    @Size(max = 20)
    private String phone;

    private Boolean isActive = true;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
