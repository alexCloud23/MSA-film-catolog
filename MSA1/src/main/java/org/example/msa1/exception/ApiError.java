package org.example.msa1.exception;

import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        List<String> details
) {
    public ApiError(int status, String error, String message) {
        this(status, error, message, null);
    }
}
