package com.hotel.hotelbookingsystem.repository;

import com.hotel.hotelbookingsystem.entity.ContactInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactInformationRepository extends JpaRepository<ContactInformation, Long> {
    
    Optional<ContactInformation> findByEmail(String email);
    
    boolean existsByEmail(String email);
}