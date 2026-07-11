package com.dave.beeskium.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Il nome non può essere vuoto")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "La mail non può essere vuota")
    @Email(message = "Formato mail non valido")
    private String email;

    @NotBlank(message = "Il numero non può essere vuoto")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "La password non può essere vuota")
    @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
