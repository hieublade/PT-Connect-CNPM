package com.ptconnect.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AvailabilitySlot implements Serializable {
    private Long id;
    private Long ptId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdAt;
    private ArrayList<Booking> bookings;

    public AvailabilitySlot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPtId() { return ptId; }
    public void setPtId(Long ptId) { this.ptId = ptId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ArrayList<Booking> getBookings() { return bookings; }
    public void setBookings(ArrayList<Booking> bookings) { this.bookings = bookings; }
}
