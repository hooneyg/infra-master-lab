# modules/docker-service — 변수 정의

variable "service_name" {
  description = "마이크로서비스 이름 (컨테이너명에 사용)"
  type        = string
}

variable "project_name" {
  description = "프로젝트 이름 (접두사)"
  type        = string
}

variable "image_id" {
  description = "Docker 이미지 ID (docker_image 리소스의 image_id)"
  type        = string
}

variable "internal_port" {
  description = "컨테이너 내부 포트"
  type        = number
}

variable "external_port" {
  description = "호스트 노출 포트 (0이면 미노출)"
  type        = number
  default     = 0
}

variable "network_name" {
  description = "연결할 Docker 네트워크 이름"
  type        = string
}

variable "environment" {
  description = "컨테이너 환경 변수 목록"
  type        = list(string)
  default     = []
}

variable "labels" {
  description = "컨테이너 라벨 (key-value 맵)"
  type        = map(string)
  default     = {}
}

variable "depends_on_resources" {
  description = "논리적 의존 관계 리소스 ID 목록 (문서화 목적)"
  type        = list(string)
  default     = []
}
