package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.exception;

import br.com.alysongustavo.policymanagementservice.domain.exception.BusinessException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {

        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "Acesso negado", req, null);
    }

    @ExceptionHandler({PolicyNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(BusinessException ex,
                                                   HttpServletRequest req,
                                                   Locale locale) {

        String key = "error." + ex.getCode();
        String message = messageSource.getMessage(key, ex.getArgs(), key, locale);

        return build(HttpStatus.NOT_FOUND, ex.getCode(), message, req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, Object> details = new LinkedHashMap<>();
        Map<String, String> fields = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fields.put(err.getField(), Objects.toString(err.getDefaultMessage(), "invalid")));

        details.put("fields", fields);

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request inválido", req, details);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex,
                                                   HttpServletRequest req,
                                                   Locale locale) {

        log.warn("Violação de regra de negócio: {}", ex.getMessage()); // Log como WARN

        String key = "error." + ex.getCode();
        String message = messageSource.getMessage(key, ex.getArgs(), key, locale);

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("code", ex.getCode());
        details.put("args", ex.getArgs().length == 0 ? null : ex.getArgs());

        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getCode(), message, req, details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY", "Violação de integridade de dados", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado no sistema: ", ex); // Log como ERROR com StackTrace
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR", "Erro inesperado", req, null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message,
                                           HttpServletRequest req, Map<String, Object> details) {
        String traceId = resolveTraceId();
        ApiError body = ApiError.of(
                status.value(),
                code,
                message,
                req.getRequestURI(),
                traceId,
                details
        );
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    private String resolveTraceId() {
        try {
            return org.slf4j.MDC.get("traceId");
        } catch (Exception ignored) {
            return null;
        }
    }
}
