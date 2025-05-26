package com.talkhasam.artichat.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Arrays;

@OpenAPIDefinition(
        info = @Info(title = "Artichat API", version = "v1"),
        servers = {
                @Server(url = "/", description = "Server URL")
        }
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth))
                .security(Arrays.asList(securityRequirement));
    }

    @Bean
    public GroupedOpenApi apiGroup(OpenApiCustomizer longToStringCustomizer) {
        return GroupedOpenApi.builder()
                .group("service-api-group")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(longToStringCustomizer)
                .build();
    }

    @Bean
    public OpenApiCustomizer longToStringCustomizer() {
        return openApi -> {
            // Components 내 모든 integer/int64 스키마를 string으로 변경
            if (openApi.getComponents() != null) {
                openApi.getComponents().getSchemas().values().forEach(schema -> {
                    if ("integer".equals(schema.getType()) && "int64".equals(schema.getFormat())) {
                        schema.setType("string");
                    }
                });
            }
            // 각 경로의 파라미터도 변경
            openApi.getPaths().values().forEach(path ->
                    path.readOperations().forEach(op ->
                            op.getParameters().forEach(p -> {
                                Schema<?> s = p.getSchema();
                                if ("integer".equals(s.getType()) && "int64".equals(s.getFormat())) {
                                    s.setType("string");
                                }
                            })
                    )
            );
        };
    }
}
