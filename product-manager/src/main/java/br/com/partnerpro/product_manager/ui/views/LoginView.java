package br.com.partnerpro.product_manager.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | Product Manager")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    
    private final LoginForm loginForm = new LoginForm();
    
    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        
        H1 title = new H1("Product Manager");
        title.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("margin-bottom", "var(--lumo-space-m)");
        
        RouterLink registerLink = new RouterLink("Criar nova conta", RegisterView.class);
        registerLink.getStyle()
                .set("margin-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-s)");
        
        VerticalLayout loginLayout = new VerticalLayout(
                title,
                loginForm,
                registerLink
        );
        
        loginLayout.setAlignItems(Alignment.CENTER);
        loginLayout.getStyle()
                .set("padding", "var(--lumo-space-l)")
                .set("background-color", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-m)");
        
        add(loginLayout);
    }
    
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
