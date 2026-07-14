package com.dave.beeskium.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dave.beeskium.dto.AdminStaffRequest;
import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.model.Staff;
import com.dave.beeskium.repository.StaffRepository;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final BarbershopService barbershopService;

    public StaffService(StaffRepository staffRepository, BarbershopService barbershopService) {
        this.staffRepository = staffRepository;
        this.barbershopService = barbershopService;
    }

    @Transactional(readOnly = true)
    public List<Staff> getByBarbershop(String barbershopSlug) {
        return staffRepository.findByBarbershop_Slug(barbershopSlug);
    }

    @Transactional(readOnly = true)
    public List<Staff> getByOwnerBarbershop(String ownerEmail, Long barbershopId) {
        Barbershop barbershop = getOwnerBarbershop(ownerEmail, barbershopId);
        return staffRepository.findByBarbershop_Id(barbershop.getId());
    }

    @Transactional
    public Staff createForOwnerBarbershop(String ownerEmail, Long barbershopId, AdminStaffRequest request) {
        Barbershop barbershop = getOwnerBarbershop(ownerEmail, barbershopId);

        Staff staff = new Staff();
        applyRequest(staff, request);
        staff.setBarbershop(barbershop);

        return staffRepository.save(staff);
    }

    @Transactional
    public Staff updateForOwnerBarbershop(String ownerEmail, Long barbershopId, Long staffId, AdminStaffRequest request) {
        Staff staff = getOwnedStaff(ownerEmail, barbershopId, staffId);
        applyRequest(staff, request);
        return staffRepository.save(staff);
    }

    @Transactional
    public void deleteForOwnerBarbershop(String ownerEmail, Long barbershopId, Long staffId) {
        Staff staff = getOwnedStaff(ownerEmail, barbershopId, staffId);
        staffRepository.delete(staff);
    }

    @Transactional(readOnly = true)
    public Staff getForOwnerBarbershop(String ownerEmail, Long barbershopId, Long staffId) {
        return getOwnedStaff(ownerEmail, barbershopId, staffId);
    }

    private void applyRequest(Staff staff, AdminStaffRequest request) {
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
    }

    private Barbershop getOwnerBarbershop(String ownerEmail, Long barbershopId) {
        return barbershopService.getMyBarbershop(ownerEmail, barbershopId)
                .orElseThrow(() -> new IllegalArgumentException("Prima crea un barbershop."));
    }

    private Staff getOwnedStaff(String ownerEmail, Long barbershopId, Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Membro dello staff non trovato."));

        Barbershop barbershop = getOwnerBarbershop(ownerEmail, barbershopId);
        if (staff.getBarbershop() == null || !barbershop.getId().equals(staff.getBarbershop().getId())) {
            throw new IllegalArgumentException("Questo staff non appartiene al tuo barbershop.");
        }

        return staff;
    }
}
