package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.ProductResponse;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {
    
    private final ProductRepository productRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public String exportProductsToCSV() throws IOException {
        log.info("Exporting products to CSV");
        
        List<Product> products = productRepository.findAll();
        
        StringWriter writer = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Nome", "Descrição", "Preço", "Categoria", "Estoque", "Data de Criação"));
        
        for (Product product : products) {
            csvPrinter.printRecord(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getCategory(),
                    product.getStock(),
                    product.getCreatedAt().format(DATE_FORMATTER)
            );
        }
        
        csvPrinter.flush();
        log.info("Exported {} products to CSV", products.size());
        
        return writer.toString();
    }
    
    public byte[] exportProductsToPDF() throws IOException {
        log.info("Exporting products to PDF");
        
        List<Product> products = productRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
       
        Paragraph title = new Paragraph("Relatório de Produtos")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);
        
        BigDecimal totalValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalStock = products.stream().mapToInt(Product::getStock).sum();
        
        Paragraph stats = new Paragraph(String.format(
                "Total de Produtos: %d | Estoque Total: %d unidades | Valor Total: R$ %.2f",
                products.size(), totalStock, totalValue
        ))
                .setFontSize(10)
                .setMarginBottom(15);
        document.add(stats);
        
        float[] columnWidths = {2, 3, 2, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        
        String[] headers = {"Nome", "Descrição", "Preço", "Categoria", "Estoque", "Data"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }
        
        for (Product product : products) {
            table.addCell(new Cell().add(new Paragraph(product.getName())));
            table.addCell(new Cell().add(new Paragraph(
                    product.getDescription() != null && product.getDescription().length() > 50
                            ? product.getDescription().substring(0, 47) + "..."
                            : product.getDescription() != null ? product.getDescription() : ""
            )));
            table.addCell(new Cell().add(new Paragraph(String.format("R$ %.2f", product.getPrice()))));
            table.addCell(new Cell().add(new Paragraph(product.getCategory())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(product.getStock()))));
            table.addCell(new Cell().add(new Paragraph(product.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
        }
        
        document.add(table);
        
        Paragraph footer = new Paragraph("Product Manager - Sistema de Gerenciamento de Produtos")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);
        
        document.close();
        
        log.info("Exported {} products to PDF", products.size());
        return baos.toByteArray();
    }
    
    public byte[] exportDashboardToPDF() throws IOException {
        log.info("Exporting dashboard to PDF");
        
        List<Product> products = productRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        Paragraph title = new Paragraph("Dashboard - Relatório Executivo")
                .setFontSize(22)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
        document.add(subtitle);
        
        BigDecimal totalValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgPrice = products.isEmpty() ? BigDecimal.ZERO :
                products.stream()
                        .map(Product::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(products.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        int totalStock = products.stream().mapToInt(Product::getStock).sum();
        long lowStock = products.stream().filter(p -> p.getStock() < 10).count();
        long outOfStock = products.stream().filter(p -> p.getStock() == 0).count();
        
        document.add(new Paragraph("📊 ESTATÍSTICAS GERAIS").setBold().setFontSize(14).setMarginBottom(10));
        document.add(new Paragraph(String.format("• Total de Produtos: %d", products.size())));
        document.add(new Paragraph(String.format("• Estoque Total: %d unidades", totalStock)));
        document.add(new Paragraph(String.format("• Valor Total em Estoque: R$ %.2f", totalValue)));
        document.add(new Paragraph(String.format("• Preço Médio: R$ %.2f", avgPrice)));
        document.add(new Paragraph(String.format("• Produtos com Estoque Baixo (<10): %d", lowStock)));
        document.add(new Paragraph(String.format("• Produtos Sem Estoque: %d", outOfStock)).setMarginBottom(20));
        
        document.add(new Paragraph("📁 PRODUTOS POR CATEGORIA").setBold().setFontSize(14).setMarginBottom(10));
        products.stream()
                .collect(java.util.stream.Collectors.groupingBy(Product::getCategory))
                .forEach((category, prods) -> {
                    document.add(new Paragraph(String.format("• %s: %d produtos", category, prods.size())));
                });
        
        document.add(new Paragraph("").setMarginBottom(20));
        
        
        document.add(new Paragraph("💰 TOP 5 PRODUTOS MAIS CAROS").setBold().setFontSize(14).setMarginBottom(10));
        products.stream()
                .sorted((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()))
                .limit(5)
                .forEach(p -> document.add(new Paragraph(
                        String.format("• %s - R$ %.2f (Estoque: %d)", p.getName(), p.getPrice(), p.getStock())
                )));
        
        document.add(new Paragraph("").setMarginBottom(20));
        
        
        if (lowStock > 0) {
            document.add(new Paragraph("⚠️ ALERTAS DE ESTOQUE BAIXO").setBold().setFontSize(14).setMarginBottom(10));
            products.stream()
                    .filter(p -> p.getStock() < 10)
                    .forEach(p -> document.add(new Paragraph(
                            String.format("• %s - Apenas %d unidades", p.getName(), p.getStock())
                    )));
        }
        
        Paragraph footer = new Paragraph("Product Manager - Sistema de Gerenciamento de Produtos")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);
        
        document.close();
        
        log.info("Dashboard PDF exported successfully");
        return baos.toByteArray();
    }
    
    
    public byte[] exportToCsv(List<ProductResponse> products) {
        log.info("Exporting {} products to CSV", products.size());
        
        try {
            StringWriter writer = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("ID", "Nome", "Descrição", "Preço", "Categoria", "Estoque", "Data de Criação"));
            
            for (ProductResponse product : products) {
                csvPrinter.printRecord(
                        product.id(),
                        product.name(),
                        product.description(),
                        product.price(),
                        product.category(),
                        product.stock(),
                        product.createdAt().format(DATE_FORMATTER)
                );
            }
            
            csvPrinter.flush();
            log.info("CSV export completed successfully");
            
            return writer.toString().getBytes();
        } catch (IOException e) {
            log.error("Error exporting to CSV", e);
            throw new RuntimeException("Failed to export to CSV", e);
        }
    }
    
    public byte[] exportToPdf(List<ProductResponse> products) {
        log.info("Exporting {} products to PDF", products.size());
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            Paragraph title = new Paragraph("Relatório de Produtos")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitle);
            
            BigDecimal totalValue = products.stream()
                    .map(p -> p.price().multiply(BigDecimal.valueOf(p.stock())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int totalStock = products.stream().mapToInt(ProductResponse::stock).sum();
            
            Paragraph stats = new Paragraph(String.format(
                    "Total de Produtos: %d | Estoque Total: %d unidades | Valor Total: R$ %.2f",
                    products.size(), totalStock, totalValue
            ))
                    .setFontSize(10)
                    .setMarginBottom(15);
            document.add(stats);
            
            float[] columnWidths = {2, 3, 2, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            
            String[] headers = {"Nome", "Descrição", "Preço", "Categoria", "Estoque", "Data"};
            for (String header : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }
            
            for (ProductResponse product : products) {
                table.addCell(new Cell().add(new Paragraph(product.name())));
                table.addCell(new Cell().add(new Paragraph(
                        product.description() != null && product.description().length() > 50
                                ? product.description().substring(0, 47) + "..."
                                : product.description() != null ? product.description() : ""
                )));
                table.addCell(new Cell().add(new Paragraph(String.format("R$ %.2f", product.price()))));
                table.addCell(new Cell().add(new Paragraph(product.category())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(product.stock()))));
                table.addCell(new Cell().add(new Paragraph(product.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
            }
            
            document.add(table);
            
            Paragraph footer = new Paragraph("Product Manager - Sistema de Gerenciamento de Produtos")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);
            
            document.close();
            
            log.info("PDF export completed successfully");
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting to PDF", e);
            throw new RuntimeException("Failed to export to PDF", e);
        }
    }
}
