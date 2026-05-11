package com.hooney.lab.business.adapter.out.persistence;

import com.hooney.lab.business.application.port.out.SaveSystemResourcePort;
import com.hooney.lab.business.domain.model.SystemResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🛡️ SystemResourcePersistenceAdapter (Persistence)       ║
 * ║                                                                  ║
 * ║  [어댑터 역할]                                                  ║
 * ║  1. SaveSystemResourcePort(Output Port)의 구체적인 구현체         ║
 * ║  2. 도메인 모델을 JPA 엔티티로 변환하여 DB에 저장 후 다시 변환     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@Component
@RequiredArgsConstructor
public class SystemResourcePersistenceAdapter implements SaveSystemResourcePort {

    private final SystemResourceJpaRepository jpaRepository;

    @Override
    public SystemResource saveResource(SystemResource resource) {
        // 1. 도메인 -> JPA 엔티티 변환
        SystemResourceJpaEntity jpaEntity = SystemResourceJpaEntity.fromDomain(resource);
        
        // 2. 실질적인 DB 저장 수행
        SystemResourceJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        
        // 3. JPA 엔티티 -> 도메인 변환하여 반환
        return savedEntity.toDomain();
    }
}
