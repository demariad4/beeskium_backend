package com.dave.beeskium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

}
