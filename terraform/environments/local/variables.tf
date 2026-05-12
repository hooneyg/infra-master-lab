# ╔══════════════════════════════════════════════════════════════════╗
# ║         📋 variables.tf — 입력 변수 정의                           ║
# ║                                                                  ║
# ║  [파일 목적]                                                     ║
# ║  Terraform 모듈에 주입되는 매개변수를 선언합니다.                    ║
# ║  실제 값은 terraform.tfvars 또는 CLI 인자로 전달합니다.             ║
# ║                                                                  ║
# ║  [설계 원칙]                                                     ║
# ║  1. 민감 정보(비밀번호)는 sensitive = true 로 콘솔 출력을 차단      ║
# ║  2. 기본값이 있는 변수는 tfvars 없이도 실행 가능하도록 설계          ║
# ║  3. description으로 변수 용도를 명확히 문서화                       ║
# ║                                                                  ║
# ║  @author Hooney — AI Architect                                   ║
# ╚══════════════════════════════════════════════════════════════════╝

# ──────────────────────────────────────────────────────────────────
# Docker Provider 설정
# ──────────────────────────────────────────────────────────────────
variable "docker_host" {
  description = <<-EOT
    Docker 데몬 소켓 경로.
    - Linux/macOS: unix:///var/run/docker.sock
    - Windows:     npipe:////.//pipe//docker_engine
  EOT
  type    = string
  default = "npipe:////.//pipe//docker_engine"   # Windows Docker Desktop 기본값
}

# ──────────────────────────────────────────────────────────────────
# 프로젝트 공통 설정
# ──────────────────────────────────────────────────────────────────
variable "project_name" {
  description = "프로젝트 이름 — 모든 리소스 이름의 접두사로 사용됩니다."
  type        = string
  default     = "infra-lab"
}

variable "environment" {
  description = "배포 환경 (local, dev, staging, prod)"
  type        = string
  default     = "local"

  validation {
    condition     = contains(["local", "dev", "staging", "prod"], var.environment)
    error_message = "environment는 local, dev, staging, prod 중 하나여야 합니다."
  }
}

# ──────────────────────────────────────────────────────────────────
# 데이터베이스 설정
# ──────────────────────────────────────────────────────────────────
variable "db_user" {
  description = "PostgreSQL 데이터베이스 사용자명"
  type        = string
  default     = "hooney"
}

variable "db_password" {
  description = "PostgreSQL 데이터베이스 비밀번호 — 콘솔 출력 차단됨"
  type        = string
  default     = "password"
  sensitive   = true   # terraform plan/apply 출력에서 마스킹 처리
}

variable "db_name" {
  description = "PostgreSQL 기본 데이터베이스 이름"
  type        = string
  default     = "infradb"
}

# ──────────────────────────────────────────────────────────────────
# 서비스 포트 매핑
# 각 MSA 서비스의 호스트 노출 포트를 외부에서 주입 가능하도록 변수화.
# 이로써 포트 충돌 시 tfvars만 수정하면 되므로 코드 변경이 불필요하다.
# ──────────────────────────────────────────────────────────────────
variable "config_service_port" {
  description = "Config Service 외부 노출 포트"
  type        = number
  default     = 8888
}

variable "gateway_service_port" {
  description = "Gateway Service 외부 노출 포트"
  type        = number
  default     = 8080
}

variable "db_port" {
  description = "PostgreSQL 외부 노출 포트"
  type        = number
  default     = 5432
}

# ──────────────────────────────────────────────────────────────────
# 컨테이너 이미지 버전
# ──────────────────────────────────────────────────────────────────
variable "postgres_image" {
  description = "PostgreSQL Docker 이미지 (tag 포함)"
  type        = string
  default     = "postgres:15-alpine"
}

variable "nginx_image" {
  description = "Nginx Docker 이미지 — Gateway 시뮬레이션용"
  type        = string
  default     = "nginx:alpine"
}
