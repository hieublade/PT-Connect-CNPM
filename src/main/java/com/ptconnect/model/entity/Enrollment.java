package com.ptconnect.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Enrollment implements Serializable {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long ptId;
    private String courseTitle;
    private String ptName;
    private String studentName;
    private User student;
    private String trainingLocation;
    private BigDecimal finalPrice;
    private Integer totalSessions;
    private Integer remainingSessions;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contractUrl;
    private String status;
    private LocalDateTime createdAt;
    private ArrayList<Payment> payments;
    private ArrayList<WorkoutPlan> workoutPlans;
    private ArrayList<Booking> bookings;

    public Enrollment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getPtId() { return ptId; }
    public void setPtId(Long ptId) { this.ptId = ptId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getPtName() { return ptName; }
    public void setPtName(String ptName) { this.ptName = ptName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public String getTrainingLocation() { return trainingLocation; }
    public void setTrainingLocation(String trainingLocation) { this.trainingLocation = trainingLocation; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }

    public Integer getRemainingSessions() { return remainingSessions; }
    public void setRemainingSessions(Integer remainingSessions) { this.remainingSessions = remainingSessions; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getContractUrl() { return contractUrl; }
    public void setContractUrl(String contractUrl) { this.contractUrl = contractUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ArrayList<Payment> getPayments() { return payments; }
    public void setPayments(ArrayList<Payment> payments) { this.payments = payments; }

    public ArrayList<WorkoutPlan> getWorkoutPlans() { return workoutPlans; }
    public void setWorkoutPlans(ArrayList<WorkoutPlan> workoutPlans) { this.workoutPlans = workoutPlans; }

    public ArrayList<Booking> getBookings() { return bookings; }
    public void setBookings(ArrayList<Booking> bookings) { this.bookings = bookings; }
}
