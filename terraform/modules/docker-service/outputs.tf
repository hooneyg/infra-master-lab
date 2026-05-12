# modules/docker-service — 출력값 정의

output "container_id" {
  description = "생성된 컨테이너의 고유 ID"
  value       = docker_container.service.id
}

output "container_name" {
  description = "컨테이너 이름"
  value       = docker_container.service.name
}
