package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.application.usecase.ProductUseCase;
import br.com.partnerpro.product_manager.domain.entity.Product;
import br.com.partnerpro.product_manager.domain.model.ChatSession;
import br.com.partnerpro.product_manager.domain.repository.ProductRepository;
import br.com.partnerpro.product_manager.framework.dto.CreateProductRequest;
import br.com.partnerpro.product_manager.framework.dto.ProductResponse;
import br.com.partnerpro.product_manager.framework.dto.UpdateProductRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AIAssistantService {
    
    private final ChatClient chatClient;
    private final ProductRepository productRepository;
    private final ProductUseCase productUseCase;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;
    private final ChartGeneratorService chartGeneratorService;
    
    public AIAssistantService(
            ChatClient.Builder chatClientBuilder,
            ProductRepository productRepository,
            ProductUseCase productUseCase,
            ObjectMapper objectMapper,
            SessionManager sessionManager,
            ChartGeneratorService chartGeneratorService
    ) {
        this.chatClient = chatClientBuilder.build();
        this.productRepository = productRepository;
        this.productUseCase = productUseCase;
        this.objectMapper = objectMapper;
        this.sessionManager = sessionManager;
        this.chartGeneratorService = chartGeneratorService;
    }
    
    public String chat(String sessionId, String userMessage) {
        ChatSession session = sessionManager.getOrCreateSession(sessionId);
        log.info("[Session: {}] User message: {}", sessionId, userMessage);
        
        session.addMessage(new UserMessage(userMessage));
        
        List<Product> allProducts = productRepository.findAll();
        
        StringBuilder context = new StringBuilder();
        context.append("Você é um assistente inteligente para gerenciamento de produtos.\n\n");
        context.append("Produtos disponíveis no sistema:\n");
        for (Product p : allProducts) {
            context.append(String.format(
                "- ID: %s | Nome: %s | Preço: R$ %.2f | Categoria: %s | Estoque: %d\n",
                p.getId(), p.getName(), p.getPrice(), p.getCategory(), p.getStock()
            ));
        }
        
        context.append("\n\n🎯 CAPACIDADES DO SISTEMA:\n");
        context.append("1. ADICIONAR produto: Quando o usuário pedir para adicionar/criar/inserir um produto\n");
        context.append("2. ATUALIZAR produto: Quando o usuário pedir para atualizar/modificar/editar um produto\n");
        context.append("3. DELETAR produto: Quando o usuário pedir para deletar/remover/excluir um produto\n");
        context.append("4. LISTAR produtos: Quando o usuário pedir para listar/mostrar produtos\n");
        context.append("5. GERAR RELATÓRIO: Quando o usuário pedir análises ou relatórios\n");
        context.append("6. 📊 GERAR GRÁFICOS: O sistema PODE e VAI gerar gráficos automaticamente!\n");
        context.append("   - Quando o usuário pedir gráficos, confirme que o gráfico será exibido\n");
        context.append("   - Tipos disponíveis: produtos por categoria, distribuição de preços, níveis de estoque, valor por categoria, preço médio\n");
        context.append("   - Exemplo de resposta: 'Claro! Vou gerar o gráfico de produtos por categoria para você. Aqui estão os dados:'\n\n");
        
        if (session.getLastProductId() != null) {
            context.append("\n🔖 CONTEXTO DA CONVERSA:\n");
            context.append(String.format("Último produto mencionado: %s (ID: %s)\n\n", 
                session.getLastProductName(), session.getLastProductId()));
        }
        
        context.append("IMPORTANTE:\n");
        context.append("- Quando for ADICIONAR um produto, responda no formato JSON:\n");
        context.append("  {\"action\": \"CREATE\", \"name\": \"nome\", \"description\": \"desc\", \"price\": 100.00, \"category\": \"Electronics\", \"stock\": 0}\n");
        context.append("- Quando for ATUALIZAR um produto, responda no formato JSON:\n");
        context.append("  {\"action\": \"UPDATE\", \"id\": \"uuid-do-produto\", \"name\": \"nome\", \"description\": \"desc\", \"price\": 100.00, \"category\": \"categoria\", \"stock\": 10}\n");
        context.append("  * Se o usuário não especificar qual produto, use o ID do último produto mencionado\n");
        context.append("  * Se o usuário só mencionar um campo (ex: estoque), mantenha os outros campos do produto atual\n");
        context.append("- Quando for DELETAR um produto, responda no formato JSON:\n");
        context.append("  {\"action\": \"DELETE\", \"id\": \"uuid\"}\n");
        context.append("- Para outras ações, responda normalmente em português de forma amigável.\n\n");
        
        context.append("Mensagem do usuário: ").append(userMessage);
        
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(context.toString()));
        
        Prompt prompt = new Prompt(messages);
        
        String aiResponse = chatClient.prompt(prompt).call().content();
        
        log.info("[Session: {}] AI Response: {}", sessionId, aiResponse);
        
        String finalResponse = processAIResponse(session, aiResponse);
        
        session.addMessage(new UserMessage("Assistant: " + finalResponse));
        
        return finalResponse;
    }
    
    private String processAIResponse(ChatSession session, String aiResponse) {
        try {
            if (aiResponse.trim().startsWith("{") && aiResponse.contains("\"action\"")) {
                JsonNode jsonNode = objectMapper.readTree(aiResponse);
                String action = jsonNode.get("action").asText();
                
                return switch (action) {
                    case "CREATE" -> executeCreate(session, jsonNode);
                    case "UPDATE" -> executeUpdate(session, jsonNode);
                    case "DELETE" -> executeDelete(session, jsonNode);
                    default -> aiResponse;
                };
            }
        } catch (JsonProcessingException e) {
            log.debug("Response is not a JSON action, returning as text");
        }
        
        return aiResponse;
    }
    
    private String executeCreate(ChatSession session, JsonNode json) {
        try {
            CreateProductRequest request = new CreateProductRequest(
                json.get("name").asText(),
                json.has("description") ? json.get("description").asText() : "",
                new BigDecimal(json.get("price").asText()),
                json.get("category").asText(),
                json.has("stock") ? json.get("stock").asInt() : 0
            );
            
            ProductResponse product = productUseCase.createProduct(request);
            
            session.updateLastProduct(product.id(), product.name());
            
            return String.format(
                "✅ Produto adicionado com sucesso!\n\n" +
                "📦 **%s**\n" +
                "💰 Preço: R$ %.2f\n" +
                "📁 Categoria: %s\n" +
                "📊 Estoque: %d unidades\n" +
                "🆔 ID: %s",
                product.name(),
                product.price(),
                product.category(),
                product.stock(),
                product.id()
            );
        } catch (Exception e) {
            log.error("Error creating product", e);
            return "❌ Ops! Não consegui adicionar o produto. Erro: " + e.getMessage();
        }
    }
    
    private String executeUpdate(ChatSession session, JsonNode json) {
        try {
            UUID productId;
            if (json.has("id") && json.get("id") != null && !json.get("id").asText().isEmpty()) {
                productId = UUID.fromString(json.get("id").asText());
            } else if (session.getLastProductId() != null) {
                productId = session.getLastProductId();
                log.info("Using last product ID from session: {}", productId);
            } else {
                return "❌ Não consegui identificar qual produto atualizar. Por favor, especifique o produto.";
            }
            
            Product currentProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            
            UpdateProductRequest request = new UpdateProductRequest(
                json.has("name") && !json.get("name").asText().isEmpty() 
                    ? json.get("name").asText() : currentProduct.getName(),
                json.has("description") && !json.get("description").asText().isEmpty()
                    ? json.get("description").asText() : currentProduct.getDescription(),
                json.has("price") && json.get("price") != null
                    ? new BigDecimal(json.get("price").asText()) : currentProduct.getPrice(),
                json.has("category") && !json.get("category").asText().isEmpty()
                    ? json.get("category").asText() : currentProduct.getCategory(),
                json.has("stock") && json.get("stock") != null
                    ? json.get("stock").asInt() : currentProduct.getStock()
            );
            
            ProductResponse product = productUseCase.updateProduct(productId, request);
            
            session.updateLastProduct(product.id(), product.name());
            
            return String.format(
                "✅ Tudo atualizado! :)\n\n" +
                "📦 **%s**\n" +
                "💰 Preço: R$ %.2f\n" +
                "📁 Categoria: %s\n" +
                "📊 Estoque: %d unidades",
                product.name(),
                product.price(),
                product.category(),
                product.stock()
            );
        } catch (Exception e) {
            log.error("Error updating product", e);
            return "❌ Ops! Não consegui atualizar o produto. Erro: " + e.getMessage();
        }
    }
    
    private String executeDelete(ChatSession session, JsonNode json) {
        try {
            UUID productId;
            if (json.has("id")) {
                productId = UUID.fromString(json.get("id").asText());
            } else if (session.getLastProductId() != null) {
                productId = session.getLastProductId();
            } else {
                return "❌ Não consegui identificar qual produto remover.";
            }
            
            productUseCase.deleteProduct(productId);
            session.updateLastProduct(null, null);
            
            return "✅ Produto removido com sucesso! 🗑️";
        } catch (Exception e) {
            log.error("Error deleting product", e);
            return "❌ Ops! Não consegui remover o produto. Erro: " + e.getMessage();
        }
    }
    
    public void clearSession(String sessionId) {
        sessionManager.clearSession(sessionId);
        log.info("Session cleared: {}", sessionId);
    }
    
    public br.com.partnerpro.product_manager.framework.dto.AIResponse chatWithCharts(String sessionId, String userMessage) {
        List<Product> allProducts = productRepository.findAll();
        
        br.com.partnerpro.product_manager.framework.dto.ChartData chartData = 
                chartGeneratorService.detectAndGenerateChart(userMessage, allProducts);
        
        String textResponse = chat(sessionId, userMessage);
        
        if (chartData != null) {
            return new br.com.partnerpro.product_manager.framework.dto.AIResponse(textResponse, chartData);
        }
        
        return new br.com.partnerpro.product_manager.framework.dto.AIResponse(textResponse);
    }
}
