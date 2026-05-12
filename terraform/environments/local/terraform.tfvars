# terraform.tfvars — 로컬 환경 변수값
# 이 파일의 값이 variables.tf의 default를 오버라이드합니다.

# Docker 데몬 소켓 (Windows Docker Desktop 기본)
docker_host = "npipe:////.//pipe//docker_engine"

# 프로젝트 설정
project_name = "infra-lab"
environment  = "local"

# DB 설정
db_user     = "hooney"
db_password = "password"
db_name     = "infradb"

# 서비스 포트
config_service_port  = 8888
gateway_service_port = 8080
db_port              = 5432

# 이미지 버전
postgres_image = "postgres:15-alpine"
nginx_image    = "nginx:alpine"
