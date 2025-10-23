package br.com.partnerpro.product_manager.ui.views;

import br.com.partnerpro.product_manager.application.service.ExportService;
import br.com.partnerpro.product_manager.application.usecase.ProductUseCase;
import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.CreateProductRequest;
import br.com.partnerpro.product_manager.framework.dto.UpdateProductRequest;
import br.com.partnerpro.product_manager.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Produtos | Product Manager")
@jakarta.annotation.security.PermitAll
public class ProductsView extends VerticalLayout {
    
    private final ProductRepository productRepository;
    private final ProductUseCase productUseCase;
    private final ExportService exportService;
    private final Grid<Product> grid = new Grid<>(Product.class, false);
    
    private TextField nameFilter;
    private TextField categoryFilter;
    private BigDecimalField minPriceFilter;
    private BigDecimalField maxPriceFilter;
    private IntegerField minStockFilter;
    private IntegerField maxStockFilter;
    private DatePicker startDateFilter;
    private DatePicker endDateFilter;
    
    private List<Product> allProducts;
    
    public ProductsView(ProductRepository productRepository, ProductUseCase productUseCase, ExportService exportService) {
        this.productRepository = productRepository;
        this.productUseCase = productUseCase;
        this.exportService = exportService;
        
        setSizeFull();
        setPadding(true);
        
        configureGrid();
        
        HorizontalLayout toolbar = createToolbar();
        Details filterPanel = createFilterPanel();
        
        add(toolbar, filterPanel, grid);
        
        updateList();
    }
    
    private HorizontalLayout createToolbar() {
        H2 title = new H2("Produtos");
        
        Button addButton = new Button("Novo Produto", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openProductDialog(null));
        
        Button refreshButton = new Button("Atualizar", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> updateList());
        
        Button exportCsvButton = new Button("Exportar CSV", VaadinIcon.DOWNLOAD.create());
        exportCsvButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportCsvButton.addClickListener(e -> exportToCsv());
        
        Button exportPdfButton = new Button("Exportar PDF", VaadinIcon.FILE_TEXT.create());
        exportPdfButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        exportPdfButton.addClickListener(e -> exportToPdf());
        
        HorizontalLayout actions = new HorizontalLayout(addButton, refreshButton, exportCsvButton, exportPdfButton);
        actions.setSpacing(true);
        
        HorizontalLayout toolbar = new HorizontalLayout(title, actions);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.CENTER);
        
