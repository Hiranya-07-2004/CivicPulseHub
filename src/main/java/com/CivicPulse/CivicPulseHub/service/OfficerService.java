package com.CivicPulse.CivicPulseHub.service;

import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.entity.Feedback;
import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus;
import com.CivicPulse.CivicPulseHub.repository.ComplaintRepository;
import com.CivicPulse.CivicPulseHub.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfficerService {

    private final ComplaintRepository complaintRepository;
    private final FeedbackRepository feedbackRepository;

    public List<Complaint> getOfficerComplaints(String officerId) {
        return complaintRepository.findByAssignedOfficerId(officerId);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    /**
     * Updates complaint status and saves resolution proof.
     * @Transactional ensures the Admin Panel sees the update immediately.
     */
    @Transactional
    public Complaint updateComplaintStatus(Long id, String status, MultipartFile proof) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ID: " + id));

        // Update Status - Safe conversion to Enum
        try {
            complaint.setStatus(ComplaintStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status + ". Must match Enum values.");
        }

        // Handle File Upload if proof is provided
        if (proof != null && !proof.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + proof.getOriginalFilename();
                Path uploadPath = Paths.get("uploads");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, proof.getBytes());

                // Store relative web path in DB
                complaint.setResolutionProof("/uploads/" + fileName);

            } catch (IOException e) {
                throw new RuntimeException("FileSystem Error: Failed to save resolution proof.");
            }
        }

        // Commit changes to DB
        return complaintRepository.save(complaint);
    }

    public void notifyCitizen(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        // Placeholder for actual Notification Logic (Email/SMS)
        System.out.println("LOG: Notification triggered for Ticket #" + id);
    }
}