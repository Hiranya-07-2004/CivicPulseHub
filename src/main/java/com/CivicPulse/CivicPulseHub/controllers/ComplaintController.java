package com.CivicPulse.CivicPulseHub.controllers;

import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus; // Added this import
import com.CivicPulse.CivicPulseHub.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping("/all")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @PutMapping("/{id}/assign-details")
    @Transactional
    public Complaint updateAssignmentDetails(@PathVariable Long id, @RequestBody Complaint updatedData) {

        Complaint complaint = complaintService.getComplaintById(id);

        // FIX: Check if status is null before converting to string, then compare
        if (updatedData.getStatus() != null && "REOPENED".equalsIgnoreCase(updatedData.getStatus().toString())) {

            // Set using the Enum constant
            complaint.setStatus(ComplaintStatus.REOPENED);

            // Citizen gets a fresh start - rating wiped
            complaint.setRating(0);
            complaint.setFeedback(null);

            // Officer must re-upload proof for the new resolution
            complaint.setResolutionProof(null);

        } else {
            // Set the status normally as provided in the request
            complaint.setStatus(updatedData.getStatus());
        }

        complaint.setPriority(updatedData.getPriority());
        complaint.setDeadline(updatedData.getDeadline());

        return complaintService.save(complaint);
    }

    @PostMapping(value = "/{governmentId}", consumes = {"multipart/form-data"})
    public Complaint submitComplaint(
            @RequestPart("complaint") Complaint complaint,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable String governmentId) throws IOException {

        if (file != null && !file.isEmpty()) {

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            complaint.setImageUrl("/uploads/" + fileName);
        }

        return complaintService.submitComplaint(complaint, governmentId);
    }

    @GetMapping("/citizen/{governmentId}")
    public List<Complaint> getComplaintsByCitizen(@PathVariable String governmentId) {
        return complaintService.getCitizenComplaints(governmentId);
    }

    @PutMapping(value = "/{id}/resolution-proof", consumes = {"multipart/form-data"})
    public Complaint uploadResolutionProof(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        if (file != null && !file.isEmpty()) {

            String fileName = "RES_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String proofUrl = "/uploads/" + fileName;

            return complaintService.saveResolutionProof(id, proofUrl);
        }

        throw new RuntimeException("File is required for resolution proof");
    }

    @DeleteMapping("/{id}")
    public void deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaintById(id);
    }
}