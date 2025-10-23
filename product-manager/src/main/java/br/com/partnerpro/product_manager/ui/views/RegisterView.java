package br.com.partnerpro.product_manager.ui.views;

import br.com.partnerpro.product_manager.application.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register")
@PageTitle("Cadastro | Product Manager")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    
    private final UserService userService;
    
    private final TextField usernameField = new TextField("Usuário");
    private final TextField fullNameField = new TextField("Nome Completo");
    private final EmailField emailField = new EmailField("E-mail");
    private final PasswordField passwordField = new PasswordField("Senha");
    private final PasswordField confirmPasswordField = new PasswordField("Confirmar Senha");
    private final Button registerButton = new Button("Cadastrar");
    
    public RegisterView(UserService userService) {
        this.userService = userService;
        
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        H1 title = new H1("Criar Conta");
        title.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("margin-bottom", "0");
        
        Paragraph subtitle = new Paragraph("Preencha os dados para criar sua conta");
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-top", "0");
        
        configureForm();
        
        RouterLink loginLink = new RouterLink("Já tem uma conta? Faça login", LoginView.class);
        loginLink.getStyle()
                .set("margin-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-s)");
        
        VerticalLayout registerLayout = new VerticalLayout(
                title,
                subtitle,
                createForm(),
                registerButton,
                loginLink
        );
        
        registerLayout.setAlignItems(Alignment.CENTER);
        registerLayout.setWidth("400px");
        registerLayout.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("background-color", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-m)");
        
        add(registerLayout);
    }
    
    private void configureForm() {
        usernameField.setRequired(true);
        usernameField.setMinLength(3);
        usernameField.setMaxLength(50);
        usernameField.setHelperText("Mínimo 3 caracteres");
        
        fullNameField.setRequired(true);
        fullNameField.setMaxLength(100);
        
        emailField.setRequired(true);
        emailField.setErrorMessage("Digite um e-mail válido");
        
        passwordField.setRequired(true);
        passwordField.setMinLength(6);
        passwordField.setHelperText("Mínimo 6 caracteres");
        
        confirmPasswordField.setRequired(true);
        
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClickListener(e -> handleRegister());
    }
    
    private FormLayout createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(
                usernameField,
                fullNameField,
                emailField,
                passwordField,
                confirmPasswordField
        );
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return formLayout;
    }
    
    private void handleRegister() {
        if (!validateForm()) {
            return;
        }
        
        try {
            userService.registerUser(
                    usernameField.getValue(),
                    passwordField.getValue(),
                    emailField.getValue(),
                    fullNameField.getValue()
            );
            
            Notification notification = Notification.show(
                    "Conta criada com sucesso! Faça login para continuar.",
                    5000,
                    Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            
        } catch (Exception e) {
            Notification notification = Notification.show(
                    "Erro ao criar conta: " + e.getMessage(),
                    5000,
                    Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private boolean validateForm() {
        if (usernameField.isEmpty() || fullNameField.isEmpty() || 
            emailField.isEmpty() || passwordField.isEmpty() || confirmPasswordField.isEmpty()) {
            showError("Todos os campos são obrigatórios");
            return false;
        }
        
        if (usernameField.getValue().length() < 3) {
            showError("Usuário deve ter no mínimo 3 caracteres");
            return false;
        }
        
        if (passwordField.getValue().length() < 6) {
            showError("Senha deve ter no mínimo 6 caracteres");
            return false;
        }
        
        if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            showError("As senhas não coincidem");
            return false;
        }
        
        if (emailField.isInvalid()) {
            showError("Digite um e-mail válido");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        Notification notification = Notification.show(
                message,
                3000,
                Notification.Position.TOP_CENTER
        );
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
