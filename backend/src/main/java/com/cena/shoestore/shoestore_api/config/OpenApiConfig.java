package com.cena.shoestore.shoestore_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ShoeStore E-Commerce API",
                version = "1.0.0",
                description = "Comprehensive REST API for ShoeStore e-commerce platform with advanced Oracle database security features. " +
                        "This API implements 11 mandatory security requirements including symmetric/asymmetric/hybrid encryption, " +
                        "VPD (Virtual Private Database), OLS (Oracle Label Security), RBAC, DAC, standard auditing, and FGA (Fine-Grained Auditing). " +
                        "All responses are wrapped in a standard ApiResponse format with status, code, message, and data fields.",
                contact = @Contact(
                        name = "ShoeStore Development Team",
                        email = "support@shoestore.com"
                )
        ),
        servers = {
                @Server(
                        description = "Local Development Server",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production Server",
                        url = "https://api.shoestore.com"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT Bearer token authentication. After login or registration, use the returned token in the Authorization header as 'Bearer {token}'"
)
public class OpenApiConfig {
}
