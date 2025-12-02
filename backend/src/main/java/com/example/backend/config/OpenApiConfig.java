package com.example.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration.
 *
 * - Defines API metadata (title, version, description, contact, license)
 * - Adds a global Security Requirement referencing 'bearerAuth'
 * - Declares the SecurityScheme 'bearerAuth' (HTTP Bearer JWT)
 *
 * Swagger UI will be available at `/swagger-ui.html` and `/swagger-ui/index.html`.
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(
                title = "PandaGamers API",
                version = "v1",
                description = "API REST para la tienda PandaGamers. Endpoints para autenticación, productos, carrito y pagos simulados.",
                contact = @Contact(name = "Equipo PandaGamers", email = "contacto@pandagamers.example"),
                license = @License(name = "MIT")
        ),
        security = {@SecurityRequirement(name = "bearerAuth")}
)
public class OpenApiConfig {
    // Annotation-based configuration; no additional beans are required for basic setup.
}
