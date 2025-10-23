package br.com.partnerpro.product_manager.framework.controller;

import br.com.partnerpro.product_manager.application.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExportController {
    
    private final ExportService exportService;
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    @GetMapping("/products/csv")
    public ResponseEntity<String> exportProductsToCSV() {
        try {
            String csv = exportService.exportProductsToCSV();
            String filename = "produtos_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".csv";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csv);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao gerar CSV: " + e.getMessage());
        }
    }
    
    @GetMapping("/products/pdf")
    public ResponseEntity<byte[]> exportProductsToPDF() {
        try {
            byte[] pdf = exportService.exportProductsToPDF();
            String filename = "produtos_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".pdf";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/dashboard/pdf")
    public ResponseEntity<byte[]> exportDashboardToPDF() {
        try {
            byte[] pdf = exportService.exportDashboardToPDF();
            String filename = "dashboard_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".pdf";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
