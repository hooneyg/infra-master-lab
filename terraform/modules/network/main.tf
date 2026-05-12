# ╔══════════════════════════════════════════════════════════════════╗
# ║  📦 modules/network — Docker 네트워크 모듈                        ║
# ║                                                                  ║
# ║  MSA 서비스 간 격리된 통신 채널을 제공하는 Docker 브릿지 네트워크.  ║
# ║  여러 환경(local, dev, prod)에서 동일한 네트워크 구조를 재사용.     ║
# ╚══════════════════════════════════════════════════════════════════╝

resource "docker_network" "main" {
  name   = "${var.project_name}-${var.environment}-network"
  driver = "bridge"

  # IPAM 설정 — 서브넷을 명시하여 IP 충돌 방지
  ipam_config {
    subnet  = "172.28.0.0/16"
    gateway = "172.28.0.1"
  }

  # 라벨 — 리소스 식별 및 관리 자동화
  labels {
    label = "com.hooney.lab.project"
    value = var.project_name
  }

  labels {
    label = "com.hooney.lab.environment"
    value = var.environment
  }

  labels {
    label = "com.hooney.lab.managed-by"
    value = "terraform"
  }
}
