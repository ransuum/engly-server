package com.engly.engly_server.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGeneralExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", ex);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(TypeMismatchException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getPropertyName(), ex.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(EntityAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Entity mismatch", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage());
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AuthenticationCredentialsNotFoundException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "You need to authorize!", ex.getMessage());
    }

    @ExceptionHandler(WebSocketException.class)
    public ResponseEntity<ExceptionResponse> handleWebSocketException(WebSocketException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid token", ex.getMessage());
    }

    @ExceptionHandler(SignInException.class)
    public ResponseEntity<ExceptionResponse> handleSignInException(SignInException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Password or email is not correct", ex.getMessage());
    }

    @ExceptionHandler(PasswordGeneratorException.class)
    public ResponseEntity<ExceptionResponse> handlePasswordGeneratorException(PasswordGeneratorException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Password Generate Error", ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {
        return buildResponse((HttpStatus) ex.getStatusCode(), ex.getReason(), ex);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ExceptionResponse> handlePropertyReferenceException(PropertyReferenceException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid data", ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Resource not found", ex);
    }

    @ExceptionHandler(GenerateTokenException.class)
    public ResponseEntity<ExceptionResponse> handleNotificationException(GenerateTokenException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot generate token for verify email", ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException() {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate entry", "A record with the same unique identifier already exists");
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleTokenNotFoundException(TokenNotFoundException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid token", ex);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ExceptionResponse> handleFieldValidationException(FieldValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return buildResponse("Validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return buildResponse("Constraint violation", errors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Max upload size exceeded max size = %d".formatted(ex.getMaxUploadSize()), ex);
    }

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String message, Exception ex) {
        return ResponseEntity.status(status)
                .body(new ExceptionResponse(message, status.value(), ex.getMessage(), LocalDateTime.now()));
    }

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String message, String ex) {
        return ResponseEntity.status(status)
                .body(new ExceptionResponse(message, status.value(), ex, LocalDateTime.now()));
    }

    private ResponseEntity<ExceptionResponse> buildResponse(String message, List<String> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(message, HttpStatus.BAD_REQUEST.value(), String.join(", ", errors), LocalDateTime.now()));
    }
}

