package br.com.partnerpro.product_manager.framework.dto;

import lombok.Builder;

@Builder
public record ChatResponse(
        String sessionId,
        String message,
        String response
) {}
