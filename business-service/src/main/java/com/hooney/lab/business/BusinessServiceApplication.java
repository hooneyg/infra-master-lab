package com.hooney.lab.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🏗️ Business Service Application (Hexagonal)             ║
 * ║                                                                  ║
 * ║  [애플리케이션 역할]                                               ║
 * ║  1. 헥사고날 아키텍처 기반의 핵심 비즈니스 로직 실행                ║
 * ║  2. 인프라 자원(SystemResource) 관리 유스케이스 제공              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class BusinessServiceApplication {

    /**
     * 메인 엔트리 포인트
     * @param args 실행 인자
     */
    public static void main(String[] args) {
        // Spring Boot 애플리케이션 구동
        SpringApplication.run(BusinessServiceApplication.class, args);
    }
}
