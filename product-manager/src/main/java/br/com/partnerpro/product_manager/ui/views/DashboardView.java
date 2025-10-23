package br.com.partnerpro.product_manager.ui.views;

import br.com.partnerpro.product_manager.application.service.ExportService;
import br.com.partnerpro.product_manager.application.usecase.DashboardUseCase;
import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.DashboardResponse;
import br.com.partnerpro.product_manager.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Product Manager")
@jakarta.annotation.security.PermitAll
public class DashboardView extends VerticalLayout {
    
    private final DashboardUseCase dashboardUseCase;
    private final ProductRepository productRepository;
    private final ExportService exportService;
    
    public DashboardView(DashboardUseCase dashboardUseCase, ProductRepository productRepository, ExportService exportService) {
        this.dashboardUseCase = dashboardUseCase;
        this.productRepository = productRepository;
        this.exportService = exportService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        
        H2 title = new H2("Dashboard");
        
        HorizontalLayout exportButtons = new HorizontalLayout();
        exportButtons.setSpacing(true);
        
        Button exportPdfButton = new Button("Exportar PDF", VaadinIcon.FILE_TEXT_O.create());
        exportPdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportPdfButton.addClickListener(e -> exportToPdf());
        
        Button exportCsvButton = new Button("Exportar CSV", VaadinIcon.FILE_TABLE.create());
        exportCsvButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportCsvButton.addClickListener(e -> exportToCsv());
        
        exportButtons.add(exportCsvButton, exportPdfButton);
        header.add(title, exportButtons);
        
        add(header);
        
        createStatisticsCards();
        createCharts();
    }
    
    private void exportToPdf() {
        try {
            byte[] pdfData = exportService.exportDashboardToPDF();
            
            StreamResource resource = new StreamResource("dashboard.pdf", 
                () -> new java.io.ByteArrayInputStream(pdfData));
            resource.setContentType("application/pdf");
            
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setId("dashboard-pdf-download-link");
            downloadLink.getStyle().set("display", "none");
            add(downloadLink);
            
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("setTimeout(function() { document.getElementById('dashboard-pdf-download-link').click(); }, 100);");
            });
            
