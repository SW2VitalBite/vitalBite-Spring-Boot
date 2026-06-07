package com.vitalbite.documental.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VitalBite - Microservicio Documental")
                        .description(
                                "API REST para gestión documental, " +
                                        "generación de PDFs, auditoría y blockchain. " +
                                        "Microservicio Spring Boot en Google Cloud."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo VitalBite")
                                .email("vitalbite@equipo.com")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082/api/v1")
                                .description("Servidor local de desarrollo")
                ));
    }
}