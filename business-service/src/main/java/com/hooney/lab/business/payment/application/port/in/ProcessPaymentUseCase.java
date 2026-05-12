package com.hooney.lab.business.payment.application.port.in;

import com.hooney.lab.business.payment.domain.Payment;

/**
 * 결제 처리 유스케이스 인터페이스 (Inbound Port)
 * 
 * 외부(Web Adapter, Controller)에서 애플리케이션 코어(Domain 로직)를 호출할 때 사용하는 진입점입니다.
 * 인터페이스를 두어 외부 계층이 코어의 내부 구현체(Service)를 직접 알지 못하게 차단합니다.
 */
public interface ProcessPaymentUseCase {
    
    /**
     * 외부망에서 들어온 결제 요청(PaymentCommand)을 받아
     * 도메인 로직을 수행하고 최종 결제 엔티티(Payment)를 반환합니다.
     * 
     * @param command 결제 승인에 필요한 주문 및 금액 정보
     * @return 처리가 완료된 결제 도메인 엔티티
     */
    Payment processPayment(PaymentCommand command);
}
