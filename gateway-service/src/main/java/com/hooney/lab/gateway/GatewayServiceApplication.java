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
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
