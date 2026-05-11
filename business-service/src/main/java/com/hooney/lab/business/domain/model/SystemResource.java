package com.hooney.lab.business.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🧬 SystemResource (Pure Domain Model)                   ║
 * ║                                                                  ║
 * ║  [도메인 역할]                                                   ║
 * ║  1. 인프라 자원(서버, DB 등)의 핵심 데이터 상태 관리               ║
 * ║  2. 외부 기술(JPA 등)에 의존하지 않는 순수 비즈니스 엔티티         ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@Getter
@Builder
@AllArgsConstructor
public class SystemResource {

    // 자원 식별자
    private final Long id;

    // 자원 이름 (예: Web-Server-01)
    private final String name;

    // 자원 유형 (SERVER, DATABASE, NETWORK)
    private final ResourceType type;

    // 자원 상태 (ACTIVE, INACTIVE, MAINTENANCE)
    private ResourceStatus status;

    // 등록 일시
    private final LocalDateTime createdAt;

    /**
     * 자원의 상태를 변경하는 비즈니스 로직
     * @param newStatus 새로운 상태
     */
    public void updateStatus(ResourceStatus newStatus) {
        // 비즈니스 규칙: 이미 점검 중인 자원은 활성화할 수 없음 (예시)
        if (this.status == ResourceStatus.MAINTENANCE && newStatus == ResourceStatus.ACTIVE) {
            throw new IllegalStateException("점검 중인 자원은 즉시 활성화할 수 없습니다.");
        }
        this.status = newStatus;
    }

    public enum ResourceType {
        SERVER, DATABASE, NETWORK
    }

    public enum ResourceStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }
}
