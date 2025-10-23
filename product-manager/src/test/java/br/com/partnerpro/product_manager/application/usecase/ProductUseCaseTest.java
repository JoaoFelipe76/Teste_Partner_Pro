package br.com.partnerpro.product_manager.application.usecase;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.CreateProductRequest;
import br.com.partnerpro.product_manager.framework.dto.ProductResponse;
import br.com.partnerpro.product_manager.framework.dto.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductUseCase productUseCase;
    
    private Product product;
    private UUID productId;
    
    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = Product.builder()
                .id(productId)
                .name("Notebook")
                .description("High performance laptop")
                .price(new BigDecimal("2500.00"))
                .category("Electronics")
                .stock(10)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void shouldCreateProduct() {
        CreateProductRequest request = new CreateProductRequest(
                "Notebook",
                "High performance laptop",
                new BigDecimal("2500.00"),
                "Electronics",
                10
        );
        
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductResponse response = productUseCase.createProduct(request);
        
        assertNotNull(response);
        assertEquals("Notebook", response.name());
        assertEquals("Electronics", response.category());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void shouldGetAllProducts() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);
        
        List<ProductResponse> responses = productUseCase.getAllProducts();
        
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    void shouldGetProductById() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        ProductResponse response = productUseCase.getProductById(productId);
        
        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals("Notebook", response.name());
        verify(productRepository, times(1)).findById(productId);
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> productUseCase.getProductById(nonExistentId));
    }
    
    @Test
    void shouldUpdateProduct() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Notebook",
                "Updated description",
                new BigDecimal("2800.00"),
                "Electronics",
                15
        );
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductResponse response = productUseCase.updateProduct(productId, request);
        
        assertNotNull(response);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void shouldDeleteProduct() {
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);
        
        productUseCase.deleteProduct(productId);
        
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).deleteById(productId);
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.existsById(nonExistentId)).thenReturn(false);
        
        assertThrows(RuntimeException.class, () -> productUseCase.deleteProduct(nonExistentId));
        verify(productRepository, never()).deleteById(any());
    }
}
