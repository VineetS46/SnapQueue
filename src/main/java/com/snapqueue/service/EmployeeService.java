package com.snapqueue.service;

import com.snapqueue.exception.ResourceNotFoundException;
import com.snapqueue.model.Employee;
import com.snapqueue.repository.EmployeeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

// handles everything employee-related: registering, loading for login, looking up current user
// also implements UserDetailsService so Spring Security knows how to load users from our DB
@Service
public class EmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder; // bcrypt, injected from SecurityConfig

    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // called when someone hits POST /api/auth/register
    // checks for duplicates first, then hashes the password before saving
    public Employee registerEmployee(Employee emp) {
        // can't have two accounts with the same email
        if (employeeRepository.existsByEmail(emp.getEmail())) {
            throw new RuntimeException("Email already registered: " + emp.getEmail());
        }

        // can't have two accounts with the same employee ID either
        if (employeeRepository.existsByEmployeeId(emp.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + emp.getEmployeeId());
        }

        // never store plain text passwords
        emp.setPassword(passwordEncoder.encode(emp.getPassword()));

        return employeeRepository.save(emp);
    }

    // Spring Security calls this automatically during login
    // "username" here is whatever the user typed — could be email or employee ID
    // we support both by searching both fields at once
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Employee emp = employeeRepository.findByEmailOrEmployeeId(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("No employee found: " + identifier));

        // we always use email as the principal name going forward
        // Spring Security needs "ROLE_" prefix on authorities, so we add it here
        return new User(emp.getEmail(), emp.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + emp.getRole().name())));
    }

    // used all over the place — gets the full Employee object for whoever is logged in right now
    // principal.getName() gives us the email because that's what we set in loadUserByUsername
    public Employee getCurrentEmployee(Principal principal) {
        return employeeRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    // used during login to convert whatever the user typed (email or EMP001) into an Employee
    // needed because Spring Security's authenticate() needs the actual email, not the ID
    public Employee findByIdentifier(String identifier) {
        return employeeRepository.findByEmailOrEmployeeId(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + identifier));
    }
}
