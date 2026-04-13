package com.snapqueue.model;

// 3 roles in this app. each one can do different things.
// EMPLOYEE submits, MANAGER reviews, ADMIN closes it out.
public enum Role {
    EMPLOYEE,  // regular staff, can only submit feedback
    MANAGER,   // team lead, approves or rejects what employees submit
    ADMIN      // has full access, resolves approved feedback
}
