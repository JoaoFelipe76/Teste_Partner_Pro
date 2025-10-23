package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIReportService {
    
    private final ChatClient chatClient;
    private final ProductRepository productRepository;
    
    public AIReportService(ChatClient.Builder chatClientBuilder, ProductRepository productRepository) {
        this.chatClient = chatClientBuilder.build();
        this.productRepository = productRepository;
    }
    
    public String generateNaturalLanguageReport(String userRequest) {
        log.info("Generating report for request: {}", userRequest);
        
        List<Product> allProducts = productRepository.findAll();
        
        StringBuilder productsContext = new StringBuilder();
        productsContext.append("Available products in database:\n");
        for (Product product : allProducts) {
            productsContext.append(String.format(
                "- ID: %s, Name: %s, Price: %.2f, Category: %s, Stock: %d, Description: %s\n",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getStock(),
                product.getDescription()
            ));
        }
        
        String promptText = """
                You are a data analyst assistant for a product management system.
                
                User Request: {userRequest}
                
                {productsContext}
                
                Based on the user's request and the available products, generate a detailed report.
                
                Instructions:
                1. Analyze the user's request to understand what filters they want (price range, stock level, category, etc.)
                2. Filter the products according to the criteria
                3. Generate a professional report in Portuguese (Brazil) with:
                   - A summary of the request
                   - The filtered products in a clear format
                   - Statistics (total products found, average price, total stock, etc.)
                   - Insights or recommendations if applicable
                
                Format the response in a clear, professional manner with sections and bullet points.
                """;
        
        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
            "userRequest", userRequest,
            "productsContext", productsContext.toString()
        ));
        
        String response = chatClient.prompt(prompt)
                .call()
                .content();
        
        log.info("Report generated successfully");
        return response;
    }
    
    public List<ProductResponse> executeSmartQuery(String naturalLanguageQuery) {
        log.info("Executing smart query: {}", naturalLanguageQuery);
        
        List<Product> allProducts = productRepository.findAll();
        
        StringBuilder productsContext = new StringBuilder();
        productsContext.append("Database schema:\n");
        productsContext.append("Table: products\n");
        productsContext.append("Columns: id (UUID), name (String), description (String), price (Decimal), category (String), stock (Integer), created_at (DateTime)\n\n");
        productsContext.append("Sample data:\n");
        
        for (int i = 0; i < Math.min(5, allProducts.size()); i++) {
            Product p = allProducts.get(i);
            productsContext.append(String.format(
                "- %s | %.2f | %s | Stock: %d\n",
                p.getName(), p.getPrice(), p.getCategory(), p.getStock()
            ));
        }
        
        String promptText = """
                You are a SQL query generator for a product management system.
                
                User Query: {query}
                
                {context}
                
                Based on the user's natural language query, determine which products match the criteria.
                
                Respond ONLY with a JSON array of product IDs that match the criteria.
                Format: ["id1", "id2", "id3"]
                
                If no products match, return an empty array: []
                
                Do not include any explanation, just the JSON array.
                """;
        
        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
            "query", naturalLanguageQuery,
            "context", productsContext.toString()
        ));
        
        String aiResponse = chatClient.prompt(prompt)
                .call()
                .content();
        
        log.info("AI Response: {}", aiResponse);
        
        return allProducts.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
}
