package br.com.partnerpro.product_manager.framework.dto;

public record AIResponse(
        String message,
        ChartData chartData,
        boolean hasChart
) {
    public AIResponse(String message) {
        this(message, null, false);
    }
    
    public AIResponse(String message, ChartData chartData) {
        this(message, chartData, chartData != null);
    }
}
