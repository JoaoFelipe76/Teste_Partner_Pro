package br.com.partnerpro.product_manager.framework.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {
    
    @Test
    void shouldCreateProductResponse() {
        UUID id = UUID.randomUUID();
        String name = "Notebook";
        String description = "High performance";
        BigDecimal price = new BigDecimal("2500.00");
        String category = "Electronics";
        Integer stock = 10;
        LocalDateTime createdAt = LocalDateTime.now();
        
        ProductResponse response = new ProductResponse(
                id, name, description, price, category, stock, createdAt
        );
        
        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(description, response.description());
        assertEquals(price, response.price());
        assertEquals(category, response.category());
        assertEquals(stock, response.stock());
        assertEquals(createdAt, response.createdAt());
    }
}
