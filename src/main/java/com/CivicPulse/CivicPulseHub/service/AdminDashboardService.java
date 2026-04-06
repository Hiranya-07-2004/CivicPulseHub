package com.CivicPulse.CivicPulseHub.service;

import com.CivicPulse.CivicPulseHub.dto.AdminDashboardDto;
import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus;
import com.CivicPulse.CivicPulseHub.repository.ComplaintRepository;
import com.CivicPulse.CivicPulseHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    public AdminDashboardDto getDashboardStats() {

        long totalUsers = userRepository.count();
        long citizens = userRepository.countByRole("CITIZEN");
        long officers = userRepository.countByRole("OFFICER");

        long totalComplaints = complaintRepository.count();

        long submitted = complaintRepository.countByStatus(ComplaintStatus.SUBMITTED);
        long pending = complaintRepository.countByStatus(ComplaintStatus.PENDING);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);

        return new AdminDashboardDto(
                totalUsers,
                citizens,
                officers,
                totalComplaints,
                submitted,
                pending,
                resolved
        );
    }

    // Fetch all complaints for admin table
    public List<Complaint> getAllComplaintsForAdmin() {
        return complaintRepository.findAll();
    }

    // 🔥 SAVE PRIORITY + DEADLINE FROM ADMIN DASHBOARD
    public Complaint assignDetails(Long id, String priority, String deadline) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setPriority(priority);

        if (deadline != null && !deadline.isEmpty()) {
            complaint.setDeadline(LocalDate.parse(deadline));
        }

        complaint.setStatus(ComplaintStatus.PENDING);

        return complaintRepository.save(complaint);
    }
}