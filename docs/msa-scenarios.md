# 🛡️ MSA 및 Zero Trust 네트워크 트래픽 시나리오 (Traffic Scenarios)

이 문서는 `infra-master-lab`의 핵심인 **"트래픽이 외부망에서 어떻게 엣지를 통과하여 MSA 내부 깊숙한 도메인 로직까지 안전하게 도달하는가?"**를 증명하는 시나리오입니다.

모든 시나리오는 IDE(IntelliJ, VSCode)에서 `examples/scenarios.http` 파일을 통해 클릭 한 번으로 시뮬레이션해 볼 수 있습니다.

---

## 🎭 시나리오 1: Zero Trust 기반 엣지 프록싱 (Edge Proxying)

### [상황]
해커가 AWS EKS 클러스터 내부에 구동 중인 결제 마이크로서비스(`business-service`)에 직접 접근하여 악의적인 결제 요청을 보내려 합니다.

### [방어 및 라우팅 흐름]
1. **인바운드 원천 차단**: 서버의 모든 퍼블릭 인바운드 포트(80, 443, 8080 등)는 인프라 수준에서 완전히 닫혀 있습니다. 해커는 서버 IP로 직접 접속조차 불가능합니다.
2. **Cloudflare Tunnel (Outbound)**: 로컬망에 떠 있는 `cloudflared` 데몬이 Cloudflare 엣지로 아웃바운드 터널을 연결해 둡니다.
3. **클라이언트 요청**: 정상적인 유저가 `https://api.hooneyz.com`으로 접속하면, Cloudflare 엣지가 WAF(웹 방화벽)로 악성 페이로드를 1차 필터링합니다.
4. **터널 인그레스 (Ingress)**: 안전한 트래픽만 `cloudflared` 터널을 타고 들어와 내부망의 Nginx(Edge Proxy)로 전달됩니다.
5. **Nginx 헤더 주입**: Nginx는 Clickjacking 방지 등 보안 헤더를 추가하고, 사용자의 진짜 IP(`CF-Connecting-IP`)를 복원한 뒤 내부망의 API Gateway로 패스(Reverse Proxy)합니다.

> 💡 **검증 방법**: 터미널에서 서버 공인 IP로 `curl`을 날리면 연결이 거부(Connection Refused)됨을 증명할 수 있습니다. 오직 `examples/scenarios.http`의 **[1. MSA 상태 점검]**을 통해 인가된 도메인(Nginx Edge)으로만 접근 가능합니다.

---

## 🎭 시나리오 2: API Gateway를 통과한 헥사고날 도메인 격리

### [상황]
안전하게 API Gateway에 도착한 결제 요청 트래픽이 `business-service`로 라우팅되어 처리됩니다. 이 과정에서 회사의 결제 대행사(PG)가 변경되는 이벤트를 맞이합니다.

### [처리 흐름]
1. **Gateway 라우팅**: API Gateway는 URI 패턴(`/api/payment/**`)을 읽어 Spring Cloud 로드밸런서를 통해 `business-service` 인스턴스로 트래픽을 넘깁니다.
2. **Web Adapter (Inbound)**: `business-service`의 Controller가 요청을 받아 `PaymentCommand` DTO로 변환하고, Application Service(`ProcessPaymentUseCase`)로 넘깁니다.
3. **Domain Logic 수행**: 순수 자바 객체인 도메인 엔티티(`Payment`)가 생성됩니다. 초기 상태는 `CREATED`입니다.
4. **PG Adapter (Outbound)**: 도메인 계층은 바깥 세상을 전혀 모른 채 `PaymentGatewayPort` 인터페이스의 메서드만 호출합니다. 현재 스프링 빈으로 등록된 `TossPaymentsAdapter`가 요청을 가로채어 Toss API를 모의 호출합니다. (10만원 이상 결제 시 차단 시뮬레이션 적용)
5. **결과 전이**: 결과에 따라 도메인 엔티티는 스스로 `APPROVE()` 또는 `FAIL()` 상태로 전이합니다.

> 💡 **검증 방법**: `examples/scenarios.http`의 **[2. 결제 승인 요청 (정상)]**과 **[3. 결제 승인 요청 (한도 초과 거절)]**을 실행해보세요. 만약 PG사를 Toss에서 Stripe로 바꾸고 싶다면? 도메인 로직은 단 한 줄도 고칠 필요 없이 `StripeAdapter` 클래스만 하나 추가하여 갈아끼우면 됩니다! 이것이 Hexagonal Architecture의 진정한 힘입니다.
