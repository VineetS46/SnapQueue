package com.snapqueue.controller;

import com.snapqueue.model.Feedback;
import com.snapqueue.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

// all feedback endpoints live here, all under /api/feedback
// this class is thin on purpose — actual rules and logic are in FeedbackService
// controller just receives the request and passes it along
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService service;

    public FeedbackController(FeedbackService service) {
        this.service = service;
    }

    // POST /api/feedback
    // employee submits a new issue — service checks they're actually an EMPLOYEE
    @PostMapping
    public ResponseEntity<Feedback> create(@RequestBody Feedback feedback, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFeedback(feedback, principal));
    }

    // GET /api/feedback/my
    // employee dashboard calls this to show "my submissions"
    @GetMapping("/my")
    public ResponseEntity<List<Feedback>> my(Principal principal) {
        return ResponseEntity.ok(service.getMyFeedbacks(principal));
    }

    // GET /api/feedback/pending
    // manager dashboard loads this to show what needs reviewing
    @GetMapping("/pending")
    public ResponseEntity<List<Feedback>> pending() {
        return ResponseEntity.ok(service.getPendingFeedbacks());
    }

    // GET /api/feedback/approved
    // admin dashboard loads this to show what's ready to resolve
    @GetMapping("/approved")
    public ResponseEntity<List<Feedback>> approved() {
        return ResponseEntity.ok(service.getApprovedFeedbacks());
    }

    // GET /api/feedback/all
    // admin can see everything regardless of status
    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> all() {
        return ResponseEntity.ok(service.getAllFeedbacks());
    }

    // PATCH /api/feedback/{id}/approve
    // manager approves a pending item — service enforces the role check
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Feedback> approve(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(service.approveFeedback(id, principal));
    }

    // PATCH /api/feedback/{id}/reject
    // manager rejects with a reason — reason comes in the request body as {"reason": "..."}
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Feedback> reject(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           Principal principal) {
        return ResponseEntity.ok(service.rejectFeedback(id, body.get("reason"), principal));
    }

    // PATCH /api/feedback/{id}/resolve
    // admin marks an approved item as done — service checks it's actually APPROVED first
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Feedback> resolve(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(service.resolveFeedback(id, principal));
    }
}
