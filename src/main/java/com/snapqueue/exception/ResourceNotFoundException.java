package com.snapqueue.exception;

// thrown when we look something up by ID and it doesn't exist
// GlobalExceptionHandler catches this and turns it into a 404 JSON response
// extends RuntimeException so we don't have to declare it in every method signature
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message); // just pass the message up, e.g. "Feedback not found with id: 42"
    }
}
