package com.CivicPulse.CivicPulseHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardDto {

    private long totalUsers;
    private long totalCitizens;
    private long totalOfficers;

    private long totalComplaints;
    private long submitted;
    private long pending;
    private long resolved;
}