package br.com.partnerpro.product_manager.framework.controller;

import br.com.partnerpro.product_manager.application.service.AIAssistantService;
import br.com.partnerpro.product_manager.application.service.AIReportService;
import br.com.partnerpro.product_manager.application.service.SessionManager;
import br.com.partnerpro.product_manager.domain.model.ChatSession;
import br.com.partnerpro.product_manager.framework.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIReportController {
    
    private final AIReportService aiReportService;
    private final AIAssistantService aiAssistantService;
    private final SessionManager sessionManager;
    
    @PostMapping("/report")
    public ResponseEntity<AIReportResponse> generateReport(@Valid @RequestBody AIReportRequest request) {
        String report = aiReportService.generateNaturalLanguageReport(request.query());
        
        return ResponseEntity.ok(AIReportResponse.builder()
                .query(request.query())
                .report(report)
                .build());
    }
    
    @PostMapping("/query")
    public ResponseEntity<List<ProductResponse>> executeSmartQuery(@Valid @RequestBody AIReportRequest request) {
        List<ProductResponse> products = aiReportService.executeSmartQuery(request.query());
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String inputSessionId = request.sessionId();
        
        
        ChatSession session = sessionManager.getOrCreateSession(inputSessionId);
        String actualSessionId = session.getSessionId();
        
        String response = aiAssistantService.chat(actualSessionId, request.message());
        
        return ResponseEntity.ok(ChatResponse.builder()
                .sessionId(actualSessionId)
                .message(request.message())
                .response(response)
                .build());
    }
    
    @PostMapping("/chat/clear")
    public ResponseEntity<String> clearChatHistory(@RequestParam(required = false) String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            aiAssistantService.clearSession(sessionId);
            return ResponseEntity.ok("Sessão " + sessionId + " limpa com sucesso!");
        }
        return ResponseEntity.ok("Informe o sessionId para limpar");
    }
}
