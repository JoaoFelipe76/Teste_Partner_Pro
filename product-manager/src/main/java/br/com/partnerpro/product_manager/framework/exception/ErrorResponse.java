package br.com.partnerpro.product_manager.framework.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {}
