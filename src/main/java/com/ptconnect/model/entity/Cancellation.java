package com.ptconnect.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cancellation implements Serializable {
    private Long id;
    private Long cancelledById;
    private Long bookingId;
    private String cancelledByRole;
    private String reason;
    private BigDecimal hoursBeforeSession;
    private BigDecimal refundAmount;
    private BigDecimal compensationAmount;
    private BigDecimal penaltyAmount;
    private String evidenceUrl;
    private String status;
    private LocalDateTime cancelledAt;

    public Cancellation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCancelledById() { return cancelledById; }
    public void setCancelledById(Long cancelledById) { this.cancelledById = cancelledById; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getCancelledByRole() { return cancelledByRole; }
    public void setCancelledByRole(String cancelledByRole) { this.cancelledByRole = cancelledByRole; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getHoursBeforeSession() { return hoursBeforeSession; }
    public void setHoursBeforeSession(BigDecimal hoursBeforeSession) { this.hoursBeforeSession = hoursBeforeSession; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public BigDecimal getCompensationAmount() { return compensationAmount; }
    public void setCompensationAmount(BigDecimal compensationAmount) { this.compensationAmount = compensationAmount; }

    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public String getEvidenceUrl() { return evidenceUrl; }
    public void setEvidenceUrl(String evidenceUrl) { this.evidenceUrl = evidenceUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