            Notification notification = Notification.show("Exportando dashboard para PDF...", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao exportar PDF: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void exportToCsv() {
        try {
            String csvData = exportService.exportProductsToCSV();
            
            StreamResource resource = new StreamResource("produtos.csv", 
                () -> new java.io.ByteArrayInputStream(csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            resource.setContentType("text/csv");
            
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setId("dashboard-csv-download-link");
            downloadLink.getStyle().set("display", "none");
            add(downloadLink);
            
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("setTimeout(function() { document.getElementById('dashboard-csv-download-link').click(); }, 100);");
            });
            
            Notification notification = Notification.show("Exportando produtos para CSV...", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
        } catch (Exception e) {
            Notification notification = Notification.show("Erro ao exportar CSV: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void createStatisticsCards() {
        DashboardResponse data = dashboardUseCase.getDashboardData();
        List<Product> products = productRepository.findAll();
        
        long lowStock = products.stream().filter(p -> p.getStock() < 10).count();
        long outOfStock = products.stream().filter(p -> p.getStock() == 0).count();
        
        BigDecimal totalValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        HorizontalLayout cardsLayout = new HorizontalLayout();
        cardsLayout.setWidthFull();
        cardsLayout.setSpacing(true);
        
        cardsLayout.add(
                createCard("Total de Produtos", String.valueOf(data.totalProducts()), "success", "📦"),
                createCard("Preço Médio", String.format("R$ %.2f", data.averagePrice()), "primary", "💰"),
                createCard("Valor em Estoque", String.format("R$ %.2f", totalValue), "contrast", "💵"),
                createCard("Estoque Baixo", String.valueOf(lowStock), "error", "⚠️")
        );
        
        add(cardsLayout);
    }
    
    private Div createCard(String title, String value, String theme, String icon) {
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        card.setWidth("25%");
        
        Span iconSpan = new Span(icon);
        iconSpan.getStyle()
                .set("font-size", "2rem")
                .set("margin-bottom", "0.5rem");
        
        Span titleSpan = new Span(title);
        titleSpan.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY
        );
        
        Span valueSpan = new Span(value);
        valueSpan.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD
        );
        
        VerticalLayout content = new VerticalLayout(iconSpan, titleSpan, valueSpan);
        content.setSpacing(false);
        content.setPadding(false);
        content.setAlignItems(Alignment.CENTER);
        
        card.add(content);
        return card;
    }
    
    private void createCharts() {
        List<Product> products = productRepository.findAll();
        
        HorizontalLayout firstRow = new HorizontalLayout();
        firstRow.setWidthFull();
        firstRow.setSpacing(true);
        
        firstRow.add(
                createCategoryChart(products),
                createPriceDistributionChart(products)
        );
        
        HorizontalLayout secondRow = new HorizontalLayout();
        secondRow.setWidthFull();
        secondRow.setSpacing(true);
        
        secondRow.add(
                createStockChart(products),
                createValueByCategoryChart(products)
        );
        
        HorizontalLayout thirdRow = new HorizontalLayout();
        thirdRow.setWidthFull();
        thirdRow.setSpacing(true);
        
        thirdRow.add(
                createRecentProductsChart(products),
                createAveragePriceByCategoryChart(products)
        );
        
        add(firstRow, secondRow, thirdRow);
    }
    
    private Div createCategoryChart(List<Product> products) {
        Div container = new Div();
        container.setWidth("50%");
        container.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        
        H3 chartTitle = new H3("📊  Produtos por Categoria");
        chartTitle.getStyle().set("margin-top", "0");
        
        Map<String, Long> categoryCount = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, Collectors.counting()));
        
        VerticalLayout barsContainer = new VerticalLayout();
        barsContainer.setSpacing(true);
        barsContainer.setPadding(false);
        barsContainer.setWidthFull();
        
        long maxCount = categoryCount.values().stream().max(Long::compare).orElse(1L);
        
        String[] colors = {"#008FFB", "#00E396", "#FEB019", "#FF4560", "#775DD0", "#546E7A", "#26a69a"};
        int colorIndex = 0;
        
        for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
            String category = entry.getKey();
            Long count = entry.getValue();
            double percentage = (count * 100.0) / maxCount;
            
            HorizontalLayout barRow = new HorizontalLayout();
            barRow.setWidthFull();
            barRow.setAlignItems(Alignment.CENTER);
            barRow.setSpacing(true);
            
            Span label = new Span(category);
            label.setWidth("120px");
            label.getStyle().set("font-weight", "500");
            
            Div barContainer = new Div();
            barContainer.setWidthFull();
            barContainer.getStyle()
                    .set("background-color", "var(--lumo-contrast-10pct)")
                    .set("border-radius", "4px")
                    .set("height", "30px")
                    .set("position", "relative");
            
            Div bar = new Div();
            bar.getStyle()
                    .set("background-color", colors[colorIndex % colors.length])
                    .set("height", "100%")
                    .set("width", percentage + "%")
                    .set("border-radius", "4px")
                    .set("transition", "width 0.3s ease");
            
            Span countLabel = new Span(count.toString());
            countLabel.getStyle()
                    .set("position", "absolute")
                    .set("right", "10px")
                    .set("top", "50%")
                    .set("transform", "translateY(-50%)")
                    .set("font-weight", "bold")
                    .set("color", "white");
            
            bar.add(countLabel);
            barContainer.add(bar);
            
            barRow.add(label, barContainer);
            barsContainer.add(barRow);
            
            colorIndex++;
        }
        
