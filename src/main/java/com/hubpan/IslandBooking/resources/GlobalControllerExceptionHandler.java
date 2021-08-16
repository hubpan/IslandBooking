package com.hubpan.IslandBooking.resources;

import com.hubpan.IslandBooking.api.error.ApiErrorResponse;
import com.hubpan.IslandBooking.api.error.FieldError;
import com.hubpan.IslandBooking.core.exceptions.ReservationTimeViolationException;
import com.hubpan.IslandBooking.core.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        Object errorBody = body != null ? body : ex.getMessage();
        return new ResponseEntity<>(
                new ApiErrorResponse(Instant.now(),
                        errorBody,
                        status.value()),
                headers,
                status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, null, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    protected ResponseEntity<Object> handleOptimisticLockingFailures(ObjectOptimisticLockingFailureException ex, WebRequest request) {
        return handleExceptionInternal(ex, "Please try again", null, HttpStatus.TOO_MANY_REQUESTS, request);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldError(e.getField(), e.getDefaultMessage())).collect(Collectors.toList());
        return handleExceptionInternal(ex, errorMessages, null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ReservationTimeViolationException.class)
    protected ResponseEntity<Object> handleReservationTimeViolation(ReservationTimeViolationException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, null, HttpStatus.BAD_REQUEST, request);
    }
}
