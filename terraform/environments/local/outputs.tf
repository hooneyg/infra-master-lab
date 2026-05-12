# outputs.tf — Terraform 출력값 정의
# terraform apply 후 사용자에게 보여줄 정보 및 Ansible 연동용 데이터

output "network_name" {
  description = "생성된 Docker 브릿지 네트워크 이름"
  value       = module.network.network_name
}

output "network_id" {
  description = "Docker 네트워크 고유 ID"
  value       = module.network.network_id
}

output "gateway_url" {
  description = "Gateway Service 접근 URL"
  value       = "http://localhost:${var.gateway_service_port}"
}

output "config_service_url" {
  description = "Config Service 접근 URL"
  value       = "http://localhost:${var.config_service_port}"
}

output "database_url" {
  description = "PostgreSQL JDBC URL"
  value       = "jdbc:postgresql://localhost:${var.db_port}/${var.db_name}"
}

output "container_ids" {
  description = "생성된 모든 컨테이너 ID"
  value = {
    postgres         = docker_container.postgres.id
    config_service   = module.config_service.container_id
    business_service = module.business_service.container_id
    gateway_service  = module.gateway_service.container_id
  }
}

output "ansible_inventory_hint" {
  description = "Ansible 동적 인벤토리 생성용 힌트"
  value = {
    db_host      = "localhost:${var.db_port}"
    config_host  = "localhost:${var.config_service_port}"
    gateway_host = "localhost:${var.gateway_service_port}"
  }
}
