package com.hooney.lab.business.payment.domain;

/**
 * 결제 상태를 나타내는 열거형 클래스 (Enum)
 * 
 * [Architecture Note]
 * 도메인 계층(Domain Layer)에 위치하며, 
 * 인프라스트럭처나 외부 프레임워크(Spring 등)에 전혀 의존하지 않는 순수 자바 객체(POJO)입니다.
 */
public enum PaymentStatus {
    /** 결제 생성됨 (처리 전 대기 상태) */
    CREATED,
    
    /** 결제 승인 완료 (PG사 승인 성공) */
    APPROVED,
    
    /** 결제 실패 (PG사 한도 초과, 잔액 부족 등) */
    FAILED,
    
    /** 결제 취소 (사용자 환불 또는 승인 취소) */
    CANCELED
}
