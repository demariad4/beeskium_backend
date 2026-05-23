package com.dave.beeskium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dave.beeskium.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

}
