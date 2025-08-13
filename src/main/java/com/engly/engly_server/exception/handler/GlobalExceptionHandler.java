package com.engly.engly_server.exception.handler;

import com.engly.engly_server.exception.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler({Exception.class, RepositoryException.class})
    public ResponseEntity<ExceptionResponse> handleGeneralExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler({
            TypeMismatchException.class,
            PasswordGeneratorException.class,
            PropertyReferenceException.class,
            FieldValidationException.class,
            MaxUploadSizeExceededException.class,
            SpecificationException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler({
            WebSocketException.class,
            SignInException.class,
            TokenNotFoundException.class,
            InvalidTokenTypeException.class,
            AuthenticationObjectException.class
    })
    public ResponseEntity<ExceptionResponse> handleUnauthorizedExceptions(Exception ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenExceptions(Exception ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({
            EntityAlreadyExistsException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(Exception ex) {
        return buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler({
            NotFoundException.class,
            GenerateTokenException.class,
            GoogleDriveException.class
    })
    public ResponseEntity<ExceptionResponse> handleInternalServerErrorExceptions(Exception ex) {
        HttpStatus status = ex instanceof NotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(status, ex);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {
        return buildResponse((HttpStatus) ex.getStatusCode(), ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return buildValidationResponse("Validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return buildValidationResponse("Constraint violation", errors);
    }

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, Exception ex) {
        String message = getMessageForException(ex);
        return ResponseEntity.status(status)
                .body(new ExceptionResponse(message, status.value(), ex.getMessage(), LocalDateTime.now()));
    }

    private ResponseEntity<ExceptionResponse> buildValidationResponse(String message, List<String> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(message, HttpStatus.BAD_REQUEST.value(), String.join(", ", errors), LocalDateTime.now()));
    }

    private String getMessageForException(Exception ex) {
        return switch (ex) {
            case TypeMismatchException e -> "Invalid data type for field: " + e.getPropertyName();
            case EntityAlreadyExistsException _ -> "Entity already exists";
            case AccessDeniedException _ -> "Access denied";
            case WebSocketException _ -> "WebSocket connection error";
            case SignInException _ -> "Invalid credentials";
            case GoogleDriveException _ -> "Google Drive Api";
            case PasswordGeneratorException _ -> "Password generation failed";
            case PropertyReferenceException _ -> "Invalid property reference";
            case NotFoundException _ -> "Resource not found";
            case GenerateTokenException _ -> "Token generation failed";
            case DataIntegrityViolationException _ -> "Data integrity violation";
            case TokenNotFoundException _ -> "Invalid or expired token";
            case FieldValidationException _ -> "Field validation error";
            case MaxUploadSizeExceededException _ -> "File size exceeds maximum allowed size";
            case ResponseStatusException e -> e.getReason() != null ? e.getReason() : "Request processing error";
            default -> "An unexpected error occurred";
        };
    }
}

