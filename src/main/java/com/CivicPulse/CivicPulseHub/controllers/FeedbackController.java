package com.CivicPulse.CivicPulseHub.controllers;

import com.CivicPulse.CivicPulseHub.dto.FeedbackRequest;
import com.CivicPulse.CivicPulseHub.entity.*;
import com.CivicPulse.CivicPulseHub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Controller responsible for managing Citizen feedback and ratings
 * for resolved grievances in the CivicPulse Hub.
 */
@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    /**
     * Submits or updates feedback for a specific complaint.
     * If the complaint was reopened, this method updates the existing record.
     */
    @PostMapping
    @Transactional
    public String submitFeedback(@RequestBody FeedbackRequest request) {

        // 1. Fetch the complaint and verify its existence
        Complaint complaint = complaintRepository.findById(request.getComplaintId())
                .orElseThrow(() -> new RuntimeException("The requested complaint was not found."));

        // 2. Fetch the citizen based on the Government ID provided in the DTO
        User citizen = userRepository.findByGovernmentId(request.getGovernmentId())
                .orElseThrow(() -> new RuntimeException("Citizen record not found."));

        // 3. Check for existing feedback records for this specific complaint
        // This allows us to handle 'Reopened' cases by updating rather than failing
        List<Feedback> existingFeedbacks = feedbackRepository.findByComplaintId(request.getComplaintId());

        Feedback feedback;

        if (!existingFeedbacks.isEmpty()) {

            // If feedback exists, we retrieve the first record to update it
            feedback = existingFeedbacks.get(0);

        } else {

            // If no previous feedback exists, we initialize a new Feedback object
            feedback = new Feedback();
            feedback.setComplaint(complaint);
            feedback.setCitizen(citizen);

        }

        // 4. Update the fields with the fresh data from the request
        feedback.setRating(request.getRating());
        feedback.setComments(request.getComments());

        // 5. Persist the feedback record to the database
        feedbackRepository.save(feedback);

        // 6. Synchronize the summary data back to the main Complaint record
        // This ensures the Dashboard and Admin views show the latest rating
        complaint.setRating(request.getRating());
        complaint.setFeedback(request.getComments());

        // 7. Save the updated complaint state
        complaintRepository.save(complaint);

        return "Feedback successfully recorded and synchronized.";
    }
}