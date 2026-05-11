package com.hooney.lab.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         ⚙️ Config Service Application (Config Server)           ║
 * ║                                                                  ║
 * ║  [애플리케이션 역할]                                               ║
 * ║  1. MSA 서비스들의 환경 설정을 중앙에서 제공                        ║
 * ║  2. @EnableConfigServer를 통한 설정 서버 기능 활성화              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
