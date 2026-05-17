package com.hooney.lab.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🌐 Gateway Service Application                          ║
 * ║                                                                  ║
 * ║  [애플리케이션 역할]                                               ║
 * ║  1. MSA 생태계의 단일 진입점 역할 수행                              ║
 * ║  2. 트래픽 라우팅 및 공통 필터링 관리                               ║
 * ║                                                                  ║
 * ║  [주의] Gateway는 WebFlux(리액티브) 기반이며 DB를 사용하지         ║
 * ║  않으므로 DataSource/JPA 자동 설정을 명시적으로 제외합니다.        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
    "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
