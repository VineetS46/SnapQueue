package com.snapqueue.service;

import com.snapqueue.exception.ResourceNotFoundException;
import com.snapqueue.model.Employee;
import com.snapqueue.model.Feedback;
import com.snapqueue.model.Role;
import com.snapqueue.model.Status;
import com.snapqueue.repository.FeedbackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

// all the business logic for feedback lives here
// the controller just calls these methods, the actual rules are enforced here
@Service
public class FeedbackService {

    private final FeedbackRepository repository;
    private final EmployeeService employeeService; // need this to check who's calling and what role they have

    public FeedbackService(FeedbackRepository repository, EmployeeService employeeService) {
        this.repository = repository;
        this.employeeService = employeeService;
    }

    // only employees can submit feedback, managers and admins can't
    // we also force status to PENDING here — client can't sneak in a different status
    public Feedback createFeedback(Feedback feedback, Principal principal) {
        Employee emp = employeeService.getCurrentEmployee(principal);

        // block managers and admins from submitting
        if (emp.getRole() != Role.EMPLOYEE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employees can submit feedback");
        }

        // always start as PENDING regardless of what the request body says
        feedback.setStatus(Status.PENDING);

        // stamp who submitted it so we can show it on their dashboard later
        feedback.setSubmittedBy(emp.getEmployeeId());
        feedback.setSubmittedByName(emp.getFullName());

        return repository.save(feedback);
    }

    // manager hits the approve button — we check role, check status, then flip it to APPROVED
    public Feedback approveFeedback(Long id, Principal principal) {
        Employee emp = employeeService.getCurrentEmployee(principal);

        if (emp.getRole() != Role.MANAGER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only managers can approve feedback");
        }

        Feedback feedback = getById(id); // throws 404 if it doesn't exist

        // can only approve something that's still waiting — not already approved/rejected
        if (feedback.getStatus() != Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING feedback can be approved");
        }

        feedback.setStatus(Status.APPROVED);
        return repository.save(feedback);
    }

    // same as approve but sets REJECTED and saves the reason why
    public Feedback rejectFeedback(Long id, String reason, Principal principal) {
        Employee emp = employeeService.getCurrentEmployee(principal);

        if (emp.getRole() != Role.MANAGER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only managers can reject feedback");
        }

        Feedback feedback = getById(id);

        if (feedback.getStatus() != Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING feedback can be rejected");
        }

        feedback.setStatus(Status.REJECTED);
        feedback.setRejectionReason(reason); // reason comes from the request body
        return repository.save(feedback);
    }

    // admin resolves it — but only after a manager has already approved it
    // can't skip straight from PENDING to RESOLVED
    public Feedback resolveFeedback(Long id, Principal principal) {
        Employee emp = employeeService.getCurrentEmployee(principal);

        if (emp.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can resolve feedback");
        }

        Feedback feedback = getById(id);

        // must be APPROVED first — admin can't resolve something the manager hasn't reviewed
        if (feedback.getStatus() != Status.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only APPROVED feedback can be resolved");
        }

        feedback.setStatus(Status.RESOLVED);
        return repository.save(feedback);
    }

    // employee dashboard — show me only my own submissions
    public List<Feedback> getMyFeedbacks(Principal principal) {
        Employee emp = employeeService.getCurrentEmployee(principal);
        return repository.findBySubmittedBy(emp.getEmployeeId());
    }

    // manager dashboard loads this to show what needs reviewing
    public List<Feedback> getPendingFeedbacks() {
        return repository.findByStatus(Status.PENDING);
    }

    // admin dashboard loads this to show what's ready to resolve
    public List<Feedback> getApprovedFeedbacks() {
        return repository.findByStatus(Status.APPROVED);
    }

    // admin can also see everything — all statuses, all employees
    public List<Feedback> getAllFeedbacks() {
        return repository.findAll();
    }

    // private helper so we don't repeat the findById + orElseThrow in every method
    private Feedback getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
    }
}
