package com.snapqueue.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// one row in the employee table = one user account
// role decides what they're allowed to do in the app
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // auto-incremented by the DB, we never set this manually

    private String fullName; // display name, e.g. "Arjun Patel"

    @Column(unique = true, nullable = false)
    private String email; // used as the login identifier in Spring Security, must be unique

    @Column(nullable = false)
    private String password; // stored as a bcrypt hash, never plain text

    @Column(unique = true, nullable = false)
    private String employeeId; // short code like EMP001, also works as a login identifier

    private String department; // just informational, e.g. "Engineering"

    // storing as VARCHAR(20) instead of letting Hibernate use MySQL ENUM
    // because MySQL ENUM caused truncation errors when the column was created with wrong size
    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "VARCHAR(20)")
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt; // hibernate sets this automatically on insert, we don't touch it

    // JPA needs a no-args constructor to work, don't remove this
    public Employee() {}

    // used in DataLoader and tests to quickly create an employee with all fields
    public Employee(String fullName, String email, String password,
                    String employeeId, String department, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.employeeId = employeeId;
        this.department = department;
        this.role = role;
    }

    // getters and setters below — nothing special, just standard boilerplate
    // Jackson uses these to serialize/deserialize JSON, JPA uses them too

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
