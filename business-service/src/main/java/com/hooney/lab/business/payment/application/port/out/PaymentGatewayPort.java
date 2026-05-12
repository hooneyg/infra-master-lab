package com.hooney.lab.business.payment.application.port.out;

import com.hooney.lab.business.payment.domain.Payment;

/**
 * 외부 결제 대행사(PG사) 연동을 위한 아웃바운드 포트 (Outbound Port)
 * 
 * [의존성 역전(DIP)의 핵심]
 * 도메인(Application Service) 계층은 오직 이 '추상화된 인터페이스'만 바라봅니다.
 * 실제 Toss Payments, Stripe, NHN KCP 등 어떤 PG사가 연동될지는 도메인 로직이 알 필요가 없습니다.
 */
public interface PaymentGatewayPort {
    
    /**
     * PG사로 실제 결제 승인 요청을 전송합니다.
     * 
     * @param payment 결제 도메인 엔티티
     * @return PG사 승인 성공 여부 (true: 결제 승인, false: 한도 초과 등 거절)
     */
    boolean requestPaymentApproval(Payment payment);
}
