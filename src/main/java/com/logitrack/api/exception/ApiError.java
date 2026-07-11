package com.logitrack.api.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetails> fieldErrors
) {
    public record FieldErrorDetails(String field, String message) {}
}