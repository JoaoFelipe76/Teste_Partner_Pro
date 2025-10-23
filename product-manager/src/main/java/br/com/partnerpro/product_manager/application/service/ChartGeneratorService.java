package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.framework.dto.ChartData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChartGeneratorService {
    
    public ChartData generateProductsByCategoryChart(List<Product> products) {
        Map<String, Long> categoryCount = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));
        
        List<String> labels = new ArrayList<>(categoryCount.keySet());
        List<Double> values = categoryCount.values().stream()
                .map(Long::doubleValue)
                .collect(Collectors.toList());
        
        return new ChartData(
                "pie",
                "Produtos por Categoria",
                labels,
                values,
                Map.of("colors", Arrays.asList("#008FFB", "#00E396", "#FEB019", "#FF4560", "#775DD0"))
        );
    }
    
    public ChartData generatePriceDistributionChart(List<Product> products) {
        long range1 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("500")) < 0).count();
        long range2 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("500")) >= 0 && 
                                                      p.getPrice().compareTo(new BigDecimal("1000")) < 0).count();
        long range3 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("1000")) >= 0 && 
                                                      p.getPrice().compareTo(new BigDecimal("2000")) < 0).count();
        long range4 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("2000")) >= 0 && 
                                                      p.getPrice().compareTo(new BigDecimal("3000")) < 0).count();
        long range5 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("3000")) >= 0).count();
        
        return new ChartData(
                "column",
                "Distribuição de Preços",
                Arrays.asList("0-500", "500-1K", "1K-2K", "2K-3K", "3K+"),
                Arrays.asList((double) range1, (double) range2, (double) range3, (double) range4, (double) range5),
                Map.of("color", "#00E396")
        );
    }
    
    public ChartData generateStockLevelsChart(List<Product> products) {
        long outOfStock = products.stream().filter(p -> p.getStock() == 0).count();
        long lowStock = products.stream().filter(p -> p.getStock() > 0 && p.getStock() < 10).count();
        long mediumStock = products.stream().filter(p -> p.getStock() >= 10 && p.getStock() < 50).count();
        long highStock = products.stream().filter(p -> p.getStock() >= 50).count();
        
        return new ChartData(
                "bar",
                "Níveis de Estoque",
                Arrays.asList("Sem Estoque", "Baixo (1-9)", "Médio (10-49)", "Alto (50+)"),
                Arrays.asList((double) outOfStock, (double) lowStock, (double) mediumStock, (double) highStock),
                Map.of("colors", Arrays.asList("#FF4560", "#FEB019", "#00E396", "#008FFB"))
        );
    }
    
    public ChartData generateValueByCategoryChart(List<Product> products) {
        Map<String, BigDecimal> categoryValues = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())),
                                BigDecimal::add
                        )
                ));
        
        List<Map.Entry<String, BigDecimal>> sortedCategories = categoryValues.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());
        
        List<String> labels = sortedCategories.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        List<Double> values = sortedCategories.stream()
                .map(e -> e.getValue().doubleValue())
                .collect(Collectors.toList());
        
        return new ChartData(
                "bar",
                "Valor Total por Categoria (R$)",
                labels,
                values,
                Map.of("colors", Arrays.asList("#00E396", "#008FFB", "#FEB019", "#FF4560", "#775DD0"))
        );
    }
    
    public ChartData generateAveragePriceByCategoryChart(List<Product> products) {
        Map<String, Double> categoryAvgPrice = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(p -> p.getPrice().doubleValue())
                ));
        
        List<Map.Entry<String, Double>> sortedCategories = categoryAvgPrice.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
        
        List<String> labels = sortedCategories.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        List<Double> values = sortedCategories.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        
        return new ChartData(
                "column",
                "Preço Médio por Categoria (R$)",
                labels,
                values,
                Map.of("colors", Arrays.asList("#775DD0", "#00E396", "#FEB019", "#FF4560", "#008FFB"))
        );
    }
    
    public ChartData detectAndGenerateChart(String userMessage, List<Product> products) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("gráfico") || lowerMessage.contains("grafico") || 
            lowerMessage.contains("visualiz") || lowerMessage.contains("chart")) {
            
            if (lowerMessage.contains("categoria") && (lowerMessage.contains("produto") || lowerMessage.contains("quantidade"))) {
                return generateProductsByCategoryChart(products);
            }
            
            if (lowerMessage.contains("preço") || lowerMessage.contains("preco") || lowerMessage.contains("valor")) {
                if (lowerMessage.contains("distribuição") || lowerMessage.contains("distribuicao") || lowerMessage.contains("faixa")) {
                    return generatePriceDistributionChart(products);
                }
                if (lowerMessage.contains("médio") || lowerMessage.contains("medio") || lowerMessage.contains("média")) {
                    return generateAveragePriceByCategoryChart(products);
                }
                if (lowerMessage.contains("categoria") || lowerMessage.contains("total")) {
                    return generateValueByCategoryChart(products);
                }
            }
            
            if (lowerMessage.contains("estoque") || lowerMessage.contains("stock")) {
                return generateStockLevelsChart(products);
            }
            
            // Default: produtos por categoria
            return generateProductsByCategoryChart(products);
        }
        
        return null;
    }
}
