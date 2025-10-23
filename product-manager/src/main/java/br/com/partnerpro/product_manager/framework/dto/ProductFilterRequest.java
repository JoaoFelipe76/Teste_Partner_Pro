package br.com.partnerpro.product_manager.framework.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ProductFilterRequest(
        String name,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String sortBy,
        String sortDirection
) {
    public boolean hasFilters() {
        return name != null || category != null || 
               minPrice != null || maxPrice != null ||
               startDate != null || endDate != null;
    }
}
