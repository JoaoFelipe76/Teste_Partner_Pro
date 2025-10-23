package br.com.partnerpro.product_manager.framework.dto;

import lombok.Builder;

@Builder
public record AIReportResponse(
        String query,
        String report
) {}
