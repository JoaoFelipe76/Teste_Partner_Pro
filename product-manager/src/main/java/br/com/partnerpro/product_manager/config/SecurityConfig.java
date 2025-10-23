package br.com.partnerpro.product_manager.config;

import br.com.partnerpro.product_manager.ui.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(
                        new AntPathRequestMatcher("/register"),
                        new AntPathRequestMatcher("/actuator/**"),
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/swagger-ui/**"),
                        new AntPathRequestMatcher("/swagger-ui.html"),
                        new AntPathRequestMatcher("/v3/api-docs/**"),
                        new AntPathRequestMatcher("/api-docs/**"),
                        new AntPathRequestMatcher("/api/**")
                ).permitAll()
        );
        
        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}