        return toolbar;
    }
    
    private void exportToCsv() {
        try {
            String csvData = exportService.exportProductsToCSV();
            
            StreamResource resource = new StreamResource("produtos.csv", 
                () -> new java.io.ByteArrayInputStream(csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            resource.setContentType("text/csv");
            
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setId("csv-download-link");
            downloadLink.getStyle().set("display", "none");
            add(downloadLink);
            
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("setTimeout(function() { document.getElementById('csv-download-link').click(); }, 100);");
            });
            
            showNotification("Exportando produtos para CSV...", NotificationVariant.LUMO_SUCCESS);
            
        } catch (Exception e) {
            showNotification("Erro ao exportar CSV: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void exportToPdf() {
        try {
            byte[] pdfData = exportService.exportProductsToPDF();
            
            StreamResource resource = new StreamResource("produtos.pdf", 
                () -> new java.io.ByteArrayInputStream(pdfData));
            resource.setContentType("application/pdf");
            
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.setId("pdf-download-link");
            downloadLink.getStyle().set("display", "none");
            add(downloadLink);
            
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("setTimeout(function() { document.getElementById('pdf-download-link').click(); }, 100);");
            });
            
            showNotification("Exportando produtos para PDF...", NotificationVariant.LUMO_SUCCESS);
            
        } catch (Exception e) {
            showNotification("Erro ao exportar PDF: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void configureGrid() {
        grid.addClassName("product-grid");
        grid.setSizeFull();
        
        grid.addColumn(Product::getName).setHeader("Nome").setSortable(true);
        grid.addColumn(Product::getDescription).setHeader("Descrição");
        grid.addColumn(p -> String.format("R$ %.2f", p.getPrice())).setHeader("Preço").setSortable(true);
        grid.addColumn(Product::getCategory).setHeader("Categoria").setSortable(true);
        grid.addColumn(Product::getStock).setHeader("Estoque").setSortable(true);
        grid.addColumn(p -> p.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setHeader("Data de Criação").setSortable(true);
        
        grid.addComponentColumn(product -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openProductDialog(product));
            
            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> deleteProduct(product));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Ações").setWidth("150px").setFlexGrow(0);
    }
    
    private void openProductDialog(Product product) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(product == null ? "Novo Produto" : "Editar Produto");
        
        FormLayout formLayout = new FormLayout();
        
        TextField nameField = new TextField("Nome");
        nameField.setRequired(true);
        nameField.setWidthFull();
        
        TextField descriptionField = new TextField("Descrição");
        descriptionField.setWidthFull();
        
        BigDecimalField priceField = new BigDecimalField("Preço");
        priceField.setRequired(true);
        priceField.setPrefixComponent(new Span("R$"));
        
        TextField categoryField = new TextField("Categoria");
        categoryField.setRequired(true);
        
        IntegerField stockField = new IntegerField("Estoque");
        stockField.setRequired(true);
        stockField.setValue(0);
        stockField.setMin(0);
        
        if (product != null) {
            nameField.setValue(product.getName());
            descriptionField.setValue(product.getDescription() != null ? product.getDescription() : "");
            priceField.setValue(product.getPrice());
            categoryField.setValue(product.getCategory());
            stockField.setValue(product.getStock());
        }
        
        formLayout.add(nameField, descriptionField, priceField, categoryField, stockField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        
        Button saveButton = new Button("Salvar", e -> {
            // Validação dos campos obrigatórios
            if (nameField.isEmpty()) {
                showNotification("Campo Nome é obrigatório", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (priceField.isEmpty()) {
                showNotification("Campo Preço é obrigatório", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (categoryField.isEmpty()) {
                showNotification("Campo Categoria é obrigatório", NotificationVariant.LUMO_ERROR);
                return;
            }
            if (stockField.isEmpty()) {
                showNotification("Campo Estoque é obrigatório", NotificationVariant.LUMO_ERROR);
                return;
            }
            
            if (product == null) {
                createProduct(nameField.getValue(), descriptionField.getValue(), 
                        priceField.getValue(), categoryField.getValue(), stockField.getValue());
            } else {
                updateProduct(product.getId(), nameField.getValue(), descriptionField.getValue(),
                        priceField.getValue(), categoryField.getValue(), stockField.getValue());
            }
            dialog.close();
            updateList();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        
        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton, saveButton);
        
        dialog.open();
    }
    
    private void createProduct(String name, String description, BigDecimal price, String category, Integer stock) {
        try {
            CreateProductRequest request = new CreateProductRequest(name, description, price, category, stock);
            productUseCase.createProduct(request);
            showNotification("Produto criado com sucesso!", NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            String errorMessage = getSimplifiedErrorMessage(e.getMessage());
            showNotification("Erro ao criar produto: " + errorMessage, NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void updateProduct(UUID id, String name, String description, BigDecimal price, String category, Integer stock) {
        try {
            UpdateProductRequest request = new UpdateProductRequest(name, description, price, category, stock);
            productUseCase.updateProduct(id, request);
            showNotification("Produto atualizado com sucesso!", NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            String errorMessage = getSimplifiedErrorMessage(e.getMessage());
            showNotification("Erro ao atualizar produto: " + errorMessage, NotificationVariant.LUMO_ERROR);
        }
    }
    
    private String getSimplifiedErrorMessage(String fullMessage) {
        if (fullMessage == null) {
            return "Erro desconhecido";
        }
        
        // Remove códigos SQL e informações técnicas
        if (fullMessage.contains("ERRO:")) {
            int errorIndex = fullMessage.indexOf("ERRO:");
            String errorPart = fullMessage.substring(errorIndex + 5).trim();
            
            // Pega apenas a primeira linha do erro
            int newLineIndex = errorPart.indexOf("\n");
            if (newLineIndex > 0) {
                errorPart = errorPart.substring(0, newLineIndex);
            }
            
            // Remove informações técnicas entre colchetes
            errorPart = errorPart.replaceAll("\\[.*?\\]", "").trim();
            
            return errorPart;
        }
        
        // Se não tiver "ERRO:", retorna a mensagem original mas limita o tamanho
        if (fullMessage.length() > 100) {
            return fullMessage.substring(0, 100) + "...";
        }
        
        return fullMessage;
    }
    
    private void deleteProduct(Product product) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar Exclusão");
        
        VerticalLayout content = new VerticalLayout();
        content.add(new Span("Tem certeza que deseja excluir o produto: " + product.getName() + "?"));
        
        Button confirmButton = new Button("Excluir", e -> {
            try {
                productUseCase.deleteProduct(product.getId());
                showNotification("Produto excluído com sucesso!", NotificationVariant.LUMO_SUCCESS);
                updateList();
            } catch (Exception ex) {
                showNotification("Erro ao excluir produto: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        
        Button cancelButton = new Button("Cancelar", e -> confirmDialog.close());
        
        confirmDialog.add(content);
        confirmDialog.getFooter().add(cancelButton, confirmButton);
        confirmDialog.open();
    }
    
    private Details createFilterPanel() {
        H4 filterTitle = new H4("🔍 Filtros Avançados");
        filterTitle.getStyle().set("margin", "0");
        
        nameFilter = new TextField("Nome do Produto");
        nameFilter.setPlaceholder("Buscar por nome...");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(e -> applyFilters());
        
        categoryFilter = new TextField("Categoria");
        categoryFilter.setPlaceholder("Buscar por categoria...");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setValueChangeMode(ValueChangeMode.LAZY);
        categoryFilter.addValueChangeListener(e -> applyFilters());
        
        minPriceFilter = new BigDecimalField("Preço Mínimo");
        minPriceFilter.setPrefixComponent(new Span("R$"));
        minPriceFilter.setClearButtonVisible(true);
        minPriceFilter.addValueChangeListener(e -> applyFilters());
        
        maxPriceFilter = new BigDecimalField("Preço Máximo");
        maxPriceFilter.setPrefixComponent(new Span("R$"));
        maxPriceFilter.setClearButtonVisible(true);
        maxPriceFilter.addValueChangeListener(e -> applyFilters());
        
        minStockFilter = new IntegerField("Estoque Mínimo");
        minStockFilter.setClearButtonVisible(true);
        minStockFilter.setMin(0);
        minStockFilter.addValueChangeListener(e -> applyFilters());
        
        maxStockFilter = new IntegerField("Estoque Máximo");
        maxStockFilter.setClearButtonVisible(true);
        maxStockFilter.setMin(0);
        maxStockFilter.addValueChangeListener(e -> applyFilters());
        
        startDateFilter = new DatePicker("Data Inicial");
        startDateFilter.setClearButtonVisible(true);
        startDateFilter.addValueChangeListener(e -> applyFilters());
        
        endDateFilter = new DatePicker("Data Final");
        endDateFilter.setClearButtonVisible(true);
        endDateFilter.addValueChangeListener(e -> applyFilters());
        
        Button clearFiltersButton = new Button("Limpar Filtros", VaadinIcon.ERASER.create());
        clearFiltersButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        clearFiltersButton.addClickListener(e -> clearFilters());
        
        FormLayout filterForm = new FormLayout();
        filterForm.add(nameFilter, categoryFilter, minPriceFilter, maxPriceFilter, 
                      minStockFilter, maxStockFilter, startDateFilter, endDateFilter);
        filterForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("800px", 4)
        );
        
        VerticalLayout filterContent = new VerticalLayout(filterForm, clearFiltersButton);
        filterContent.setPadding(false);
        filterContent.setSpacing(true);
        
        Details details = new Details(filterTitle, filterContent);
        details.setWidthFull();
        details.setOpened(false);
        
        return details;
    }
    
    private void applyFilters() {
        if (allProducts == null) {
            return;
        }
        
        List<Product> filtered = allProducts.stream()
                .filter(this::matchesFilters)
                .collect(Collectors.toList());
        
        grid.setItems(filtered);
        
        if (hasActiveFilters()) {
            showNotification(
                String.format("Encontrados %d produto(s)", filtered.size()),
                NotificationVariant.LUMO_PRIMARY
            );
        }
    }
    
    private boolean matchesFilters(Product product) {
        if (nameFilter.getValue() != null && !nameFilter.getValue().isEmpty()) {
            if (!product.getName().toLowerCase().contains(nameFilter.getValue().toLowerCase())) {
                return false;
            }
        }
        
        if (categoryFilter.getValue() != null && !categoryFilter.getValue().isEmpty()) {
            if (!product.getCategory().toLowerCase().contains(categoryFilter.getValue().toLowerCase())) {
                return false;
            }
        }
        
        if (minPriceFilter.getValue() != null) {
            if (product.getPrice().compareTo(minPriceFilter.getValue()) < 0) {
                return false;
            }
        }
        
        if (maxPriceFilter.getValue() != null) {
            if (product.getPrice().compareTo(maxPriceFilter.getValue()) > 0) {
                return false;
            }
        }
        
        if (minStockFilter.getValue() != null) {
            if (product.getStock() < minStockFilter.getValue()) {
                return false;
            }
        }
        
        if (maxStockFilter.getValue() != null) {
            if (product.getStock() > maxStockFilter.getValue()) {
                return false;
            }
        }
        
        if (startDateFilter.getValue() != null) {
            LocalDate productDate = product.getCreatedAt().toLocalDate();
            if (productDate.isBefore(startDateFilter.getValue())) {
                return false;
            }
        }
        
        if (endDateFilter.getValue() != null) {
            LocalDate productDate = product.getCreatedAt().toLocalDate();
            if (productDate.isAfter(endDateFilter.getValue())) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean hasActiveFilters() {
        return (nameFilter.getValue() != null && !nameFilter.getValue().isEmpty()) ||
               (categoryFilter.getValue() != null && !categoryFilter.getValue().isEmpty()) ||
               minPriceFilter.getValue() != null ||
               maxPriceFilter.getValue() != null ||
               minStockFilter.getValue() != null ||
               maxStockFilter.getValue() != null ||
               startDateFilter.getValue() != null ||
               endDateFilter.getValue() != null;
    }
    
    private void clearFilters() {
        nameFilter.clear();
        categoryFilter.clear();
        minPriceFilter.clear();
        maxPriceFilter.clear();
        minStockFilter.clear();
        maxStockFilter.clear();
        startDateFilter.clear();
        endDateFilter.clear();
        
        applyFilters();
        showNotification("Filtros limpos!", NotificationVariant.LUMO_SUCCESS);
    }
    
    private void updateList() {
        allProducts = productRepository.findAll();
        applyFilters();
    }
    
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_END);
        notification.open();
    }
}
