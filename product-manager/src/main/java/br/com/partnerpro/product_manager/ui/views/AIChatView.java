package br.com.partnerpro.product_manager.ui.views;

import br.com.partnerpro.product_manager.application.service.AIAssistantService;
import br.com.partnerpro.product_manager.framework.dto.ChatRequest;
import br.com.partnerpro.product_manager.framework.dto.ChatResponse;
import br.com.partnerpro.product_manager.ui.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.UUID;

@Route(value = "ai-chat", layout = MainLayout.class)
@PageTitle("Assistente AI | Product Manager")
@jakarta.annotation.security.PermitAll
public class AIChatView extends VerticalLayout {
    
    private static final String SESSION_ID_KEY = "ai.chat.session.id";
    
    private final AIAssistantService aiAssistantService;
    private final VerticalLayout chatContainer;
    private final TextField messageField;
    private String sessionId;
    
    public AIChatView(AIAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
        
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        this.sessionId = (String) vaadinSession.getAttribute(SESSION_ID_KEY);
        
        if (this.sessionId == null) {
            this.sessionId = UUID.randomUUID().toString();
            vaadinSession.setAttribute(SESSION_ID_KEY, this.sessionId);
        }
        
        setSizeFull();
        setPadding(true);
        
        addCustomStyles();
        
        H2 title = new H2("Assistente AI");
        
        Button newChatButton = new Button("Nova Conversa", VaadinIcon.PLUS.create());
        newChatButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        newChatButton.addClickListener(e -> startNewChat());
        
        HorizontalLayout header = new HorizontalLayout(title, newChatButton);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        
        chatContainer = new VerticalLayout();
        chatContainer.addClassName("chat-container");
        chatContainer.setSizeFull();
        chatContainer.getStyle()
                .set("overflow-y", "auto")
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-m)");
        
        addWelcomeMessage();
        
        messageField = new TextField();
        messageField.setPlaceholder("Digite sua mensagem...");
        messageField.setWidthFull();
        
        Button sendButton = new Button("Enviar", VaadinIcon.PAPERPLANE.create());
        sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendButton.addClickListener(e -> sendMessage());
        
        messageField.addKeyPressListener(event -> {
            if (event.getKey().getKeys().contains("Enter")) {
                sendMessage();
            }
        });
        
        HorizontalLayout inputLayout = new HorizontalLayout(messageField, sendButton);
        inputLayout.setWidthFull();
        inputLayout.setAlignItems(Alignment.END);
        inputLayout.expand(messageField);
        
