package com.hooney.lab.business.payment.application.port.in;

import java.math.BigDecimal;

/**
 * 결제 처리를 요청하기 위한 커맨드 객체 (Command DTO)
 * 
 * [Architecture Note]
 * Web 계층(Controller)에서 사용하는 Request DTO를 도메인 계층으로 그대로 넘기지 않고,
 * Application 계층 전용 Command 객체로 변환하여 사용합니다. (결합도 최소화)
 * 
 * @param orderId 결제할 주문의 고유 식별자
 * @param amount 결제 요청 금액
 */
public record PaymentCommand(String orderId, BigDecimal amount) {
    
    /**
     * 컴팩트 생성자 (Compact Constructor)
     * 객체가 생성되는 시점에 방어적 로직(Defensive Validation)을 수행하여,
     * 비정상적인 데이터가 도메인 내부로 침투하는 것을 원천 차단합니다.
     */
    public PaymentCommand {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
    }
}
