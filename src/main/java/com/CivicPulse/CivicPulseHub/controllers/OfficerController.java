package com.CivicPulse.CivicPulseHub.controllers;

import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.entity.Feedback;
import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus;
import com.CivicPulse.CivicPulseHub.service.OfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/officer")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OfficerController {

    private final OfficerService officerService;

    // Fetch complaints assigned to a specific officer
    @GetMapping("/complaints/{officerId}")
    public List<Complaint> getOfficerComplaints(@PathVariable String officerId) {
        return officerService.getOfficerComplaints(officerId);
    }

    // Handles status change and file upload
    @PutMapping("/complaints/{id}/status")
    public ResponseEntity<Complaint> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status, // Kept as String to handle manual toUpper in Service
            @RequestParam(value = "proof", required = false) MultipartFile proof) {

        Complaint updated = officerService.updateComplaintStatus(id, status, proof);
        return ResponseEntity.ok(updated);
    }

    // Fetch all citizen feedback
    @GetMapping("/feedback")
    public List<Feedback> getOfficerFeedback() {
        return officerService.getAllFeedback();
    }

    // Trigger notification to citizen
    @PostMapping("/notify/{id}")
    public ResponseEntity<String> notifyCitizen(@PathVariable Long id) {
        officerService.notifyCitizen(id);
        return ResponseEntity.ok("{\"message\": \"Citizen notified successfully\"}");
    }
}