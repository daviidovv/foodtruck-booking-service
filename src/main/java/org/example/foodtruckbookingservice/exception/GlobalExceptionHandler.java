package org.example.foodtruckbookingservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler following RFC 7807 Problem Details format.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_BASE_URI = "https://api.foodtruck-booking.de/errors/";

    @ExceptionHandler(LocationNotFoundException.class)
    public ProblemDetail handleLocationNotFound(LocationNotFoundException ex) {
        log.warn("Location not found: {}", ex.getLocationId());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "not-found"));
        problem.setTitle("Location Not Found");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("locationId", ex.getLocationId());
        return problem;
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ProblemDetail handleReservationNotFound(ReservationNotFoundException ex) {
        log.warn("Reservation not found: {}", ex.getReservationId());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "not-found"));
        problem.setTitle("Reservation Not Found");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("reservationId", ex.getReservationId());
        return problem;
    }

    @ExceptionHandler(CapacityExceededException.class)
    public ProblemDetail handleCapacityExceeded(CapacityExceededException ex) {
        log.warn("Capacity exceeded: requested={}, available={}", ex.getRequested(), ex.getAvailable());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "capacity-exceeded"));
        problem.setTitle("Capacity Exceeded");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("requested", ex.getRequested());
        problem.setProperty("availableCapacity", ex.getAvailable());
        return problem;
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ProblemDetail handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        log.warn("Invalid status transition: {} -> {}", ex.getCurrentStatus(), ex.getRequestedStatus());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "invalid-status-transition"));
        problem.setTitle("Invalid Status Transition");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("currentStatus", ex.getCurrentStatus());
        problem.setProperty("requestedStatus", ex.getRequestedStatus());
        return problem;
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "business-rule-violation"));
        problem.setTitle("Business Rule Violation");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("ruleCode", ex.getRuleCode());
        return problem;
    }

    @ExceptionHandler(LocationNameAlreadyExistsException.class)
    public ProblemDetail handleLocationNameExists(LocationNameAlreadyExistsException ex) {
        log.warn("Location name already exists: {}", ex.getName());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage());
        problem.setType(URI.create(ERROR_BASE_URI + "conflict"));
        problem.setTitle("Location Name Already Exists");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("name", ex.getName());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed");
        problem.setType(URI.create(ERROR_BASE_URI + "validation-error"));
        problem.setTitle("Validation Failed");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        problem.setType(URI.create(ERROR_BASE_URI + "internal-error"));
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    private Map<String, String> mapFieldError(FieldError error) {
        return Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
        );
    }
}
