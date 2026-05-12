package com.hooney.lab.business.payment.application.service;

import com.hooney.lab.business.payment.application.port.in.PaymentCommand;
import com.hooney.lab.business.payment.application.port.out.PaymentGatewayPort;
import com.hooney.lab.business.payment.domain.Payment;
import com.hooney.lab.business.payment.domain.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 결제 서비스 단위 테스트 (Unit Test)
 * 
 * [Hexagonal Architecture 의 강력한 우수성 증명]
 * 외부 DB, 복잡한 스프링 컨텍스트(@SpringBootTest), 무거운 네트워크 계층 로딩 없이,
 * 오직 순수한 자바 비즈니스 로직(결제 상태 변경 흐름)만을 "0.01초 만에" 번개처럼 빠르게 검증할 수 있습니다.
 */
class PaymentServiceTest {

    @Test
    @DisplayName("결제 승인 성공 시나리오: PG사가 승인(true)을 반환하면 결제 상태가 APPROVED가 되어야 한다.")
    void processPayment_Success() {
        // given: 무조건 승인(true)을 반환하는 가짜(Mock) PG 어댑터를 람다식으로 주입
        PaymentGatewayPort mockPgPort = payment -> true;
        PaymentService paymentService = new PaymentService(mockPgPort);
        
        // 5만원 결제 요청 (Command)
        PaymentCommand command = new PaymentCommand("ORDER-123", new BigDecimal("50000"));

        // when: 애플리케이션 서비스 유스케이스 호출
        Payment result = paymentService.processPayment(command);

        // then: 생성된 도메인의 상태가 정확히 승인됨(APPROVED)으로 전이되었는지 검증
        assertNotNull(result.getPaymentId(), "결제 식별자가 생성되어야 합니다.");
        assertEquals("ORDER-123", result.getOrderId(), "주문 번호가 일치해야 합니다.");
        assertEquals(PaymentStatus.APPROVED, result.getStatus(), "결제 상태는 APPROVED여야 합니다.");
    }

    @Test
    @DisplayName("결제 승인 실패 시나리오: PG사가 거절(false)을 반환하면 예외가 발생하고 결제 상태가 FAILED가 되어야 한다.")
    void processPayment_Fail() {
        // given: 무조건 거절(false)을 반환하는 가짜(Mock) PG 어댑터 주입
        PaymentGatewayPort mockPgPort = payment -> false;
        PaymentService paymentService = new PaymentService(mockPgPort);

        // 20만원 결제 요청 (Command)
        PaymentCommand command = new PaymentCommand("ORDER-999", new BigDecimal("200000"));

        // when & then: 결제 처리가 예외를 던지는지 검증
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(command);
        });

        // 에러 메시지가 정확히 노출되는지 확인
        assertEquals("PG사 결제 승인에 실패했습니다.", exception.getMessage());
    }
}
