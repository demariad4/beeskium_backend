package com.dave.beeskium.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dave.beeskium.dto.AdminBarbershopRequest;
import com.dave.beeskium.dto.AdminServiceRequest;
import com.dave.beeskium.dto.AdminStaffRequest;
import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.model.Service;
import com.dave.beeskium.model.Staff;
import com.dave.beeskium.model.User;
import com.dave.beeskium.security.JwtUtil;
import com.dave.beeskium.service.BarbershopService;
import com.dave.beeskium.service.ServicesService;
import com.dave.beeskium.service.StaffService;
import com.dave.beeskium.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BarbershopService barbershopService;
    private final ServicesService servicesService;
    private final StaffService staffService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminController(BarbershopService barbershopService,
            ServicesService servicesService,
            StaffService staffService,
            UserService userService,
            JwtUtil jwtUtil) {
        this.barbershopService = barbershopService;
        this.servicesService = servicesService;
        this.staffService = staffService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/become-admin")
    public ResponseEntity<String> becomeAdmin(Principal principal) {
        User user = userService.makeAdmin(principal.getName());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/barbershops")
    public ResponseEntity<List<Barbershop>> getMyBarbershops(Principal principal) {
        ensureAdmin(principal);
        List<Barbershop> barbershops = barbershopService.getMyBarbershops(principal.getName());
        return ResponseEntity.ok(barbershops);
    }

    @GetMapping("/barbershops/{barbershopId}")
    public ResponseEntity<Barbershop> getMyBarbershop(
            @PathVariable Long barbershopId,
            Principal principal) {
        ensureAdmin(principal);
        Optional<Barbershop> barbershop = barbershopService.getMyBarbershop(principal.getName(), barbershopId);
        return barbershop.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/barbershops")
    public ResponseEntity<Barbershop> createMyBarbershop(
            Principal principal,
            @Valid @RequestBody AdminBarbershopRequest request) {
        ensureAdmin(principal);
        Barbershop barbershop = barbershopService.createMyBarbershop(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(barbershop);
    }

    @PutMapping("/barbershops/{barbershopId}")
    public ResponseEntity<Barbershop> updateMyBarbershop(
            @PathVariable Long barbershopId,
            Principal principal,
            @Valid @RequestBody AdminBarbershopRequest request) {
        ensureAdmin(principal);
        Barbershop barbershop = barbershopService.updateMyBarbershop(principal.getName(), barbershopId, request);
        return ResponseEntity.ok(barbershop);
    }

    @DeleteMapping("/barbershops/{barbershopId}")
    public ResponseEntity<Void> deleteMyBarbershop(
            @PathVariable Long barbershopId,
            Principal principal) {
        ensureAdmin(principal);
        barbershopService.deleteMyBarbershop(principal.getName(), barbershopId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barbershops/{barbershopId}/services")
    public ResponseEntity<List<Service>> getMyServices(
            Principal principal,
            @PathVariable Long barbershopId) {
        ensureAdmin(principal);
        List<Service> services = servicesService.getServicesByOwnerBarbershop(principal.getName(), barbershopId);
        return ResponseEntity.ok(services);
    }

    @PostMapping("/barbershops/{barbershopId}/services")
    public ResponseEntity<Service> createMyService(
            Principal principal,
            @PathVariable Long barbershopId,
            @Valid @RequestBody AdminServiceRequest request) {
        ensureAdmin(principal);
        Service service = servicesService.createForOwnerBarbershop(principal.getName(), barbershopId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PutMapping("/barbershops/{barbershopId}/services/{serviceId}")
    public ResponseEntity<Service> updateMyService(
            Principal principal,
            @PathVariable Long barbershopId,
            @PathVariable Long serviceId,
            @Valid @RequestBody AdminServiceRequest request) {
        ensureAdmin(principal);
        Service service = servicesService.updateForOwnerBarbershop(principal.getName(), barbershopId, serviceId, request);
        return ResponseEntity.ok(service);
    }

    @DeleteMapping("/barbershops/{barbershopId}/services/{serviceId}")
    public ResponseEntity<Void> deleteMyService(
            Principal principal,
            @PathVariable Long barbershopId,
            @PathVariable Long serviceId) {
        ensureAdmin(principal);
        servicesService.deleteForOwnerBarbershop(principal.getName(), barbershopId, serviceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barbershops/{barbershopId}/staff")
    public ResponseEntity<List<Staff>> getMyStaff(
            Principal principal,
            @PathVariable Long barbershopId) {
        ensureAdmin(principal);
        List<Staff> staffMembers = staffService.getByOwnerBarbershop(principal.getName(), barbershopId);
        return ResponseEntity.ok(staffMembers);
    }

    @PostMapping("/barbershops/{barbershopId}/staff")
    public ResponseEntity<Staff> createMyStaff(
            Principal principal,
            @PathVariable Long barbershopId,
            @Valid @RequestBody AdminStaffRequest request) {
        ensureAdmin(principal);
        Staff staff = staffService.createForOwnerBarbershop(principal.getName(), barbershopId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(staff);
    }

    @PutMapping("/barbershops/{barbershopId}/staff/{staffId}")
    public ResponseEntity<Staff> updateMyStaff(
            Principal principal,
            @PathVariable Long barbershopId,
            @PathVariable Long staffId,
            @Valid @RequestBody AdminStaffRequest request) {
        ensureAdmin(principal);
        Staff staff = staffService.updateForOwnerBarbershop(principal.getName(), barbershopId, staffId, request);
        return ResponseEntity.ok(staff);
    }

    @DeleteMapping("/barbershops/{barbershopId}/staff/{staffId}")
    public ResponseEntity<Void> deleteMyStaff(
            Principal principal,
            @PathVariable Long barbershopId,
            @PathVariable Long staffId) {
        ensureAdmin(principal);
        staffService.deleteForOwnerBarbershop(principal.getName(), barbershopId, staffId);
        return ResponseEntity.noContent().build();
    }

    private void ensureAdmin(Principal principal) {
        if (!userService.isAdmin(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accesso riservato agli amministratori.");
        }
    }
}
