package br.com.partnerpro.product_manager.ui;

import br.com.partnerpro.product_manager.ui.views.AIChatView;
import br.com.partnerpro.product_manager.ui.views.DashboardView;
import br.com.partnerpro.product_manager.ui.views.ProductsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {
    
    private final AuthenticationContext authenticationContext;
    
    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        createHeader();
        createDrawer();
    }
    
    private void createHeader() {
        H1 logo = new H1("Product Manager");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );
        
        Button logoutButton = new Button("Sair", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClickListener(e -> authenticationContext.logout());
        
        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                logo,
                logoutButton
        );
        
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(logo);
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );
        
        addToNavbar(header);
    }
    
    private void createDrawer() {
        SideNav nav = new SideNav();
        
        nav.addItem(new SideNavItem("Home", ProductsView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Assistente AI", AIChatView.class, VaadinIcon.CHAT.create()));
        
        addToDrawer(nav);
    }
}
