package br.com.partnerpro.product_manager.domain.specification;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.framework.dto.ProductFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    
    public static Specification<Product> withFilters(ProductFilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            
            if (filters.name() != null && !filters.name().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filters.name().toLowerCase() + "%"
                ));
            }
            
            
            if (filters.category() != null && !filters.category().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category")),
                    filters.category().toLowerCase()
                ));
            }
            
            
            if (filters.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    filters.minPrice()
                ));
            }
            
            
            if (filters.maxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    filters.maxPrice()
                ));
            }
            
            
            if (filters.startDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    filters.startDate()
                ));
            }
            
            
            if (filters.endDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    filters.endDate()
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
