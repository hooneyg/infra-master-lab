package com.hooney.lab.business.application.service;

import com.hooney.lab.business.application.port.in.RegisterSystemResourceUseCase;
import com.hooney.lab.business.application.port.out.SaveSystemResourcePort;
import com.hooney.lab.business.domain.model.SystemResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         ⚙️ SystemResourceService (UseCase Implement)             ║
 * ║                                                                  ║
 * ║  [서비스 역할]                                                   ║
 * ║  1. 자원 등록 유스케이스의 구체적인 비즈니스 흐름 제어              ║
 * ║  2. 도메인 모델을 생성하고 아웃풋 포트를 통해 영속화 수행          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SystemResourceService implements RegisterSystemResourceUseCase {

    private final SaveSystemResourcePort saveSystemResourcePort;

    @Override
    public SystemResource registerResource(RegisterCommand command) {
        // 1. 도메인 모델 생성 (Initial 상태: ACTIVE)
        SystemResource resource = SystemResource.builder()
                .name(command.name())
                .type(command.type())
                .status(SystemResource.ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // 2. 아웃풋 포트를 통해 저장 실행 (구체적인 DB 기술은 서비스가 알 필요 없음)
        return saveSystemResourcePort.saveResource(resource);
    }
}
