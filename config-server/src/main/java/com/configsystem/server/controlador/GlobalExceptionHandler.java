package com.configsystem.server.controlador;

import com.configsystem.server.dto.ConfigurationCrudResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador global para tratamento de exceções
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata erros de validação de Bean Validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ConfigurationCrudResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        ConfigurationCrudResponse response = ConfigurationCrudResponse.error(
            "Dados inválidos: " + errorMessage
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata erros de validação de constraints
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        String errorMessage = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Violação de constraint: " + errorMessage);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata erros de argumento ilegal
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ConfigurationCrudResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ConfigurationCrudResponse response = ConfigurationCrudResponse.error(
            "Argumento inválido: " + ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata erros de runtime
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ConfigurationCrudResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception: {}", ex.getMessage(), ex);
        
        ConfigurationCrudResponse response = ConfigurationCrudResponse.error(
            "Erro interno do servidor: " + ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Trata erros genéricos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("message", "Erro inesperado: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}