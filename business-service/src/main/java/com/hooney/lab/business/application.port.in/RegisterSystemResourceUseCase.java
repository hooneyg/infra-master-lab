package com.hooney.lab.business.application.port.in;

import com.hooney.lab.business.domain.model.SystemResource;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         📥 RegisterSystemResourceUseCase (Input Port)           ║
 * ║                                                                  ║
 * ║  [인터페이스 역할]                                               ║
 * ║  1. 외부(Web 등)로부터 들어오는 시스템 자원 등록 요청을 정의       ║
 * ║  2. 비즈니스 유스케이스의 진입점 제공                             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public interface RegisterSystemResourceUseCase {

    /**
     * 새로운 시스템 자원을 등록합니다.
     * @param command 등록 명령 데이터
     * @return 등록된 자원 정보
     */
    SystemResource registerResource(RegisterCommand command);

    /**
     * 등록 요청을 위한 커맨드 객체 (불변 객체)
     */
    record RegisterCommand(
            String name,
            SystemResource.ResourceType type
    ) {}
}
