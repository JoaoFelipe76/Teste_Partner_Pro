package br.com.partnerpro.product_manager.framework.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DashboardResponse(
        Long totalProducts,
        BigDecimal averagePrice
) {}
