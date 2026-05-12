# ╔══════════════════════════════════════════════════════════════════╗
# ║         🔧 providers.tf — Terraform Provider 설정                 ║
# ║                                                                  ║
# ║  [파일 목적]                                                     ║
# ║  Terraform이 사용할 Provider(플러그인)를 선언합니다.               ║
# ║  이 설정으로 Terraform은 Docker API와 통신할 수 있게 됩니다.        ║
# ║                                                                  ║
# ║  [Provider 선택 근거]                                             ║
# ║  kreuzwerker/docker: 공식 Docker Provider로, 로컬 Docker 데몬과    ║
# ║  통신하여 컨테이너/네트워크/이미지/볼륨을 IaC로 관리합니다.          ║
# ║  클라우드 계정 없이 무료로 인프라 프로비저닝을 학습할 수 있습니다.    ║
# ║                                                                  ║
# ║  @author Hooney — AI Architect                                   ║
# ╚══════════════════════════════════════════════════════════════════╝

terraform {
  # ──────────────────────────────────────────────────────────────
  # Terraform 최소 버전 제약
  # 1.5+ 부터 import 블록, check 블록 등 최신 기능을 사용할 수 있다.
  # ──────────────────────────────────────────────────────────────
  required_version = ">= 1.5.0"

  required_providers {
    # Docker Provider — 로컬 Docker 데몬을 Terraform으로 제어
    # Registry: https://registry.terraform.io/providers/kreuzwerker/docker
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"   # 메이저 버전 고정으로 Breaking Change 방지
    }
  }
}

# ──────────────────────────────────────────────────────────────────
# Docker Provider 설정
# ──────────────────────────────────────────────────────────────────
provider "docker" {
  # host: Docker 데몬 소켓 경로
  #
  # [OS별 설정]
  # - Linux:   unix:///var/run/docker.sock (기본값)
  # - Windows: npipe:////.//pipe//docker_engine
  # - macOS:   unix:///var/run/docker.sock (Docker Desktop)
  #
  # ⚠️ Windows에서 Docker Desktop이 실행 중이어야 합니다.
  # 환경 변수 DOCKER_HOST가 설정되어 있으면 자동으로 해당 값을 사용합니다.
  host = var.docker_host
}
