package br.com.partnerpro.product_manager.framework.dto;

import jakarta.validation.constraints.NotBlank;

public record AIReportRequest(
        @NotBlank(message = "Query is required")
        String query
) {}
