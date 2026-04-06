package com.CivicPulse.CivicPulseHub.entity;

import com.CivicPulse.CivicPulseHub.enums.ComplaintStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Data
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;

    @Column(length = 2000)
    private String description;

    private String officerProof;

    private String gpsLocation;
    private String manualArea;

    private String priority;
    private LocalDate deadline;
    private int rating;

    @Column(length = 1000)
    private String feedback;
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.SUBMITTED;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "resolution_proof")
    private String resolutionProof;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "citizen_id", nullable = false)
    @JsonIgnoreProperties({"password","complaints","enabled","authorities"})
    private User citizen;

    private String assignedOfficerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = ComplaintStatus.SUBMITTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}