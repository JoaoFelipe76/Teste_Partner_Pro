package br.com.partnerpro.product_manager.framework.controller;

import br.com.partnerpro.product_manager.application.service.ExportService;
import br.com.partnerpro.product_manager.application.usecase.ProductUseCase;
import br.com.partnerpro.product_manager.framework.dto.CreateProductRequest;
import br.com.partnerpro.product_manager.framework.dto.ProductFilterRequest;
import br.com.partnerpro.product_manager.framework.dto.ProductResponse;
import br.com.partnerpro.product_manager.framework.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "API para gerenciamento de produtos")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ExportService exportService;

    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista paginada de todos os produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/products - Listing all products with pagination");
        Page<ProductResponse> products = productUseCase.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID do produto") @PathVariable UUID id) {
        log.info("GET /api/products/{} - Getting product by ID", id);
        ProductResponse product = productUseCase.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Criar novo produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("POST /api/products - Creating new product: {}", request.name());
        ProductResponse product = productUseCase.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "ID do produto") @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("PUT /api/products/{} - Updating product", id);
        ProductResponse product = productUseCase.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID do produto") @PathVariable UUID id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filtrar produtos", description = "Busca produtos com filtros avançados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos filtrados retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @PostMapping("/filter")
    public ResponseEntity<Page<ProductResponse>> filterProducts(
            @RequestBody ProductFilterRequest filter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("POST /api/products/filter - Filtering products");
        Page<ProductResponse> products = productUseCase.filterProducts(filter, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Exportar produtos para CSV", description = "Gera um arquivo CSV com todos os produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CSV gerado com sucesso")
    })
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportToCsv() {
        log.info("GET /api/products/export/csv - Exporting products to CSV");
        try {
            String csvData = exportService.exportProductsToCSV();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "produtos.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Error exporting to CSV", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Exportar produtos para PDF", description = "Gera um arquivo PDF com todos os produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso")
    })
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPdf() {
        log.info("GET /api/products/export/pdf - Exporting products to PDF");
        try {
            byte[] pdfData = exportService.exportProductsToPDF();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "produtos.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);
        } catch (Exception e) {
            log.error("Error exporting to PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
