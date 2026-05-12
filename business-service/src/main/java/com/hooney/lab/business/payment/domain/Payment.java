package com.hooney.lab.business.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 비즈니스의 핵심을 담당하는 도메인 엔티티 (Domain Entity)
 * 
 * [Hexagonal Architecture 핵심 규칙 준수]
 * 1. 이 클래스는 JPA Annotation (@Entity, @Table 등)을 절대로 가지지 않습니다.
 * 2. 오직 결제에 관련된 순수 비즈니스 규칙과 상태 변경만 캡슐화합니다.
 * 3. 외부 환경(DB 종류, PG사 API 스펙)의 변경이 이 도메인 객체에 영향을 주지 못하도록 완벽히 격리됩니다.
 */
public class Payment {
    
    private final String paymentId;       // 결제 고유 식별자 (UUID)
    private final String orderId;         // 연관된 주문 ID
    private final BigDecimal amount;      // 결제 금액
    private PaymentStatus status;         // 현재 결제 상태 (가변)
    private final LocalDateTime createdAt;// 결제 생성 일시

    /**
     * 결제 객체 생성자
     * 객체 생성 시점에 제약조건(Validation)을 검사하여 불변성 및 데이터 무결성을 보장합니다.
     * 
     * @param paymentId 결제 고유 식별자
     * @param orderId 주문 번호
     * @param amount 결제 금액
     */
    public Payment(String paymentId, String orderId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.CREATED; // 초기 상태는 무조건 '생성됨(대기)'
        this.createdAt = LocalDateTime.now();
    }

    /**
     * [도메인 비즈니스 로직] 결제 승인 처리
     * 외부 PG사 연동이 성공했을 때 호출되며, 도메인의 상태를 안전하게 변경합니다.
     */
    public void approve() {
        if (this.status != PaymentStatus.CREATED) {
            throw new IllegalStateException("대기 상태(CREATED)인 결제만 승인할 수 있습니다.");
        }
        this.status = PaymentStatus.APPROVED;
    }

    /**
     * [도메인 비즈니스 로직] 결제 실패 처리
     * PG사 한도 초과, 잔액 부족 등으로 결제가 거절되었을 때 호출됩니다.
     */
    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public String getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
