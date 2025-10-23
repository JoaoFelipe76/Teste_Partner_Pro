package br.com.partnerpro.product_manager.framework.dto;

import br.com.partnerpro.product_manager.domain.entity.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stock,
        LocalDateTime createdAt
) {
    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
