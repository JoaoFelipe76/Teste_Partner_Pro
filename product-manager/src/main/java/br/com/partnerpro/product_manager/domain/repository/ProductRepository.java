package br.com.partnerpro.product_manager.domain.repository;

import br.com.partnerpro.product_manager.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByOrderByPriceAsc();
    
    List<Product> findByOrderByPriceDesc();
    
    @Query("SELECT AVG(p.price) FROM Product p")
    BigDecimal findAveragePrice();
    
    @Query("SELECT COUNT(p) FROM Product p")
    Long countProducts();
}
