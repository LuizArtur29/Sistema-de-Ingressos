package com.vendaingressos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.PathItem;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Venda de Ingressos")
                        .version("1.0")
                        .description("Documentação do sistema de eventos e ingressos."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomizer rolesOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperationsMap().forEach((method, operation) -> {
                    if (isPublic(path, method)) {
                        operation.setSecurity(List.of());
                        return;
                    }

                    operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                    List<String> roles = resolveRoles(path, method);
                    if (!roles.isEmpty()) {
                        operation.addExtension("x-roles", roles);
                    }
                });
            });
        };
    }

    private boolean isPublic(String path, PathItem.HttpMethod method) {
        if (path.startsWith("/api/auth")) {
            return true;
        }
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return true;
        }
        if (path.startsWith("/api/eventos") && method == PathItem.HttpMethod.GET) {
            return true;
        }
        return path.equals("/api/usuarios") && method == PathItem.HttpMethod.POST;
    }

    private List<String> resolveRoles(String path, PathItem.HttpMethod method) {
        if (path.startsWith("/api/eventos")) {
            if (method == PathItem.HttpMethod.POST || method == PathItem.HttpMethod.PUT || method == PathItem.HttpMethod.DELETE) {
                return List.of("ROLE_ADMIN");
            }
        }
        if (path.startsWith("/api/sessoes-evento")
                || path.startsWith("/api/tipos-ingresso")
                || path.startsWith("/api/administradores")
                || path.startsWith("/ingressos")) {
            return List.of("ROLE_ADMIN");
        }
        if (path.startsWith("/api/usuarios")) {
            if (path.equals("/api/usuarios/me") || path.equals("/api/usuarios/meus-ingressos")) {
                return List.of("ROLE_USER", "ROLE_ADMIN");
            }
            if (method != PathItem.HttpMethod.POST) {
                return List.of("ROLE_ADMIN");
            }
        }
        if (path.startsWith("/api/compras") || path.startsWith("/api/transferencias")) {
            return List.of("ROLE_USER", "ROLE_ADMIN");
        }
        return List.of();
    }
}