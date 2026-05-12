# modules/network — 출력값 정의

output "network_name" {
  description = "생성된 Docker 네트워크 이름"
  value       = docker_network.main.name
}

output "network_id" {
  description = "Docker 네트워크 고유 ID"
  value       = docker_network.main.id
}
