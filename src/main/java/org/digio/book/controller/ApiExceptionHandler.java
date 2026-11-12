package org.digio.book.controller;

import lombok.extern.slf4j.Slf4j;
import org.digio.book.dto.ApiResponseDto;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({QueryTimeoutException.class, TransactionTimedOutException.class})
    public ResponseEntity<ApiResponseDto> handleTimeout(Exception exception) {
        return error(HttpStatus.GATEWAY_TIMEOUT, "Book retrieval timed out");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponseDto> handleDatabaseFailure(DataAccessException exception) {
        log.error("Database request failed", exception);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Database connection failed");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponseDto> handleResponseStatus(ResponseStatusException exception) {
        HttpStatusCode status = exception.getStatusCode();
        String message = exception.getReason() == null ? "Request failed" : exception.getReason();
        return error(status, message);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponseDto> handleInvalidRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "Invalid request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleUnexpectedFailure(Exception exception) {
        log.error("Unexpected request failure", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ApiResponseDto> error(HttpStatusCode status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponseDto(status.value(), null, message));
    }
}
