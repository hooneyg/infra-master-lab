package com.hooney.lab.business.adapter.in.web;

import com.hooney.lab.business.application.port.in.RegisterSystemResourceUseCase;
import com.hooney.lab.business.domain.model.SystemResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         🌐 SystemResourceController (Web Adapter)               ║
 * ║                                                                  ║
 * ║  [어댑터 역할]                                                  ║
 * ║  1. 외부 REST API 요청을 받아 애플리케이션 유스케이스를 호출        ║
 * ║  2. HTTP 요청 데이터를 RegisterCommand로 변환하여 전달            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class SystemResourceController {

    private final RegisterSystemResourceUseCase registerUseCase;

    /**
     * 새로운 인프라 자원을 등록하는 API
     * @param request 요청 데이터
     * @return 등록 완료된 자원 정보
     */
    @PostMapping
    public ResponseEntity<SystemResource> register(@RequestBody RegisterRequest request) {
        // 1. 요청 데이터를 유스케이스 커맨드로 변환
        RegisterSystemResourceUseCase.RegisterCommand command = 
                new RegisterSystemResourceUseCase.RegisterCommand(request.name(), request.type());

        // 2. 유스케이스 실행
        SystemResource registeredResource = registerUseCase.registerResource(command);

        // 3. 결과 반환
        return ResponseEntity.ok(registeredResource);
    }

    /**
     * API 전용 요청 DTO (Immutable)
     */
    public record RegisterRequest(
            String name,
            SystemResource.ResourceType type
    ) {}
}
