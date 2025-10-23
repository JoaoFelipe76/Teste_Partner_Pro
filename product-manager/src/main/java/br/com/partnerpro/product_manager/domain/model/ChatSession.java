package br.com.partnerpro.product_manager.domain.model;

import lombok.Data;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ChatSession {
    private String sessionId;
    private List<Message> conversationHistory;
    private UUID lastProductId;
    private String lastProductName; 
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    
    public ChatSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.conversationHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    public ChatSession(String sessionId) {
        this.sessionId = sessionId;
        this.conversationHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    public void addMessage(Message message) {
        this.conversationHistory.add(message);
        this.lastActivity = LocalDateTime.now();
    }
    
    public void updateLastProduct(UUID productId, String productName) {
        this.lastProductId = productId;
        this.lastProductName = productName;
        this.lastActivity = LocalDateTime.now();
    }
}
