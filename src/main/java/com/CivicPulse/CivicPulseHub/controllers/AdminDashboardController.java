package com.CivicPulse.CivicPulseHub.controllers;

import com.CivicPulse.CivicPulseHub.dto.AdminDashboardDto;
import com.CivicPulse.CivicPulseHub.entity.Complaint;
import com.CivicPulse.CivicPulseHub.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public AdminDashboardDto getDashboard() {
        return adminDashboardService.getDashboardStats();
    }

    @GetMapping("/complaints")
    public List<Complaint> getAllComplaints() {
        return adminDashboardService.getAllComplaintsForAdmin();
    }

    @PutMapping("/complaints/{id}/assign-details")
    public Complaint assignDetails(
            @PathVariable Long id,
            @RequestBody Map<String,String> request
    ){
        return adminDashboardService.assignDetails(
                id,
                request.get("priority"),
                request.get("deadline")
        );
    }
}