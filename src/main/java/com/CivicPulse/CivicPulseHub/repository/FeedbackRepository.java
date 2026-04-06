package com.CivicPulse.CivicPulseHub.repository;

import com.CivicPulse.CivicPulseHub.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByComplaintId(Long complaintId);
    List<Feedback> findByCitizen_GovernmentId(String governmentId);
}