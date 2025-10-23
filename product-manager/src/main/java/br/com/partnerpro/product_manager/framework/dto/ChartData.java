package br.com.partnerpro.product_manager.framework.dto;

import java.util.List;
import java.util.Map;

public record ChartData(
        String type,  
        String title,
        List<String> labels,
        List<Double> values,
        Map<String, Object> metadata
) {
}
