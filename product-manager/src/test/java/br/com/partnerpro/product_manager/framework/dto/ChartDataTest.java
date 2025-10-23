package br.com.partnerpro.product_manager.framework.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChartDataTest {
    
    @Test
    void shouldCreateChartData() {
        String type = "pie";
        String title = "Test Chart";
        List<String> labels = Arrays.asList("Label1", "Label2");
        List<Double> values = Arrays.asList(10.0, 20.0);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("color", "#FF0000");
        
        ChartData chartData = new ChartData(type, title, labels, values, metadata);
        
        assertNotNull(chartData);
        assertEquals(type, chartData.type());
        assertEquals(title, chartData.title());
        assertEquals(labels, chartData.labels());
        assertEquals(values, chartData.values());
        assertEquals(metadata, chartData.metadata());
    }
    
    @Test
    void shouldHandleEmptyMetadata() {
        Map<String, Object> emptyMetadata = new HashMap<>();
        
        ChartData chartData = new ChartData(
                "bar",
                "Test",
                Arrays.asList("A"),
                Arrays.asList(1.0),
                emptyMetadata
        );
        
        assertNotNull(chartData.metadata());
        assertTrue(chartData.metadata().isEmpty());
    }
}
