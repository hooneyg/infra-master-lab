# ╔══════════════════════════════════════════════════════════════════╗
# ║  📦 modules/docker-service — 범용 Docker 컨테이너 모듈            ║
# ║                                                                  ║
# ║  MSA의 각 마이크로서비스를 동일한 패턴으로 생성하기 위한 모듈.       ║
# ║  Config, Business, Gateway 서비스 모두 이 모듈을 호출한다.         ║
# ║                                                                  ║
# ║  [모듈화 이점]                                                   ║
# ║  1. DRY 원칙 — 동일 코드 반복 제거                                ║
# ║  2. 일관성 — 모든 서비스가 동일한 라벨링/네트워크 구성               ║
# ║  3. 테스트 — 모듈 단위로 독립 테스트 가능                           ║
# ╚══════════════════════════════════════════════════════════════════╝

resource "docker_container" "service" {
  name  = "${var.project_name}-${var.service_name}"
  image = var.image_id

  # 환경 변수 주입
  env = var.environment

  # 포트 매핑 — external_port가 0이면 호스트에 노출하지 않음
  dynamic "ports" {
    for_each = var.external_port > 0 ? [1] : []
    content {
      internal = var.internal_port
      external = var.external_port
    }
  }

  # MSA 네트워크 연결
  networks_advanced {
    name = var.network_name
  }

  # 라벨 — 컨테이너 메타데이터 (모니터링, 로깅 연동에 활용)
  dynamic "labels" {
    for_each = var.labels
    content {
      label = labels.key
      value = labels.value
    }
  }

  # 자동 재시작
  restart = "unless-stopped"

  # Health Check
  healthcheck {
    test         = ["CMD", "curl", "-f", "http://localhost:${var.internal_port}/"]
    interval     = "15s"
    timeout      = "5s"
    start_period = "10s"
    retries      = 3
  }
}
