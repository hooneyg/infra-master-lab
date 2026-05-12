# 🏗️ Terraform — Infrastructure Provisioning (Day 0)

> Ansible이 서버를 **설정**한다면, Terraform은 서버를 **생성**합니다.

## 📋 개요

이 디렉토리는 infra-master-lab의 MSA 인프라를 **Terraform의 선언형 방식(HCL)**으로 프로비저닝합니다.

## 🗂️ 디렉토리 구조

```
terraform/
├── environments/
│   ├── local/                    # 🐳 로컬 Docker 기반 (무료, 즉시 실행)
│   │   ├── providers.tf          # Docker Provider 설정
│   │   ├── variables.tf          # 입력 변수 정의
│   │   ├── main.tf               # MSA 인프라 리소스
│   │   ├── outputs.tf            # 출력값 (URL, ID)
│   │   └── terraform.tfvars      # 로컬 환경 변수값
│   │
│   └── aws-reference/            # ☁️ AWS 프로덕션 참고 (비용 발생)
│       ├── main.tf               # VPC + EC2 + RDS + ALB
│       └── README.md             # 비용 경고
│
└── modules/                      # 📦 재사용 가능 모듈
    ├── network/                  # Docker 브릿지 네트워크
    └── docker-service/           # 범용 컨테이너 모듈
```

## 🚀 Quick Start (로컬 Lab)

### 사전 요구사항
- [Terraform CLI](https://developer.hashicorp.com/terraform/install) v1.5+
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) 실행 중

### 실행 명령

```bash
# 1. 작업 디렉토리 이동
cd terraform/environments/local

# 2. 초기화 — Provider 다운로드
terraform init

# 3. 실행 계획 미리보기
terraform plan

# 4. 인프라 프로비저닝
terraform apply

# 5. 생성된 인프라 확인
terraform output

# 6. 인프라 정리 (모든 리소스 삭제)
terraform destroy
```

## 📊 Ansible과의 통합 워크플로우

```
Terraform (생성)          Ansible (설정)
     │                         │
     ├── terraform apply       │
     │   → Docker 컨테이너 생성 │
     │   → 네트워크 구성         │
     │                         │
     ├── terraform output -json│
     │   → IP/포트 정보 추출    │
     │                    ─────┤
     │                         ├── ansible-playbook site.yml
     │                         │   → OS 패키지 설치
     │                         │   → 보안 설정
     │                         │   → Docker 구성
     │                         │
     └─────────────────────────┘
```

## ⚠️ 주의 사항

| 항목 | 설명 |
|------|------|
| `.tfstate` 파일 | 절대 Git에 커밋하지 마세요 (이미 .gitignore에 포함) |
| `terraform.tfvars` | 민감 정보가 포함될 수 있으므로 프로덕션에선 환경 변수 사용 권장 |
| AWS 레퍼런스 | 실행 시 실제 비용 발생 — README 확인 필수 |
