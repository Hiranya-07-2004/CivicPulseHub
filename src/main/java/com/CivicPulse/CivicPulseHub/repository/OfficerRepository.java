package com.CivicPulse.CivicPulseHub.repository;

import com.CivicPulse.CivicPulseHub.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficerRepository extends JpaRepository<Officer, Long> {
}