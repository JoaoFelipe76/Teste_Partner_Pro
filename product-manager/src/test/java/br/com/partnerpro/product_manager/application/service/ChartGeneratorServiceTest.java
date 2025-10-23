package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.framework.dto.ChartData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChartGeneratorServiceTest {
    
    private ChartGeneratorService chartGeneratorService;
    private List<Product> products;
    
    @BeforeEach
    void setUp() {
        chartGeneratorService = new ChartGeneratorService();
        
        products = Arrays.asList(
                Product.builder()
                        .name("Notebook")
                        .category("Electronics")
                        .price(new BigDecimal("2500.00"))
                        .stock(10)
                        .build(),
                Product.builder()
                        .name("Mouse")
                        .category("Electronics")
                        .price(new BigDecimal("50.00"))
                        .stock(50)
                        .build(),
                Product.builder()
                        .name("Desk")
                        .category("Furniture")
                        .price(new BigDecimal("800.00"))
                        .stock(5)
                        .build()
        );
    }
    
    @Test
    void shouldGenerateProductsByCategoryChart() {
        ChartData chart = chartGeneratorService.generateProductsByCategoryChart(products);
        
        assertNotNull(chart);
        assertEquals("pie", chart.type());
        assertEquals("Produtos por Categoria", chart.title());
        assertEquals(2, chart.labels().size());
        assertTrue(chart.labels().contains("Electronics"));
        assertTrue(chart.labels().contains("Furniture"));
    }
    
    @Test
    void shouldGeneratePriceDistributionChart() {
        ChartData chart = chartGeneratorService.generatePriceDistributionChart(products);
        
        assertNotNull(chart);
        assertEquals("column", chart.type());
        assertEquals("Distribuição de Preços", chart.title());
        assertEquals(5, chart.labels().size());
        assertEquals(5, chart.values().size());
    }
    
    @Test
    void shouldGenerateStockLevelsChart() {
        ChartData chart = chartGeneratorService.generateStockLevelsChart(products);
        
        assertNotNull(chart);
        assertEquals("bar", chart.type());
        assertEquals("Níveis de Estoque", chart.title());
        assertEquals(4, chart.labels().size());
    }
    
    @Test
    void shouldGenerateValueByCategoryChart() {
        ChartData chart = chartGeneratorService.generateValueByCategoryChart(products);
        
        assertNotNull(chart);
        assertEquals("bar", chart.type());
        assertEquals("Valor Total por Categoria (R$)", chart.title());
        assertFalse(chart.labels().isEmpty());
        assertFalse(chart.values().isEmpty());
    }
    
    @Test
    void shouldDetectCategoryChartRequest() {
        ChartData chart = chartGeneratorService.detectAndGenerateChart(
                "Mostre um gráfico de produtos por categoria", products);
        
        assertNotNull(chart);
        assertEquals("pie", chart.type());
    }
    
    @Test
    void shouldDetectPriceDistributionRequest() {
        ChartData chart = chartGeneratorService.detectAndGenerateChart(
                "Gráfico de distribuição de preços", products);
        
        assertNotNull(chart);
        assertEquals("column", chart.type());
    }
    
    @Test
    void shouldReturnNullWhenNoChartDetected() {
        ChartData chart = chartGeneratorService.detectAndGenerateChart(
                "Quantos produtos temos?", products);
        
        assertNull(chart);
    }
}
