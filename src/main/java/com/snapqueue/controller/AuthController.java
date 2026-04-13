package com.snapqueue.controller;

import com.snapqueue.model.Employee;
import com.snapqueue.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

// handles register, login, logout, and "who am I" endpoints
// all under /api/auth
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager; // Spring Security's thing for verifying credentials

    public AuthController(EmployeeService employeeService, AuthenticationManager authenticationManager) {
        this.employeeService = employeeService;
        this.authenticationManager = authenticationManager;
    }

    // POST /api/auth/register
    // open to everyone, no login needed
    // just passes the employee object to the service which handles validation + hashing
    @PostMapping("/register")
    public ResponseEntity<Employee> register(@RequestBody Employee employee) {
        Employee saved = employeeService.registerEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // POST /api/auth/login
    // accepts either email or employee ID in the "identifier" field
    // if valid, creates a session and returns the employee's profile
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String identifier = credentials.get("identifier");
        String password = credentials.get("password");

        // first we need to find the actual employee so we can get their email
        // Spring Security's authenticate() needs the email, not the employee ID
        Employee found;
        try {
            found = employeeService.findByIdentifier(identifier);
        } catch (Exception e) {
            // no employee found with that email or ID
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        try {
            // now let Spring Security verify the password against the bcrypt hash
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(found.getEmail(), password)
            );

            // password was correct — store the auth in the security context and session
            // this is what keeps the user "logged in" for future requests
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = request.getSession(true); // create session if there isn't one
            session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

            // return the employee's profile so the frontend can store it and know the role
            Employee emp = employeeService.getCurrentEmployee(auth::getName);
            return ResponseEntity.ok(Map.of(
                    "id", emp.getId(),
                    "fullName", emp.getFullName(),
                    "employeeId", emp.getEmployeeId(),
                    "department", emp.getDepartment(),
                    "role", emp.getRole().name(), // "EMPLOYEE", "MANAGER", or "ADMIN"
                    "email", emp.getEmail()
            ));
        } catch (AuthenticationException ex) {
            // wrong password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }
    }

    // POST /api/auth/logout
    // kills the session and clears the security context
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // false = don't create one if it doesn't exist

        if (session != null) session.invalidate(); // wipe the session

        SecurityContextHolder.clearContext(); // clear in-memory auth for this thread too

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // GET /api/auth/me
    // frontend calls this on page load to check who's logged in
    // Spring Security returns 401 automatically if there's no valid session
    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        Employee emp = employeeService.getCurrentEmployee(principal);
        return ResponseEntity.ok(Map.of(
                "id", emp.getId(),
                "fullName", emp.getFullName(),
                "employeeId", emp.getEmployeeId(),
                "department", emp.getDepartment(),
                "role", emp.getRole().name(),
                "email", emp.getEmail()
        ));
    }
}
