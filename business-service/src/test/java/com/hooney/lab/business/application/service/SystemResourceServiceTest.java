package com.hooney.lab.business.application.service;

import com.hooney.lab.business.application.port.in.RegisterSystemResourceUseCase;
import com.hooney.lab.business.application.port.out.SaveSystemResourcePort;
import com.hooney.lab.business.domain.model.SystemResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🧪 SystemResourceServiceTest (Hexagonal Proof)          ║
 * ║                                                                  ║
 * ║  [테스트 목적]                                                   ║
 * ║  1. 외부 인프라(JPA) 없이도 비즈니스 로직이 완벽히 작동함을 증명      ║
 * ║  2. 어댑터 교체(Stubbing)를 통한 아키텍처 유연성 검증               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
class SystemResourceServiceTest {

    @Test
    @DisplayName("인프라(DB) 없이도 시스템 자원 등록 유스케이스는 정상 작동해야 한다.")
    void registerResourceTest() {
        // [Given] 1. 메모리 기반의 가짜 어댑터(Stub) 생성 (Adapter Swapping 시연)
        SaveSystemResourcePort memoryAdapter = new SaveSystemResourcePort() {
            private final Map<Long, SystemResource> store = new HashMap<>();
            private Long sequence = 1L;

            @Override
            public SystemResource saveResource(SystemResource resource) {
                SystemResource saved = SystemResource.builder()
                        .id(sequence++)
                        .name(resource.getName())
                        .type(resource.getType())
                        .status(resource.getStatus())
                        .createdAt(resource.getCreatedAt())
                        .build();
                store.put(saved.getId(), saved);
                return saved;
            }
        };

        // [Given] 2. 실제 서비스에 가짜 어댑터 주입
        SystemResourceService service = new SystemResourceService(memoryAdapter);
        RegisterSystemResourceUseCase.RegisterCommand command = 
                new RegisterSystemResourceUseCase.RegisterCommand("Test-Server", SystemResource.ResourceType.SERVER);

        // [When] 3. 유스케이스 실행
        SystemResource result = service.registerResource(command);

        // [Then] 4. 검증 (DB 연결 없이도 비즈니스 규칙이 완벽히 수행됨)
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Test-Server");
        assertThat(result.getStatus()).isEqualTo(SystemResource.ResourceStatus.ACTIVE);
        System.out.println("✅ 아키텍처 검증 완료: 외부 인프라 의존성 없이 비즈니스 로직 실행 성공!");
    }
}
