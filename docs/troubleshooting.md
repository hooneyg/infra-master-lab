# 🛠️ Troubleshooting Guide

본 문서는 `infra-master-lab` 개발 및 인프라 구축 과정에서 발생했던 주요 에러와 그 해결 과정(Troubleshooting)을 기록한 문서입니다. 이를 통해 동일한 문제가 발생했을 때 디버깅 시간을 단축하고, 근본적인 원인을 이해할 수 있도록 돕습니다.

---

## 1. Cloudflare Tunnel(Zero Trust) 연결 실패 오류

### 🚨 현상 (Symptom)
- `docker-compose up` 실행 시, Cloudflared 컨테이너에서 `Failed to authenticate with Cloudflare` 혹은 `Invalid token` 에러 발생.
- 외부 트래픽이 엣지 프록시로 도달하지 않고 HTTP 502 Bad Gateway 응답 반환.

### 🔍 원인 분석 (Root Cause)
- Cloudflare Tunnel 인증을 위한 Tunnel Token이 환경 변수로 제대로 전달되지 않거나 유효기간이 만료됨.
- `edge-proxy` 내부 네트워크와 Cloudflared 터널 데몬 간의 DNS 리졸빙(resolving) 실패.

### 💡 해결 과정 (Solution)
1. **토큰 재발급 및 환경변수 주입:**
   Cloudflare Zero Trust 대시보드에서 해당 터널의 Token을 재발급하여 `.env` 파일에 `CLOUDFLARE_TUNNEL_TOKEN`으로 저장.
2. **docker-compose.yml 수정:**
   ```yaml
   cloudflared:
     image: cloudflare/cloudflared:latest
     command: tunnel run
     environment:
       - TUNNEL_TOKEN=${CLOUDFLARE_TUNNEL_TOKEN}
   ```
3. **네트워크 격리 점검:** Nginx와 Cloudflared가 동일한 `infra-network` 상에 존재하는지 확인.

---

## 2. Kubernetes Pod CrashLoopBackOff (Config Server 의존성 문제)

### 🚨 현상 (Symptom)
- K8S에 `business-service` 배포 후, Pod 상태가 `CrashLoopBackOff`로 반복 종료됨.
- Pod 로그 확인 결과: `Could not resolve placeholder 'spring.datasource.url' in value "${spring.datasource.url}"`

### 🔍 원인 분석 (Root Cause)
- `business-service`가 구동될 때 `config-service`로부터 설정을 가져와야 하나, Config Server Pod가 완전히 준비되기 전에 비즈니스 서비스가 시작됨. (레이스 컨디션)
- Spring Boot 3.x/4.x 의 `spring.config.import` 설정에서 Config Server 통신 실패 시 기본적으로 애플리케이션 기동이 실패하도록 설정됨.

### 💡 해결 과정 (Solution)
1. **Init Containers 활용:** K8S 배포 매니페스트(`k8s-manifests/business-service-deployment.yml`)에 `initContainers`를 추가하여 `config-service`의 8888 포트가 열릴 때까지 대기.
2. **Readiness Probe 추가:** Config Server에 Health Check를 적용.
3. **재시도 로직 추가:** `application.yml`에 Config Server 연결 재시도 로직(Retry Policy) 추가.
   ```yaml
   spring:
     config:
       import: "optional:configserver:http://config-service:8888"
     cloud:
       config:
         fail-fast: true
         retry:
           max-attempts: 6
           multiplier: 1.5
   ```

---

## 3. Hexagonal Architecture 내부 의존성 누수 (Dependency Leak)

### 🚨 현상 (Symptom)
- `Domain` 계층의 엔티티 클래스에서 `javax.persistence.*` (JPA) 어노테이션 사용으로 인한 강결합 발생.
- 기술 스택 변경 시 도메인 로직이 훼손되는 아키텍처 규칙 위반.

### 🔍 원인 분석 (Root Cause)
- 개발 편의성을 위해 DB Entity와 Domain Model을 분리하지 않고 하나의 클래스로 혼용.

### 💡 해결 과정 (Solution)
1. **엄격한 분리:** 순수 POJO 형태의 `Domain Model`과 JPA 어노테이션이 붙은 `JpaEntity`를 분리.
2. **Mapper 도입:** `Adapter` 계층에서 도메인 모델과 영속성 객체를 변환하는 Mapper 클래스 구현.
3. **ArchUnit 테스트 도입:** 도메인 계층이 `framework`나 `adapter` 패키지를 참조하지 못하도록 강제하는 아키텍처 검증 테스트(Architecture Test) 작성.