        add(header, chatContainer, inputLayout);
        setFlexGrow(1, chatContainer);
    }
    
    private void addWelcomeMessage() {
        Div welcomeMessage = createMessageBubble(
                "Olá! Sou seu assistente AI. Posso ajudá-lo com informações sobre produtos, " +
                "estatísticas, análises e muito mais. Como posso ajudá-lo hoje?",
                false
        );
        chatContainer.add(welcomeMessage);
    }
    
    private void startNewChat() {
        sessionId = UUID.randomUUID().toString();
        VaadinSession.getCurrent().setAttribute(SESSION_ID_KEY, sessionId);
        chatContainer.removeAll();
        addWelcomeMessage();
        showNotification("Nova conversa iniciada!", NotificationVariant.LUMO_SUCCESS);
    }
    
    private void sendMessage() {
        String message = messageField.getValue().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        Div userMessage = createMessageBubble(message, true);
        chatContainer.add(userMessage);
        
        messageField.clear();
        messageField.setEnabled(false);
        
        Div loadingMessage = createLoadingBubble();
        chatContainer.add(loadingMessage);
        
        chatContainer.getElement().executeJs("this.scrollTop = this.scrollHeight");
        
        UI ui = UI.getCurrent();
        
        new Thread(() -> {
            try {
                br.com.partnerpro.product_manager.framework.dto.AIResponse response = 
                        aiAssistantService.chatWithCharts(sessionId, message);
                
                ui.access(() -> {
                    chatContainer.remove(loadingMessage);
                    
                    Div aiMessage = createMessageBubble(response.message(), false);
                    chatContainer.add(aiMessage);
                    
                    if (response.hasChart()) {
                        Div chartDiv = createChartVisualization(response.chartData());
                        chatContainer.add(chartDiv);
                    }
                    
                    chatContainer.getElement().executeJs("this.scrollTop = this.scrollHeight");
                    
                    messageField.setEnabled(true);
                    messageField.focus();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                ui.access(() -> {
                    chatContainer.remove(loadingMessage);
                    Div errorMessage = createMessageBubble(
                            "Desculpe, ocorreu um erro: " + e.getMessage(),
                            false
                    );
                    errorMessage.getStyle().set("border-color", "var(--lumo-error-color)");
                    chatContainer.add(errorMessage);
                    
                    messageField.setEnabled(true);
                    messageField.focus();
                    
                    showNotification("Erro: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
                });
            }
        }).start();
    }
    
    private Div createMessageBubble(String text, boolean isUser) {
        Div bubble = new Div();
        bubble.addClassName("message-bubble");
        
        Paragraph content = new Paragraph(text);
        content.getStyle()
                .set("margin", "0")
                .set("white-space", "pre-wrap")
                .set("word-wrap", "break-word");
        
        bubble.add(content);
        bubble.getStyle()
                .set("max-width", "70%")
                .set("padding", "var(--lumo-space-m)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("margin-bottom", "var(--lumo-space-s)")
                .set("align-self", isUser ? "flex-end" : "flex-start")
                .set("background-color", isUser ? "var(--lumo-primary-color)" : "var(--lumo-contrast-10pct)")
                .set("color", isUser ? "var(--lumo-primary-contrast-color)" : "var(--lumo-body-text-color)");
        
        return bubble;
    }
    
    private Div createLoadingBubble() {
        Div bubble = new Div();
        bubble.addClassName("loading-bubble");
        
        Span dot1 = new Span("•");
        Span dot2 = new Span("•");
        Span dot3 = new Span("•");
        
        dot1.getStyle().set("animation", "loading 1.4s infinite");
        dot2.getStyle().set("animation", "loading 1.4s infinite 0.2s");
        dot3.getStyle().set("animation", "loading 1.4s infinite 0.4s");
        
        HorizontalLayout dots = new HorizontalLayout(dot1, dot2, dot3);
        dots.setSpacing(false);
        dots.getStyle().set("gap", "4px");
        
        bubble.add(dots);
        bubble.getStyle()
                .set("max-width", "70%")
                .set("padding", "var(--lumo-space-m)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("margin-bottom", "var(--lumo-space-s)")
                .set("align-self", "flex-start")
                .set("background-color", "var(--lumo-contrast-10pct)");
        
        return bubble;
    }
    
    private Div createChartVisualization(br.com.partnerpro.product_manager.framework.dto.ChartData chartData) {
        Div container = new Div();
        container.addClassName("chart-container");
        container.getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-m)")
                .set("margin-bottom", "var(--lumo-space-s)")
                .set("max-width", "90%")
                .set("align-self", "flex-start");
        
        H3 title = new H3(chartData.title());
        title.getStyle().set("margin-top", "0");
        
        if ("pie".equals(chartData.type())) {
            container.add(title, createPieChart(chartData));
        } else if ("bar".equals(chartData.type())) {
            container.add(title, createBarChart(chartData));
        } else if ("column".equals(chartData.type())) {
            container.add(title, createColumnChart(chartData));
        }
        
        return container;
    }
    
    private VerticalLayout createPieChart(br.com.partnerpro.product_manager.framework.dto.ChartData chartData) {
        VerticalLayout chart = new VerticalLayout();
        chart.setSpacing(true);
        chart.setPadding(false);
        chart.setWidthFull();
        
        double total = chartData.values().stream().mapToDouble(Double::doubleValue).sum();
        String[] colors = {"#008FFB", "#00E396", "#FEB019", "#FF4560", "#775DD0", "#546E7A"};
        
        for (int i = 0; i < chartData.labels().size(); i++) {
            String label = chartData.labels().get(i);
            double value = chartData.values().get(i);
            double percentage = (value / total) * 100;
            
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setAlignItems(Alignment.CENTER);
            row.setSpacing(true);
            
            Div colorBox = new Div();
            colorBox.getStyle()
                    .set("width", "20px")
                    .set("height", "20px")
                    .set("background-color", colors[i % colors.length])
                    .set("border-radius", "4px");
            
            Span labelSpan = new Span(label);
            labelSpan.setWidth("150px");
            
            Span valueSpan = new Span(String.format("%.0f (%.1f%%)", value, percentage));
            valueSpan.getStyle().set("font-weight", "bold");
            
            row.add(colorBox, labelSpan, valueSpan);
            chart.add(row);
        }
        
        return chart;
    }
    
    private VerticalLayout createBarChart(br.com.partnerpro.product_manager.framework.dto.ChartData chartData) {
        VerticalLayout chart = new VerticalLayout();
        chart.setSpacing(true);
        chart.setPadding(false);
        chart.setWidthFull();
        
        double maxValue = chartData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        
        @SuppressWarnings("unchecked")
        List<String> colors = (List<String>) chartData.metadata().getOrDefault("colors", 
                java.util.Arrays.asList("#008FFB", "#00E396", "#FEB019", "#FF4560", "#775DD0"));
        
        for (int i = 0; i < chartData.labels().size(); i++) {
            String label = chartData.labels().get(i);
            double value = chartData.values().get(i);
            double percentage = (value / maxValue) * 100;
            
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setAlignItems(Alignment.CENTER);
            row.setSpacing(true);
            
            Span labelSpan = new Span(label);
            labelSpan.setWidth("150px");
            labelSpan.getStyle().set("font-weight", "500");
            
            Div barContainer = new Div();
            barContainer.setWidthFull();
            barContainer.getStyle()
                    .set("background-color", "var(--lumo-contrast-10pct)")
                    .set("border-radius", "4px")
                    .set("height", "30px")
                    .set("position", "relative");
            
            Div bar = new Div();
            bar.getStyle()
                    .set("background-color", colors.get(i % colors.size()))
                    .set("height", "100%")
                    .set("width", percentage + "%")
                    .set("border-radius", "4px")
                    .set("transition", "width 0.3s ease")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "flex-end")
                    .set("padding-right", "10px");
            
            Span valueLabel = new Span(String.format("%.0f", value));
            valueLabel.getStyle()
                    .set("font-weight", "bold")
                    .set("color", "white")
                    .set("font-size", "0.9rem");
            
            bar.add(valueLabel);
            barContainer.add(bar);
            
            row.add(labelSpan, barContainer);
            chart.add(row);
        }
        
        return chart;
    }
    
    private HorizontalLayout createColumnChart(br.com.partnerpro.product_manager.framework.dto.ChartData chartData) {
        HorizontalLayout chart = new HorizontalLayout();
        chart.setWidthFull();
        chart.setAlignItems(Alignment.END);
        chart.setJustifyContentMode(JustifyContentMode.AROUND);
        chart.getStyle().set("height", "200px");
        
        double maxValue = chartData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        String color = (String) chartData.metadata().getOrDefault("color", "#00E396");
        
        for (int i = 0; i < chartData.labels().size(); i++) {
            String label = chartData.labels().get(i);
            double value = chartData.values().get(i);
            double heightPercentage = (value / maxValue) * 100;
            
            VerticalLayout column = new VerticalLayout();
            column.setAlignItems(Alignment.CENTER);
            column.setSpacing(false);
            column.setPadding(false);
            column.setWidth("80px");
            
            Span valueLabel = new Span(String.format("%.0f", value));
            valueLabel.getStyle()
                    .set("font-weight", "bold")
                    .set("margin-bottom", "5px")
                    .set("font-size", "0.9rem");
            
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
                    .set("margin-top", "5px")
                    .set("text-align", "center");
            
            column.add(valueLabel, bar, labelSpan);
            chart.add(column);
        }
        
        return chart;
    }
    
    private void addCustomStyles() {
        getElement().executeJs(
            "const style = document.createElement('style');" +
            "style.textContent = `" +
            "@keyframes blink {" +
            "  0%, 100% { opacity: 1; }" +
            "  50% { opacity: 0.3; }" +
            "}" +
            "`;" +
            "document.head.appendChild(style);"
        );
    }
    
    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_END);
        notification.open();
    }
}
