package br.com.partnerpro.product_manager.application.usecase;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.domain.specification.ProductSpecification;
import br.com.partnerpro.product_manager.framework.dto.CreateProductRequest;
import br.com.partnerpro.product_manager.framework.dto.ProductFilterRequest;
import br.com.partnerpro.product_manager.framework.dto.ProductResponse;
import br.com.partnerpro.product_manager.framework.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductUseCase {
    
    private final ProductRepository productRepository;
    
    @Cacheable("products")
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products from database");
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.debug("Fetching all products with pagination");
        return productRepository.findAll(pageable)
                .map(ProductResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public Page<ProductResponse> filterProducts(ProductFilterRequest filters, Pageable pageable) {
        log.debug("Filtering products with pagination: {}", filters);
        Specification<Product> spec = ProductSpecification.withFilters(filters);
        return productRepository.findAll(spec, pageable)
                .map(ProductResponse::fromEntity);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        log.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .map(ProductResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    @CacheEvict(value = {"products", "dashboard"}, allEntries = true)
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request.name());
        
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .stock(request.stock())
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        
        return ProductResponse.fromEntity(savedProduct);
    }
    
    @CacheEvict(value = {"products", "dashboard"}, allEntries = true)
    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        log.info("Updating product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setStock(request.stock());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully");
        
        return ProductResponse.fromEntity(updatedProduct);
    }
    
    @CacheEvict(value = {"products", "dashboard"}, allEntries = true)
    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully");
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        log.debug("Fetching products by category: {}", category);
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsSortedByPrice(String order) {
        log.debug("Fetching products sorted by price: {}", order);
        
        List<Product> products = "desc".equalsIgnoreCase(order)
                ? productRepository.findByOrderByPriceDesc()
                : productRepository.findByOrderByPriceAsc();
        
        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(ProductFilterRequest filters) {
        log.debug("Searching products with filters: {}", filters);
        
        Specification<Product> spec = ProductSpecification.withFilters(filters);
        List<Product> products = productRepository.findAll(spec);
        
        
        if (filters.sortBy() != null && !filters.sortBy().isEmpty()) {
            products = applySorting(products, filters.sortBy(), filters.sortDirection());
        }
        
        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
    
    private List<Product> applySorting(List<Product> products, String sortBy, String sortDirection) {
        boolean ascending = !"desc".equalsIgnoreCase(sortDirection);
        
        return products.stream()
                .sorted((p1, p2) -> {
                    int comparison = switch (sortBy.toLowerCase()) {
                        case "name" -> p1.getName().compareTo(p2.getName());
                        case "price" -> p1.getPrice().compareTo(p2.getPrice());
                        case "category" -> p1.getCategory().compareTo(p2.getCategory());
                        case "createdat", "date" -> p1.getCreatedAt().compareTo(p2.getCreatedAt());
                        default -> 0;
                    };
                    return ascending ? comparison : -comparison;
                })
                .toList();
    }
}
