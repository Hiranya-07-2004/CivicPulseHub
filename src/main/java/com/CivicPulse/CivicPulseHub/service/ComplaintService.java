package com.CivicPulse.CivicPulseHub.service;

import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.entity.User;
import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus;
import com.CivicPulse.CivicPulseHub.repository.ComplaintRepository;
import com.CivicPulse.CivicPulseHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    // --- ADDED METHODS TO RESOLVE CONTROLLER ERRORS ---

    /**
     * Fetches a single complaint by its ID. Used for assignment and updates.
     */
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    /**
     * Standard save method used for syncing status and feedback updates.
     */
    @Transactional
    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    // --- EXISTING METHODS (UNCHANGED) ---

    // SUBMIT COMPLAINT with Automated Mapping
    public Complaint submitComplaint(Complaint complaint, String governmentId) {

        governmentId = governmentId.trim().toUpperCase();

        User citizen = userRepository.findByGovernmentId(governmentId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        complaint.setCitizen(citizen);

        // --- AUTOMATIC OFFICER MAPPING LOGIC ---
        String rawCategory = complaint.getCategory();

        System.out.println("DEBUG: Category Received: [" + rawCategory + "]");

        String assignedId = "OFFICER001"; // Default fallback

        if (rawCategory != null) {
            String cleanCategory = rawCategory.trim();

            assignedId = switch (cleanCategory) {
                case "Water Leakage", "No Water Supply", "Drainage Overflow"
                        -> "OFFICER001";

                case "Potholes / Road Damage", "Broken Footpath", "Traffic Signal Not Working"
                        -> "OFFICER002";

                case "Garbage Not Collected", "Illegal Dumping", "Public Toilet Unclean"
                        -> "OFFICER003";

                case "Street Light Not Working", "Fallen Electric Pole", "Power Line Sparking"
                        -> "OFFICER004";

                case "Tree Fallen on Road"
                        -> "OFFICER005";

                case "Unauthorized Construction"
                        -> "OFFICER006";

                case "Stray Animals Issue"
                        -> "OFFICER007";

                case "Noise Pollution"
                        -> "OFFICER008";

                case "Street Flooding", "other"
                        -> "OFFICER009";

                default -> {
                    System.out.println("WARNING: No match for [" + cleanCategory + "]. Defaulting to Officer 1.");
                    yield "OFFICER001";
                }
            };
        }

        complaint.setAssignedOfficerId(assignedId);

        // Update status to ASSIGNED
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        return complaintRepository.save(complaint);
    }

    // GET COMPLAINTS OF A CITIZEN
    public List<Complaint> getCitizenComplaints(String governmentId) {
        governmentId = governmentId.trim().toUpperCase();
        return complaintRepository.findByCitizen_GovernmentId(governmentId);
    }

    // ASSIGN OFFICER TO COMPLAINT (Manual override)
    public Complaint assignComplaint(Long complaintId, String officerId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setAssignedOfficerId(officerId);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        return complaintRepository.save(complaint);
    }

    // SAVE OFFICER RESOLUTION PROOF
    public Complaint saveResolutionProof(Long id, String proofUrl) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setResolutionProof(proofUrl);
        complaint.setStatus(ComplaintStatus.RESOLVED);

        return complaintRepository.save(complaint);
    }

    // DELETE COMPLAINT
    public void deleteComplaintById(Long id) {
        if (!complaintRepository.existsById(id)) {
            throw new RuntimeException("Complaint not found");
        }
        complaintRepository.deleteById(id);
    }

    // GET ALL COMPLAINTS (ADMIN)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
}