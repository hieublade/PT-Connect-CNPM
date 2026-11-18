package com.ptconnect.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Attendance implements Serializable {
    private Long id;
    private Long bookingId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String verificationMethod;
    private String otpCode;
    private LocalDateTime otpExpiresAt;
    private Boolean studentConfirmed;
    private Boolean ptConfirmed;
    private String status;

    public Attendance() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getOtpExpiresAt() { return otpExpiresAt; }
    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }

    public Boolean getStudentConfirmed() { return studentConfirmed; }
    public void setStudentConfirmed(Boolean studentConfirmed) { this.studentConfirmed = studentConfirmed; }

    public Boolean getPtConfirmed() { return ptConfirmed; }
    public void setPtConfirmed(Boolean ptConfirmed) { this.ptConfirmed = ptConfirmed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
