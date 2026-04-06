package com.CivicPulse.CivicPulseHub.repository;

import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Get complaints by citizen government ID
    List<Complaint> findByCitizen_GovernmentId(String governmentId);

    // Get complaints assigned to officer
    List<Complaint> findByAssignedOfficerId(String assignedOfficerId);

    // Get complaints by status
    List<Complaint> findByStatus(ComplaintStatus status);

    // Admin dashboard counts
    long countByStatus(ComplaintStatus status);
}