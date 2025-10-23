package br.com.partnerpro.product_manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Manager API")
                        .version("1.0.0")
                        .description("API REST completa para gerenciamento de produtos com Clean Architecture, " +
                                "incluindo autenticação, cache, monitoramento e integração com IA")
                        .contact(new Contact()
                                .name("PARTNER Pro")
                                .email("contato@partnerpro.com.br")
                                .url("https://partnerpro.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.partnerpro.com.br")
                                .description("Servidor de Produção")
                ));
    }
}
