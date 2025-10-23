package br.com.partnerpro.product_manager.framework.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        String sessionId,
        @NotBlank(message = "Message is required")
        String message
) {}
