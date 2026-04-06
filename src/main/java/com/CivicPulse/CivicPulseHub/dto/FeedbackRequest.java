package com.CivicPulse.CivicPulseHub.dto;

import lombok.Data;

@Data
public class FeedbackRequest {
    private Long complaintId;
    private String governmentId;
    private int rating;
    private String comments;
}