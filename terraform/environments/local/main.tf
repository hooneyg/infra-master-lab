# ╔══════════════════════════════════════════════════════════════════╗
# ║         🏗️ main.tf — MSA 인프라 프로비저닝 (Docker Provider)       ║
# ║                                                                  ║
# ║  [파일 목적]                                                     ║
# ║  기존 docker-compose.yml에서 정의하던 MSA 인프라를 Terraform의      ║
# ║  선언형 방식으로 재구현합니다.                                      ║
# ║                                                                  ║
# ║  [docker-compose.yml 대비 이점]                                   ║
# ║  1. 상태 관리 — tfstate로 인프라 현재 상태를 정확히 추적             ║
# ║  2. 변경 미리보기 — terraform plan으로 적용 전 영향 분석             ║
# ║  3. 모듈화 — 재사용 가능한 인프라 컴포넌트 설계                      ║
# ║  4. 의존성 그래프 — 자동 순서 제어 및 병렬 프로비저닝                 ║
# ║                                                                  ║
# ║  [리소스 구성]                                                    ║
# ║  Network → PostgreSQL → Config Service → Business Service → GW  ║
# ║                                                                  ║
# ║  @author Hooney — AI Architect                                   ║
# ╚══════════════════════════════════════════════════════════════════╝

# ══════════════════════════════════════════════════════════════════
# 1. 모듈 호출 — 재사용 가능한 공통 인프라 컴포넌트
# ══════════════════════════════════════════════════════════════════

# ──────────────────────────────────────────────────────────────────
# 1.1 네트워크 모듈
# 모든 MSA 서비스가 통신하는 격리된 Docker 브릿지 네트워크.
# docker-compose.yml의 networks.infra-network에 해당한다.
# ──────────────────────────────────────────────────────────────────
module "network" {
  source = "../../modules/network"

  project_name = var.project_name
  environment  = var.environment
}

# ══════════════════════════════════════════════════════════════════
# 2. 데이터베이스 계층 — PostgreSQL
# ══════════════════════════════════════════════════════════════════

# ──────────────────────────────────────────────────────────────────
# 2.1 PostgreSQL 이미지 Pull
# keep_locally = true: 컨테이너 삭제 시에도 이미지를 로컬에 유지하여
# 재생성 시 다운로드 시간을 절약한다.
# ──────────────────────────────────────────────────────────────────
resource "docker_image" "postgres" {
  name         = var.postgres_image
  keep_locally = true
}

# ──────────────────────────────────────────────────────────────────
# 2.2 PostgreSQL 컨테이너
# docker-compose.yml의 db 서비스에 대응한다.
# 환경 변수로 초기 DB, 사용자, 비밀번호를 설정한다.
# ──────────────────────────────────────────────────────────────────
resource "docker_container" "postgres" {
  name  = "${var.project_name}-db"
  image = docker_image.postgres.image_id

  # 환경 변수 — PostgreSQL 초기화 파라미터
  env = [
    "POSTGRES_USER=${var.db_user}",
    "POSTGRES_PASSWORD=${var.db_password}",
    "POSTGRES_DB=${var.db_name}"
  ]

  # 호스트 포트 매핑 — 호스트에서 DB 클라이언트로 직접 접속 가능
  ports {
    internal = 5432
    external = var.db_port
  }

  # MSA 전용 네트워크에 연결
  networks_advanced {
    name = module.network.network_name
  }

  # 데이터 영속화 — 컨테이너 재생성 시에도 데이터 보존
  volumes {
    volume_name    = docker_volume.postgres_data.name
    container_path = "/var/lib/postgresql/data"
  }

  # Health Check — 데이터베이스가 쿼리를 수락할 준비가 되었는지 확인
  healthcheck {
    test         = ["CMD-SHELL", "pg_isready -U ${var.db_user} -d ${var.db_name}"]
    interval     = "10s"
    timeout      = "5s"
    start_period = "15s"
    retries      = 5
  }

  # 자동 재시작 정책
  restart = "unless-stopped"
}

