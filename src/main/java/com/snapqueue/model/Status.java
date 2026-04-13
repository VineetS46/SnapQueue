package com.snapqueue.model;

// tracks where a feedback item is in its lifecycle
// normal flow: PENDING -> APPROVED -> RESOLVED
// if manager says no: PENDING -> REJECTED (done, nothing more happens)
public enum Status {
    PENDING,   // just submitted, waiting for manager to look at it
    APPROVED,  // manager said yes, now admin needs to act on it
    REJECTED,  // manager said no, feedback is closed with a reason
    RESOLVED   // admin marked it done, full cycle complete
}
