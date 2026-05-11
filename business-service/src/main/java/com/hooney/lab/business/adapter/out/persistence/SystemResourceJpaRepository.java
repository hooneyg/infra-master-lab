package com.hooney.lab.business.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         📊 SystemResourceJpaRepository (Spring Data JPA)        ║
 * ║                                                                  ║
 * ║  [레포지토리 역할]                                              ║
 * ║  1. DB와의 실질적인 CRUD 작업 수행 (JPA 추상화)                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public interface SystemResourceJpaRepository extends JpaRepository<SystemResourceJpaEntity, Long> {
}
