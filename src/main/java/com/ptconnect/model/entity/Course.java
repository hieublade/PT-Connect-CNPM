package com.ptconnect.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Course implements Serializable {
    private Long id;
    private Long ptId;
    private String title;
    private String description;
    private int totalSessions;
    private int validityDays;
    private BigDecimal basePrice;
    private String status;
    private LocalDateTime createdAt;
    private ArrayList<Enrollment> enrollments;

    public Course() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPtId() { return ptId; }
    public void setPtId(Long ptId) { this.ptId = ptId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ArrayList<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(ArrayList<Enrollment> enrollments) { this.enrollments = enrollments; }
}
