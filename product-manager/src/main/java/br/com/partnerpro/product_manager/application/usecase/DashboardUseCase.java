package br.com.partnerpro.product_manager.application.usecase;

import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardUseCase {
    
    private final ProductRepository productRepository;
    
    @Cacheable("dashboard")
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        log.debug("Fetching dashboard data from database");
        
        Long totalProducts = productRepository.countProducts();
        BigDecimal averagePrice = productRepository.findAveragePrice();
        
        if (averagePrice == null) {
            averagePrice = BigDecimal.ZERO;
        }
        
        return DashboardResponse.builder()
                .totalProducts(totalProducts)
                .averagePrice(averagePrice)
                .build();
    }
}
