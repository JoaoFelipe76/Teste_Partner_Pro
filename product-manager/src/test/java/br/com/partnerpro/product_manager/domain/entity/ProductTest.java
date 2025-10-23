package br.com.partnerpro.product_manager.domain.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    
    @Test
    void shouldCreateProductWithBuilder() {
        UUID id = UUID.randomUUID();
        String name = "Notebook";
        String description = "High performance laptop";
        BigDecimal price = new BigDecimal("2500.00");
        String category = "Electronics";
        Integer stock = 10;
        LocalDateTime createdAt = LocalDateTime.now();
        
        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .stock(stock)
                .createdAt(createdAt)
                .build();
        
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
        assertEquals(stock, product.getStock());
        assertEquals(createdAt, product.getCreatedAt());
    }
    
    @Test
    void shouldHaveDefaultStockValueOfZero() {
        Product product = Product.builder()
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .category("Test")
                .build();
        
        assertEquals(0, product.getStock());
    }
    
    @Test
    void shouldAllowStockUpdate() {
        Product product = Product.builder()
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .category("Test")
                .stock(5)
                .build();
        
        product.setStock(15);
        
        assertEquals(15, product.getStock());
    }
}
