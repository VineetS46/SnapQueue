package com.snapqueue.repository;

import com.snapqueue.model.Category;
import com.snapqueue.model.Feedback;
import com.snapqueue.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// all the DB queries we need for feedback
// Spring Data generates the SQL from the method names, no @Query needed
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // filter by category — not used much right now but useful for future filtering UI
    List<Feedback> findByCategory(Category category);

    // main one — used to get PENDING list for manager, APPROVED list for admin, etc.
    List<Feedback> findByStatus(Status status);

    // employee dashboard uses this to show "my submissions"
    // submittedBy stores the employeeId string like "EMP002"
    List<Feedback> findBySubmittedBy(String employeeId);

    // handy when you want to fetch multiple statuses at once
    // e.g. findByStatusIn(List.of(APPROVED, RESOLVED)) for admin overview
    List<Feedback> findByStatusIn(List<Status> statuses);
}
