package com.snapqueue.config;

import com.snapqueue.model.Category;
import com.snapqueue.model.Employee;
import com.snapqueue.model.Feedback;
import com.snapqueue.model.Role;
import com.snapqueue.model.Status;
import com.snapqueue.repository.EmployeeRepository;
import com.snapqueue.repository.FeedbackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// runs once on startup to seed default accounts and sample feedback
// CommandLineRunner means Spring calls run() automatically after the app is ready
// each block checks if the data already exists before inserting — safe to restart
@Component
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final FeedbackRepository feedbackRepository;
    private final PasswordEncoder passwordEncoder; // need this to hash the default passwords

    public DataLoader(EmployeeRepository employeeRepository,
                      FeedbackRepository feedbackRepository,
                      PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.feedbackRepository = feedbackRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // seed admin account — login with admin123@gmail.com / admin123
        // checking by employeeId not email because that's the unique business key
        if (!employeeRepository.existsByEmployeeId("ADMIN001")) {
            employeeRepository.save(new Employee(
                    "Admin",
                    "admin123@gmail.com",
                    passwordEncoder.encode("admin123"), // hash it before saving
                    "ADMIN001",
                    "Administration",
                    Role.ADMIN
            ));
        }

        // seed manager account — login with EMP001 / password123
        if (!employeeRepository.existsByEmployeeId("EMP001")) {
            employeeRepository.save(new Employee(
                    "Priya Shah",
                    "priya@snapqueue.com",
                    passwordEncoder.encode("password123"),
                    "EMP001",
                    "Engineering",
                    Role.MANAGER
            ));
        }

        // seed employee account — login with EMP002 / password123
        if (!employeeRepository.existsByEmployeeId("EMP002")) {
            employeeRepository.save(new Employee(
                    "Arjun Patel",
                    "arjun@snapqueue.com",
                    passwordEncoder.encode("password123"),
                    "EMP002",
                    "Engineering",
                    Role.EMPLOYEE
            ));
        }

        // seed sample feedback only if the table is empty
        // this gives us data to look at on all three dashboards without manually submitting
        if (feedbackRepository.count() == 0) {

            // pending — shows up on manager dashboard waiting for action
            Feedback f1 = new Feedback(
                    "Build pipeline keeps failing on staging",
                    Category.BUG,
                    "EMP002",
                    "Arjun Patel",
                    Status.PENDING
            );

            // approved — shows up on admin dashboard ready to resolve
            Feedback f2 = new Feedback(
                    "Need access to production logs",
                    Category.SUGGESTION,
                    "EMP002",
                    "Arjun Patel",
                    Status.APPROVED
            );

            // rejected — shows up on employee dashboard with a reason
            Feedback f3 = new Feedback(
                    "Request for new design tools license",
                    Category.SUGGESTION,
                    "EMP002",
                    "Arjun Patel",
                    Status.REJECTED
            );
            f3.setRejectionReason("Budget not available this quarter"); // set separately since constructor doesn't take it

            // resolved — full lifecycle complete, good for demo
            Feedback f4 = new Feedback(
                    "Login page broken on mobile",
                    Category.BUG,
                    "EMP002",
                    "Arjun Patel",
                    Status.RESOLVED
            );

            feedbackRepository.saveAll(List.of(f1, f2, f3, f4)); // one DB call instead of four
        }
    }
}
