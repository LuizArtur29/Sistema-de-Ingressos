package com.vendaingressos.advice;

import com.vendaingressos.dto.FieldErrorItem;
import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ForbiddenException;
import com.vendaingressos.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    private static final String MDC_CORRELATION_ID = "correlationId";

    private static final String TITLE_BAD_REQUEST = "Bad Request";
    private static final String TITLE_VALIDATION_ERROR = "Validation Error";
    private static final String TITLE_NOT_FOUND = "Not Found";
    private static final String TITLE_UNAUTHORIZED = "Unauthorized";
    private static final String TITLE_FORBIDDEN = "Forbidden";
    private static final String TITLE_INTERNAL_ERROR = "Internal Server Error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail pd = buildProblemDetail(HttpStatus.BAD_REQUEST, TITLE_VALIDATION_ERROR, "Dados inválidos", request);

        List<FieldErrorItem> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldErrorItem(error.getField(), error.getDefaultMessage()))
            .toList();

        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail pd = buildProblemDetail(HttpStatus.NOT_FOUND, TITLE_NOT_FOUND, ex.getMessage(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        ProblemDetail pd = buildProblemDetail(HttpStatus.BAD_REQUEST, TITLE_BAD_REQUEST, ex.getMessage(), request);

        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ProblemDetail pd = buildProblemDetail(HttpStatus.UNAUTHORIZED, TITLE_UNAUTHORIZED, "Email ou senha incorretos.", request);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String correlationId = MDC.get(MDC_CORRELATION_ID);
        log.error("erro_interno correlationId={}", correlationId, ex);
        ProblemDetail pd =buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                TITLE_INTERNAL_ERROR,
                "Erro interno no servidor.", request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        ProblemDetail pd = buildProblemDetail(HttpStatus.FORBIDDEN, TITLE_FORBIDDEN, ex.getMessage(), request);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(pd);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail,
                                             HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("path", request.getRequestURI());
        pd.setProperty("correlationId", MDC.get("correlationId"));
        return pd;
    }

}