# ──────────────────────────────────────────────────────────────────
# 2.3 PostgreSQL 데이터 볼륨
# Named Volume으로 생성하여 컨테이너 생명주기와 데이터를 분리한다.
# docker volume ls 로 확인 가능.
# ──────────────────────────────────────────────────────────────────
resource "docker_volume" "postgres_data" {
  name = "${var.project_name}-postgres-data"
}

# ══════════════════════════════════════════════════════════════════
# 3. Config Service — 중앙 집중 설정 서버
# Spring Cloud Config Server를 시뮬레이션한다.
# 실제 프로덕션에서는 Spring Boot 빌드 이미지를 사용한다.
# 로컬 Lab에서는 Nginx로 대체하여 네트워크 구성에 집중한다.
# ══════════════════════════════════════════════════════════════════

resource "docker_image" "nginx" {
  name         = var.nginx_image
  keep_locally = true
}

module "config_service" {
  source = "../../modules/docker-service"

  service_name  = "config-service"
  project_name  = var.project_name
  image_id      = docker_image.nginx.image_id
  internal_port = 80
  external_port = var.config_service_port
  network_name  = module.network.network_name

  environment = [
    "SERVICE_NAME=config-service",
    "SERVICE_ROLE=centralized-configuration"
  ]

  labels = {
    "com.hooney.lab.service" = "config-service"
    "com.hooney.lab.tier"    = "infrastructure"
    "com.hooney.lab.env"     = var.environment
  }
}

# ══════════════════════════════════════════════════════════════════
# 4. Business Service — 핵심 비즈니스 로직
# Hexagonal Architecture 기반의 도메인 서비스.
# Config Service와 PostgreSQL에 의존한다.
# ══════════════════════════════════════════════════════════════════

module "business_service" {
  source = "../../modules/docker-service"

  service_name  = "business-service"
  project_name  = var.project_name
  image_id      = docker_image.nginx.image_id
  internal_port = 80
  external_port = 0    # 0 = 호스트에 포트 노출하지 않음 (내부 통신만)
  network_name  = module.network.network_name

  environment = [
    "SERVICE_NAME=business-service",
    "SERVICE_ROLE=hexagonal-core",
    "SPRING_PROFILES_ACTIVE=prod",
    "SPRING_CONFIG_IMPORT=optional:configserver:http://config-service:80",
    "SPRING_DATASOURCE_URL=jdbc:postgresql://${var.project_name}-db:5432/${var.db_name}"
  ]

  labels = {
    "com.hooney.lab.service" = "business-service"
    "com.hooney.lab.tier"    = "application"
    "com.hooney.lab.env"     = var.environment
  }

  # Terraform은 의존성 그래프를 자동 생성하지만,
  # 논리적 의존 관계를 명시적으로 선언하여 가독성을 높인다.
  depends_on_resources = [
    module.config_service.container_id,
    docker_container.postgres.id
  ]
}

# ══════════════════════════════════════════════════════════════════
# 5. Gateway Service — API 진입점
# 모든 외부 트래픽의 단일 진입점 (Single Entry Point).
# 실제 프로덕션에서는 Spring Cloud Gateway를 사용한다.
# ══════════════════════════════════════════════════════════════════

module "gateway_service" {
  source = "../../modules/docker-service"

  service_name  = "gateway-service"
  project_name  = var.project_name
  image_id      = docker_image.nginx.image_id
  internal_port = 80
  external_port = var.gateway_service_port
  network_name  = module.network.network_name

  environment = [
    "SERVICE_NAME=gateway-service",
    "SERVICE_ROLE=api-gateway",
    "SPRING_PROFILES_ACTIVE=prod",
    "SPRING_CONFIG_IMPORT=optional:configserver:http://config-service:80"
  ]

  labels = {
    "com.hooney.lab.service" = "gateway-service"
    "com.hooney.lab.tier"    = "edge"
    "com.hooney.lab.env"     = var.environment
  }

  depends_on_resources = [
    module.config_service.container_id,
    module.business_service.container_id
  ]
}
