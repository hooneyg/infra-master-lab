package com.hooney.lab.business.adapter.out.persistence;

import com.hooney.lab.business.domain.model.SystemResource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🗄️ SystemResourceJpaEntity (JPA Entity)                 ║
 * ║                                                                  ║
 * ║  [엔티티 역할]                                                 ║
 * ║  1. SYSTEM_RESOURCES 테이블과 매핑되는 영속성 객체                 ║
 * ║  2. 도메인 모델과 물리 저장 구조를 분리하기 위한 데이터 컨테이너    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@Entity
@Table(name = "SYSTEM_RESOURCES")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SystemResourceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemResource.ResourceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemResource.ResourceStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 도메인 모델로부터 JPA 엔티티를 생성하는 팩토리 메서드
     */
    public static SystemResourceJpaEntity fromDomain(SystemResource domain) {
        return SystemResourceJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .type(domain.getType())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    /**
     * JPA 엔티티를 도메인 모델로 변환하는 메서드
     */
    public SystemResource toDomain() {
        return SystemResource.builder()
                .id(this.id)
                .name(this.name)
                .type(this.type)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }
}
