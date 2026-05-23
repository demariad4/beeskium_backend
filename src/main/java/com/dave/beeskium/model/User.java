package com.dave.beeskium.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome non può essere vuoto")
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Il ruolo non può essere vuoto")
    @Column(nullable = false, length = 20)
    private String role = "USER";

    @NotBlank(message = "La mail non può essere vuota")
    @Email(message = "Formato mail non valido")
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Il numero non può essere vuoto")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @NotBlank(message = "La password non può essere vuota")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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