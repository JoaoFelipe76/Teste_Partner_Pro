package br.com.partnerpro.product_manager.application.usecase;

import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.DashboardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardUseCaseTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private DashboardUseCase dashboardUseCase;
    
    @Test
    void shouldGetDashboardData() {
        when(productRepository.countProducts()).thenReturn(10L);
        when(productRepository.findAveragePrice()).thenReturn(new BigDecimal("1500.00"));
        
        DashboardResponse response = dashboardUseCase.getDashboardData();
        
        assertNotNull(response);
        assertEquals(10L, response.totalProducts());
        assertEquals(new BigDecimal("1500.00"), response.averagePrice());
        verify(productRepository, times(1)).countProducts();
        verify(productRepository, times(1)).findAveragePrice();
    }
    
    @Test
    void shouldHandleNullAveragePrice() {
        when(productRepository.countProducts()).thenReturn(0L);
        when(productRepository.findAveragePrice()).thenReturn(null);
        
        DashboardResponse response = dashboardUseCase.getDashboardData();
        
        assertNotNull(response);
        assertEquals(0L, response.totalProducts());
        assertEquals(BigDecimal.ZERO, response.averagePrice());
    }
    
    @Test
    void shouldReturnCorrectDataWhenProductsExist() {
        when(productRepository.countProducts()).thenReturn(5L);
        when(productRepository.findAveragePrice()).thenReturn(new BigDecimal("2500.50"));
        
        DashboardResponse response = dashboardUseCase.getDashboardData();
        
        assertEquals(5L, response.totalProducts());
        assertEquals(new BigDecimal("2500.50"), response.averagePrice());
    }
}
