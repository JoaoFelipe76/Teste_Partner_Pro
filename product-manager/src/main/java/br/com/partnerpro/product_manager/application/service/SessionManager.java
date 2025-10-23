package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.domain.model.ChatSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SessionManager {
    
    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    public ChatSession getOrCreateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            log.info("Creating new session: {}", sessionId);
        }
        
        ChatSession session = sessions.get(sessionId);
        
        if (session == null) {
            session = new ChatSession(sessionId);
            sessions.put(sessionId, session);
            log.info("New session created: {}", sessionId);
        } else {
            if (isSessionExpired(session)) {
                log.info("Session expired, creating new one: {}", sessionId);
                session = new ChatSession(sessionId);
                sessions.put(sessionId, session);
            }
        }
        
        return session;
    }
    
    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("Session cleared: {}", sessionId);
    }
    
    public void clearAllSessions() {
        sessions.clear();
        log.info("All sessions cleared");
    }
    
    private boolean isSessionExpired(ChatSession session) {
        LocalDateTime expirationTime = session.getLastActivity().plusMinutes(SESSION_TIMEOUT_MINUTES);
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    public int getActiveSessionsCount() {
        return sessions.size();
    }
}
