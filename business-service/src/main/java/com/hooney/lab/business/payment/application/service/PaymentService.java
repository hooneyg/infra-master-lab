package com.hooney.lab.business.payment.application.service;

import com.hooney.lab.business.payment.application.port.in.PaymentCommand;
import com.hooney.lab.business.payment.application.port.in.ProcessPaymentUseCase;
import com.hooney.lab.business.payment.application.port.out.PaymentGatewayPort;
import com.hooney.lab.business.payment.domain.Payment;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 결제 유스케이스를 실제로 구현하는 애플리케이션 서비스 (Application Service)
 * 
 * 도메인 객체(Payment)의 생명주기를 관리하고, 외부 아웃바운드 포트(PaymentGatewayPort)를 조율합니다.
 * 주의: 순수 비즈니스 상태 변경(approve, fail 등)은 Service가 아니라 Payment 도메인 엔티티 내부에 위임합니다.
 */
@Service
public class PaymentService implements ProcessPaymentUseCase {

    // [핵심] 구체적인 클래스(TossAdapter)가 아닌 추상화된 Port 인터페이스에 의존합니다. (DIP)
    private final PaymentGatewayPort paymentGatewayPort;

    public PaymentService(PaymentGatewayPort paymentGatewayPort) {
        this.paymentGatewayPort = paymentGatewayPort;
    }

    @Override
    public Payment processPayment(PaymentCommand command) {
        // 1. 도메인 엔티티 생성 (이 시점에 결제 상태는 CREATED 로 강제 할당됨)
        // 실제 운영 환경에서는 별도의 ID 생성기(Snowflake 등)를 사용하거나 DB Auto Increment를 활용합니다.
        String newPaymentId = UUID.randomUUID().toString();
        Payment payment = new Payment(newPaymentId, command.orderId(), command.amount());

        // 2. 아웃바운드 포트를 통해 외부 PG사(Toss, Stripe 등)에 결제 승인 요청
        // 코어 로직은 누가 이 요청을 받아 처리하는지 모릅니다.
        boolean isApproved = paymentGatewayPort.requestPaymentApproval(payment);

        // 3. 외부 응답 결과에 따라 도메인 엔티티의 풍부한 비즈니스 로직(메서드) 호출
        if (isApproved) {
            payment.approve(); // 결제 상태를 APPROVED로 안전하게 전이
        } else {
            payment.fail();    // 결제 상태를 FAILED로 전이
            throw new IllegalStateException("PG사 결제 승인에 실패했습니다.");
        }

        // (실제 환경에서는 이 시점에 RepositoryPort를 통해 DB에 Transaction Commit을 수행합니다.)
        
        return payment;
    }
}
