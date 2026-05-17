package com.hooney.lab.business.adapter.in.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         ⚙️ OpenApiConfig (Swagger UI Configuration)             ║
 * ║                                                                  ║
 * ║  [설정 역할]                                                     ║
 * ║  1. 로컬 환경에서 Swagger UI API 명세 시각화 구성                  ║
 * ║  2. 헥사고날 아키텍처 API 설명서 메타데이터 제공                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Infra Master Lab - Business Service API")
                        .version("1.0.0")
                        .description("Cloud Native Infrastructure와 Zero Trust Edge 아키텍처를 검증하기 위한 헥사고날 비즈니스 서비스 API 명세서입니다.")
                        .contact(new Contact()
                                .name("Hooney")
                                .url("https://github.com/hooneyg")
                        ));
    }
}
