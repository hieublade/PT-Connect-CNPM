package com.ptconnect.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PTProfile implements Serializable {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String bio;
    private int experienceYears;
    private String locationPricingNote;
    private String certificateUrls;
    private BigDecimal averageRating;
    private int totalReviews;
    private String verificationStatus;
    private BigDecimal minCoursePrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ArrayList<PTLocation> locations;
    private ArrayList<AvailabilitySlot> availabilitySlots;
    private ArrayList<Review> reviews;
    private ArrayList<Course> courses;

    public PTProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getLocationPricingNote() { return locationPricingNote; }
    public void setLocationPricingNote(String locationPricingNote) { this.locationPricingNote = locationPricingNote; }

    public String getCertificateUrls() { return certificateUrls; }
    public void setCertificateUrls(String certificateUrls) { this.certificateUrls = certificateUrls; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public BigDecimal getMinCoursePrice() { return minCoursePrice; }
    public void setMinCoursePrice(BigDecimal minCoursePrice) { this.minCoursePrice = minCoursePrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public ArrayList<PTLocation> getLocations() { return locations; }
    public void setLocations(ArrayList<PTLocation> locations) { this.locations = locations; }

    public ArrayList<AvailabilitySlot> getAvailabilitySlots() { return availabilitySlots; }
    public void setAvailabilitySlots(ArrayList<AvailabilitySlot> availabilitySlots) { this.availabilitySlots = availabilitySlots; }

    public ArrayList<Review> getReviews() { return reviews; }
    public void setReviews(ArrayList<Review> reviews) { this.reviews = reviews; }

    public ArrayList<Course> getCourses() { return courses; }
    public void setCourses(ArrayList<Course> courses) { this.courses = courses; }
}
