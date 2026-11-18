package com.ptconnect.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class User implements Serializable {
    private Long id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private String role;
    private String status;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Wallet wallet;
    private ArrayList<Cancellation> cancellations;
    private ArrayList<Booking> bookings;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public ArrayList<Cancellation> getCancellations() { return cancellations; }
    public void setCancellations(ArrayList<Cancellation> cancellations) { this.cancellations = cancellations; }

    public ArrayList<Booking> getBookings() { return bookings; }
    public void setBookings(ArrayList<Booking> bookings) { this.bookings = bookings; }
}
