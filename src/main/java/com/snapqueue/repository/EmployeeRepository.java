package com.snapqueue.repository;

import com.snapqueue.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data JPA generates all the SQL for us based on method names
// we just declare what we need and it figures out the query
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // used after login to load the full employee object from their email
    Optional<Employee> findByEmail(String email);

    // used when someone looks up by employee ID instead of email
    Optional<Employee> findByEmployeeId(String employeeId);

    // this is the main one for login — user can type either email or EMP001 style ID
    // Spring generates: WHERE email = ? OR employee_id = ?
    Optional<Employee> findByEmailOrEmployeeId(String email, String employeeId);

    // registration check — don't allow two accounts with the same email
    boolean existsByEmail(String email);

    // registration check + DataLoader check — don't allow duplicate employee IDs
    boolean existsByEmployeeId(String employeeId);
}
