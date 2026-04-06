package com.CivicPulse.CivicPulseHub.repository;

import com.CivicPulse.CivicPulseHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByGovernmentId(String governmentId);

    Optional<User> findByUsernameAndGovernmentId(String username, String governmentId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByGovernmentId(String governmentId);

    // ✅ ADD THIS FOR ADMIN DASHBOARD
    long countByRole(String role);
}