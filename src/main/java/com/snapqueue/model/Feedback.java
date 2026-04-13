package com.snapqueue.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// one row = one feedback item submitted by an employee
// goes through: PENDING -> APPROVED -> RESOLVED  (or REJECTED if manager says no)
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB auto-generates this

    @Column(columnDefinition = "TEXT")
    private String message; // the actual feedback text, using TEXT so there's no length limit

    // same VARCHAR trick as Employee.role — avoids MySQL truncation issues
    @Enumerated(EnumType.STRING)
    @Column(name = "category", columnDefinition = "VARCHAR(20)")
    private Category category; // BUG, SUGGESTION, or APPRECIATION

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    private Status status = Status.PENDING; // always starts as PENDING, service changes it later

    private String submittedBy;     // employee ID of whoever submitted this, e.g. "EMP002"
    private String submittedByName; // their full name — stored here so we don't need a JOIN to show it

    @Column(columnDefinition = "TEXT")
    private String rejectionReason; // only filled in when manager rejects, null otherwise

    @CreationTimestamp
    private LocalDateTime createdAt; // set by hibernate on insert

    // JPA needs this
    public Feedback() {}

    // used in DataLoader to seed sample data
    public Feedback(String message, Category category, String submittedBy,
                    String submittedByName, Status status) {
        this.message = message;
        this.category = category;
        this.submittedBy = submittedBy;
        this.submittedByName = submittedByName;
        this.status = status;
    }

    // standard getters/setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getSubmittedByName() { return submittedByName; }
    public void setSubmittedByName(String submittedByName) { this.submittedByName = submittedByName; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
