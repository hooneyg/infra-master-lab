# ╔══════════════════════════════════════════════════════════════════╗
# ║  ☁️ AWS 프로덕션 레퍼런스 — VPC + EC2 + RDS + ALB                ║
# ║                                                                  ║
# ║  ⚠️ WARNING: 이 코드는 실행 시 실제 AWS 비용이 발생합니다!          ║
# ║  학습 및 참고 목적으로만 제공되며, 실행 전 요금을 반드시 확인하세요.   ║
# ║                                                                  ║
# ║  [구성 요소]                                                     ║
# ║  1. VPC + 서브넷 (Public/Private)                                ║
# ║  2. 보안 그룹 (Security Group)                                   ║
# ║  3. EC2 인스턴스 (Application Server)                            ║
# ║  4. RDS PostgreSQL (Managed Database)                            ║
# ║  5. ALB (Application Load Balancer)                              ║
# ║                                                                  ║
# ║  [비용 예상 (서울 리전, 최소 사양)]                                ║
# ║  EC2 t3.micro:   ~$10/월                                        ║
# ║  RDS db.t3.micro: ~$15/월                                       ║
# ║  ALB:             ~$20/월                                        ║
# ║  합계:            ~$45/월 (프리 티어 적용 시 일부 절감)              ║
# ║                                                                  ║
# ║  @author Hooney — AI Architect                                   ║
# ╚══════════════════════════════════════════════════════════════════╝

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# ──────────────────────────────────────────────────────────────────
# Provider 설정
# AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY 환경 변수 필수
# 또는 ~/.aws/credentials 파일 설정
# ──────────────────────────────────────────────────────────────────
provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "infra-master-lab"
      Environment = var.environment
      ManagedBy   = "terraform"
      Owner       = "hooney"
    }
  }
}

# ══════════════════════════════════════════════════════════════════
# 변수 정의
# ══════════════════════════════════════════════════════════════════

variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"   # 서울 리전
}

variable "environment" {
  description = "배포 환경"
  type        = string
  default     = "dev"
}

variable "db_password" {
  description = "RDS 마스터 비밀번호"
  type        = string
  sensitive   = true
}

# ══════════════════════════════════════════════════════════════════
# 1. VPC — 가상 사설 네트워크
# 모든 AWS 리소스가 배치되는 논리적 격리 환경
# ══════════════════════════════════════════════════════════════════

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = { Name = "infra-lab-vpc" }
}

# Public 서브넷 — 인터넷 접근 가능 (ALB, Bastion)
resource "aws_subnet" "public" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index + 1}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]

  map_public_ip_on_launch = true
  tags = { Name = "infra-lab-public-${count.index + 1}" }
}

# Private 서브넷 — 인터넷 비접근 (EC2, RDS)
resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index + 10}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = { Name = "infra-lab-private-${count.index + 1}" }
}

data "aws_availability_zones" "available" {
  state = "available"
}

# ══════════════════════════════════════════════════════════════════
# 2. 보안 그룹 — 방화벽 규칙
# ══════════════════════════════════════════════════════════════════

resource "aws_security_group" "app" {
  name_prefix = "infra-lab-app-"
  vpc_id      = aws_vpc.main.id

  # HTTP 인바운드 (ALB → EC2)
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  # 모든 아웃바운드 허용
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "infra-lab-app-sg" }
}

resource "aws_security_group" "db" {
  name_prefix = "infra-lab-db-"
  vpc_id      = aws_vpc.main.id

  # PostgreSQL 인바운드 (EC2 → RDS만 허용)
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app.id]
  }

  tags = { Name = "infra-lab-db-sg" }
}

# ══════════════════════════════════════════════════════════════════
# 3. RDS — 관리형 PostgreSQL
# ══════════════════════════════════════════════════════════════════

resource "aws_db_instance" "postgres" {
  identifier = "infra-lab-db"
  engine     = "postgres"

  instance_class    = "db.t3.micro"   # 프리 티어 대상
  allocated_storage = 20

  db_name  = "infradb"
  username = "hooney"
  password = var.db_password

  vpc_security_group_ids = [aws_security_group.db.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name

  skip_final_snapshot = true   # Lab 환경이므로 최종 스냅샷 생략

  tags = { Name = "infra-lab-postgres" }
}

resource "aws_db_subnet_group" "main" {
  name       = "infra-lab-db-subnet"
  subnet_ids = aws_subnet.private[*].id

  tags = { Name = "infra-lab-db-subnet-group" }
}

# ══════════════════════════════════════════════════════════════════
# 출력값
# ══════════════════════════════════════════════════════════════════

output "vpc_id" {
  value = aws_vpc.main.id
}

output "rds_endpoint" {
  value = aws_db_instance.postgres.endpoint
}
