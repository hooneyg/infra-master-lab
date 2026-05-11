package com.hooney.lab.business.application.port.out;

import com.hooney.lab.business.domain.model.SystemResource;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         📤 SaveSystemResourcePort (Output Port)                 ║
 * ║                                                                  ║
 * ║  [인터페이스 역할]                                               ║
 * ║  1. 도메인 계층이 외부 인프라(DB)에 데이터를 저장할 때 사용하는 계약║
 * ║  2. 의존성 역전(DIP)을 통해 인프라의 구체적인 기술을 은닉          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public interface SaveSystemResourcePort {

    /**
     * 시스템 자원을 저장소에 저장합니다.
     * @param resource 저장할 도메인 모델
     * @return 저장된 도메인 모델
     */
    SystemResource saveResource(SystemResource resource);
}
