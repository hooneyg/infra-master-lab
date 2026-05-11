package com.hooney.lab.business.test;

import com.hooney.lab.business.application.port.in.RegisterSystemResourceUseCase;
import com.hooney.lab.business.application.port.out.SaveSystemResourcePort;
import com.hooney.lab.business.domain.model.SystemResource;
import com.hooney.lab.business.application.service.SystemResourceService;

import java.time.LocalDateTime;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         ⚔️ PureArchitectureTest (No Framework Test)             ║
 * ║                                                                  ║
 * ║  [테스트 의도]                                                   ║
 * ║  1. Gradle이나 Spring 없이 오직 JDK만으로 비즈니스 로직을 검증함   ║
 * ║  2. 헥사고날 아키텍처의 인프라 독립성을 최종적으로 증명함          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class PureArchitectureTest {

    public static void main(String[] args) {
        System.out.println("🚀 [Arch-Test] 프레임워크 독립성 검증 시작...");

        // 1. 가짜 어댑터(Stub) 구현
        SaveSystemResourcePort stubAdapter = resource -> {
            System.out.println("📝 [Stub] 데이터 저장소에 기록됨: " + resource.getName());
            return resource; // 간단한 반환
        };

        // 2. 서비스 생성 및 주입
        SystemResourceService service = new SystemResourceService(stubAdapter);

        // 3. 유스케이스 실행
        RegisterSystemResourceUseCase.RegisterCommand command = 
                new RegisterSystemResourceUseCase.RegisterCommand("Infra-Node-01", SystemResource.ResourceType.SERVER);
        
        try {
            SystemResource result = service.registerResource(command);
            
            // 4. 결과 검증
            if (result.getName().equals("Infra-Node-01")) {
                System.out.println("✅ [Success] 비즈니스 로직이 외부 도구 없이 완벽하게 실행되었습니다!");
                System.out.println("🏛️ 이것이 바로 헥사고날 아키텍처의 힘입니다.");
            }
        } catch (Exception e) {
            System.err.println("❌ [Fail] 테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
