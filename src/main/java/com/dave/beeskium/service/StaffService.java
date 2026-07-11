package com.dave.beeskium.service;

import java.util.List;

import com.dave.beeskium.model.Staff;
import com.dave.beeskium.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Transactional(readOnly = true)
    public List<Staff> getByBarbershop(String barbershopSlug) {
        return staffRepository.findByBarbershop_Slug(barbershopSlug);
    }
}
