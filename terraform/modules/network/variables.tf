# modules/network — 변수 정의

variable "project_name" {
  description = "프로젝트 이름 (네트워크 이름 접두사)"
  type        = string
}

variable "environment" {
  description = "배포 환경 (local, dev, staging, prod)"
  type        = string
}
