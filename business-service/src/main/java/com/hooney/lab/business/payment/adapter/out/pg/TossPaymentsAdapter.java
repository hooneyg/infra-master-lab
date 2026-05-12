package com.hooney.lab.business.payment.adapter.out.pg;

import com.hooney.lab.business.payment.application.port.out.PaymentGatewayPort;
import com.hooney.lab.business.payment.domain.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Toss Payments 연동을 담당하는 아웃바운드 어댑터 (Outbound Adapter)
 * 
 * [Architecture Note]
 * PaymentGatewayPort 인터페이스를 실제 Toss API 기술(REST API 호출 등)로 구현하는 클래스입니다.
 * 비즈니스의 요구사항이 변경되어 결제 대행사를 Toss에서 Stripe로 갈아타야 한다면, 
 * 오직 StripeAdapter를 새로 만들고 Bean 주입만 변경하면 됩니다. 코어 도메인 로직은 0% 수정됩니다.
 */
@Component
public class TossPaymentsAdapter implements PaymentGatewayPort {

    /**
     * PG사 API로 승인 요청을 보내는 것을 시뮬레이션합니다.
     */
    @Override
    public boolean requestPaymentApproval(Payment payment) {
        System.out.println("==================================================");
        System.out.println("🚀 [Toss Payments Adapter] 결제 승인 API 호출 시뮬레이션");
        System.out.println("  - 주문 식별자: " + payment.getOrderId());
        System.out.println("  - 요청 금액: " + payment.getAmount() + " KRW");
        
        // 가짜(Mock) 비즈니스 룰: 결제 금액이 100,000원을 초과하면 한도 초과로 간주하여 승인 거절
        BigDecimal limit = new BigDecimal("100000");
        if (payment.getAmount().compareTo(limit) > 0) {
            System.out.println("❌ [Toss Payments Adapter] 승인 거절 (사유: 10만원 한도 초과)");
            System.out.println("==================================================");
            return false;
        }

        System.out.println("✅ [Toss Payments Adapter] 승인 완료 (200 OK)");
        System.out.println("==================================================");
        return true; // 승인 성공
    }
}