        container.add(chartTitle, barsContainer);
        return container;
    }
    
    private Div createPriceDistributionChart(List<Product> products) {
        Div container = new Div();
        container.setWidth("50%");
        container.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        
        H3 chartTitle = new H3("💰  Distribuição de Preços");
        chartTitle.getStyle().set("margin-top", "0");
        
        long range1 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("500")) < 0).count();
        long range2 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("500")) >= 0 && 
                                                      p.getPrice().compareTo(new BigDecimal("1000")) < 0).count();
        long range3 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("1000")) >= 0 && 
                                                      p.getPrice().compareTo(new BigDecimal("2000")) < 0).count();
        long range4 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("2000")) >= 0 && 
                                                      p.getPrice().compareTo(new BigDecimal("3000")) < 0).count();
        long range5 = products.stream().filter(p -> p.getPrice().compareTo(new BigDecimal("3000")) >= 0).count();
        
        long maxValue = Math.max(Math.max(Math.max(Math.max(range1, range2), range3), range4), range5);
        if (maxValue == 0) maxValue = 1;
        
        HorizontalLayout barsContainer = new HorizontalLayout();
        barsContainer.setWidthFull();
        barsContainer.setAlignItems(Alignment.END);
        barsContainer.setJustifyContentMode(JustifyContentMode.AROUND);
        barsContainer.getStyle().set("height", "250px");
        
        barsContainer.add(
                createColumnBar("0-500", range1, maxValue, "#00E396"),
                createColumnBar("500-1K", range2, maxValue, "#008FFB"),
                createColumnBar("1K-2K", range3, maxValue, "#FEB019"),
                createColumnBar("2K-3K", range4, maxValue, "#FF4560"),
                createColumnBar("3K+", range5, maxValue, "#775DD0")
        );
        
        container.add(chartTitle, barsContainer);
        return container;
    }
    
    private VerticalLayout createColumnBar(String label, long value, long maxValue, String color) {
        VerticalLayout column = new VerticalLayout();
        column.setAlignItems(Alignment.CENTER);
        column.setSpacing(false);
        column.setPadding(false);
        column.setWidth("80px");
        
        double heightPercentage = maxValue > 0 ? (value * 100.0) / maxValue : 0;
        
        Span valueLabel = new Span(String.valueOf(value));
        valueLabel.getStyle()
                .set("font-weight", "bold")
                .set("margin-bottom", "5px");
        
        Div bar = new Div();
        bar.getStyle()
                .set("background-color", color)
                .set("width", "60px")
                .set("height", heightPercentage + "%")
                .set("border-radius", "4px 4px 0 0")
                .set("transition", "height 0.3s ease")
                .set("min-height", value > 0 ? "20px" : "0");
        
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "0.875rem")
                .set("margin-top", "5px");
        
        column.add(valueLabel, bar, labelSpan);
        return column;
    }
    
    private Div createStockChart(List<Product> products) {
        Div container = new Div();
        container.setWidth("50%");
        container.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        
        H3 chartTitle = new H3("📦  Níveis de Estoque");
        chartTitle.getStyle().set("margin-top", "0");
        
        long outOfStock = products.stream().filter(p -> p.getStock() == 0).count();
        long lowStock = products.stream().filter(p -> p.getStock() > 0 && p.getStock() < 10).count();
        long mediumStock = products.stream().filter(p -> p.getStock() >= 10 && p.getStock() < 50).count();
        long highStock = products.stream().filter(p -> p.getStock() >= 50).count();
        
        VerticalLayout statsContainer = new VerticalLayout();
        statsContainer.setSpacing(true);
        statsContainer.setPadding(false);
        statsContainer.setWidthFull();
        
        statsContainer.add(
                createStockRow("Sem Estoque", outOfStock, "#FF4560", "🔴"),
                createStockRow("Estoque Baixo (1-9)", lowStock, "#FEB019", "🟡"),
                createStockRow("Estoque Médio (10-49)", mediumStock, "#00E396", "🟢"),
                createStockRow("Estoque Alto (50+)", highStock, "#008FFB", "🔵")
        );
        
        container.add(chartTitle, statsContainer);
        return container;
    }
    
    private HorizontalLayout createStockRow(String label, long count, String color, String icon) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setSpacing(true);
        
        Span iconSpan = new Span(icon);
        iconSpan.getStyle().set("font-size", "1.5rem");
        
        Span labelSpan = new Span(label);
        labelSpan.setWidth("180px");
        labelSpan.getStyle().set("font-weight", "500");
        
        Div badge = new Div();
        badge.setText(String.valueOf(count));
        badge.getStyle()
                .set("background-color", color)
                .set("color", "white")
                .set("padding", "5px 15px")
                .set("border-radius", "20px")
                .set("font-weight", "bold")
                .set("min-width", "50px")
                .set("text-align", "center");
        
        row.add(iconSpan, labelSpan, badge);
        return row;
    }
    
    private Div createValueByCategoryChart(List<Product> products) {
        Div container = new Div();
        container.setWidth("50%");
        container.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        
        H3 chartTitle = new H3("💵  Valor Total por Categoria");
        chartTitle.getStyle().set("margin-top", "0");
        
        Map<String, BigDecimal> categoryValues = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())),
                                BigDecimal::add
                        )
                ));
        
        if (categoryValues.isEmpty()) {
            Span emptyMessage = new Span("Nenhum produto cadastrado");
            emptyMessage.getStyle().set("color", "var(--lumo-secondary-text-color)");
            container.add(chartTitle, emptyMessage);
            return container;
        }
        
        List<Map.Entry<String, BigDecimal>> sortedCategories = categoryValues.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());
        
        BigDecimal maxValue = sortedCategories.isEmpty() ? BigDecimal.ONE : 
                sortedCategories.get(0).getValue();
        if (maxValue.compareTo(BigDecimal.ZERO) == 0) {
            maxValue = BigDecimal.ONE;
        }
        
        VerticalLayout barsContainer = new VerticalLayout();
        barsContainer.setSpacing(true);
        barsContainer.setPadding(false);
        barsContainer.setWidthFull();
        
        String[] colors = {"#00E396", "#008FFB", "#FEB019", "#FF4560", "#775DD0", "#546E7A", "#26a69a"};
        int colorIndex = 0;
        
        for (Map.Entry<String, BigDecimal> entry : sortedCategories) {
            String category = entry.getKey();
            BigDecimal value = entry.getValue();
            double percentage = value.multiply(new BigDecimal("100"))
                    .divide(maxValue, 2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            
            HorizontalLayout barRow = new HorizontalLayout();
            barRow.setWidthFull();
            barRow.setAlignItems(Alignment.CENTER);
            barRow.setSpacing(true);
            
            Span label = new Span(category);
            label.setWidth("120px");
            label.getStyle().set("font-weight", "500");
            
            Div barContainer = new Div();
            barContainer.setWidthFull();
            barContainer.getStyle()
                    .set("background-color", "var(--lumo-contrast-10pct)")
                    .set("border-radius", "4px")
                    .set("height", "35px")
                    .set("position", "relative");
            
            Div bar = new Div();
            bar.getStyle()
                    .set("background-color", colors[colorIndex % colors.length])
                    .set("height", "100%")
                    .set("width", percentage + "%")
                    .set("border-radius", "4px")
                    .set("transition", "width 0.3s ease")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "flex-end")
                    .set("padding-right", "10px");
            
            Span valueLabel = new Span(String.format("R$ %.2f", value));
            valueLabel.getStyle()
                    .set("font-weight", "bold")
                    .set("color", "white")
                    .set("font-size", "0.9rem");
            
            bar.add(valueLabel);
            barContainer.add(bar);
            
            barRow.add(label, barContainer);
            barsContainer.add(barRow);
            
            colorIndex++;
        }
        
        container.add(chartTitle, barsContainer);
        return container;
    }
    
    private Div createRecentProductsChart(List<Product> products) {
        Div container = new Div();
        container.setWidth("50%");
        container.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        
        H3 chartTitle = new H3("🆕  Produtos Recentes");
        chartTitle.getStyle().set("margin-top", "0");
        
        List<Product> recentProducts = products.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());
        
        if (recentProducts.isEmpty()) {
            Span emptyMessage = new Span("Nenhum produto cadastrado");
            emptyMessage.getStyle().set("color", "var(--lumo-secondary-text-color)");
            container.add(chartTitle, emptyMessage);
            return container;
        }
        
        VerticalLayout productsContainer = new VerticalLayout();
        productsContainer.setSpacing(true);
        productsContainer.setPadding(false);
        productsContainer.setWidthFull();
        
        for (Product product : recentProducts) {
            HorizontalLayout productRow = new HorizontalLayout();
            productRow.setWidthFull();
            productRow.setAlignItems(Alignment.CENTER);
            productRow.setSpacing(true);
            productRow.getStyle()
                    .set("padding", "10px")
                    .set("border-radius", "8px")
                    .set("background-color", "var(--lumo-contrast-5pct)");
            
            Span iconSpan = new Span("📦");
            iconSpan.getStyle().set("font-size", "1.5rem");
            
            VerticalLayout infoLayout = new VerticalLayout();
            infoLayout.setSpacing(false);
            infoLayout.setPadding(false);
            infoLayout.setWidthFull();
            
            Span nameSpan = new Span(product.getName().length() > 35 ? 
                    product.getName().substring(0, 32) + "..." : product.getName());
            nameSpan.getStyle().set("font-weight", "500");
            
            Span detailsSpan = new Span(String.format("%s • R$ %.2f • Estoque: %d",
                    product.getCategory(),
                    product.getPrice(),
                    product.getStock()));
            detailsSpan.getStyle()
                    .set("font-size", "0.875rem")
                    .set("color", "var(--lumo-secondary-text-color)");
            
            infoLayout.add(nameSpan, detailsSpan);
            
            Span dateSpan = new Span(product.getCreatedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dateSpan.getStyle()
                    .set("font-size", "0.875rem")
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("white-space", "nowrap");
            
            productRow.add(iconSpan, infoLayout, dateSpan);
            productsContainer.add(productRow);
        }
        
        container.add(chartTitle, productsContainer);
        return container;
    }
    
    private Div createAveragePriceByCategoryChart(List<Product> products) {
        Div container = new Div();
        container.setWidth("50%");
        container.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        
        H3 chartTitle = new H3("📊  Preço Médio por Categoria");
        chartTitle.getStyle().set("margin-top", "0");
        
        Map<String, Double> categoryAvgPrice = products.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(p -> p.getPrice().doubleValue())
                ));
        
        if (categoryAvgPrice.isEmpty()) {
            Span emptyMessage = new Span("Nenhum produto cadastrado");
            emptyMessage.getStyle().set("color", "var(--lumo-secondary-text-color)");
            container.add(chartTitle, emptyMessage);
            return container;
        }
        
        List<Map.Entry<String, Double>> sortedCategories = categoryAvgPrice.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toList());
        
        double maxAvg = sortedCategories.isEmpty() ? 1.0 : sortedCategories.get(0).getValue();
        if (maxAvg == 0) maxAvg = 1.0;
        
        HorizontalLayout barsContainer = new HorizontalLayout();
        barsContainer.setWidthFull();
        barsContainer.setAlignItems(Alignment.END);
        barsContainer.setJustifyContentMode(JustifyContentMode.AROUND);
        barsContainer.getStyle().set("height", "250px");
        
        String[] colors = {"#775DD0", "#00E396", "#FEB019", "#FF4560", "#008FFB", "#546E7A"};
        
        for (int i = 0; i < sortedCategories.size() && i < 6; i++) {
            Map.Entry<String, Double> entry = sortedCategories.get(i);
            barsContainer.add(createAveragePriceColumn(
                    entry.getKey(),
                    entry.getValue(),
                    maxAvg,
                    colors[i % colors.length]
            ));
        }
        
        container.add(chartTitle, barsContainer);
        return container;
    }
    
    private VerticalLayout createAveragePriceColumn(String category, double avgPrice, double maxAvg, String color) {
        VerticalLayout column = new VerticalLayout();
        column.setAlignItems(Alignment.CENTER);
        column.setSpacing(false);
        column.setPadding(false);
        column.setWidth("100px");
        
        double heightPercentage = (avgPrice * 100.0) / maxAvg;
        
        Span valueLabel = new Span(String.format("R$ %.0f", avgPrice));
        valueLabel.getStyle()
                .set("font-weight", "bold")
                .set("margin-bottom", "5px")
                .set("font-size", "0.9rem");
        
        Div bar = new Div();
        bar.getStyle()
                .set("background-color", color)
                .set("width", "70px")
                .set("height", heightPercentage + "%")
                .set("border-radius", "4px 4px 0 0")
                .set("transition", "height 0.3s ease")
                .set("min-height", "20px");
        
        Span labelSpan = new Span(category.length() > 10 ? category.substring(0, 8) + "..." : category);
        labelSpan.getStyle()
                .set("font-size", "0.875rem")
                .set("margin-top", "5px")
                .set("text-align", "center");
        
        column.add(valueLabel, bar, labelSpan);
        return column;
    }
}
