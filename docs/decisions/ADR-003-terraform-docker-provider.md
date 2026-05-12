# ADR-003: Terraform Docker Provider를 활용한 IaC 이원화 전략

## 상태
**승인됨 (Accepted)** — 2026-05-13

## 컨텍스트

infra-master-lab 프로젝트는 기존에 Ansible만을 사용하여 IaC(Infrastructure as Code)를 구현하고 있었습니다.
Ansible은 서버 구성 관리(Configuration Management)에는 탁월하지만, 인프라 자체를 프로비저닝하는 데는 구조적 한계가 있습니다.

### 기존 문제점
1. **상태 관리 부재** — Ansible은 State File이 없어 현재 인프라 상태를 추적할 수 없습니다.
2. **프로비저닝 한계** — VM/네트워크/스토리지 생성은 Ansible의 핵심 역할이 아닙니다.
3. **드리프트 감지 불가** — 수동 변경 사항을 자동으로 감지할 수 없습니다.
4. **포트폴리오 완성도** — IaC의 절반(프로비저닝)만 커버하고 있어 기술 역량 증명이 불완전합니다.

## 결정

**Terraform을 추가 도입하여 Ansible과 상호보완적으로 운영합니다.**

- **Day 0 (프로비저닝)**: Terraform — 인프라 리소스 생성/변경/삭제
- **Day 1+ (구성 관리)**: Ansible — OS 설정, 패키지 설치, 서비스 구성

### Provider 선택: kreuzwerker/docker

| 대안 | 선택 여부 | 이유 |
|------|:--------:|------|
| `kreuzwerker/docker` | ✅ 채택 | 로컬 무료 실행, 클라우드 계정 불필요, 즉시 학습 가능 |
| `hashicorp/aws` | 레퍼런스만 | 실행 시 비용 발생, 계정 필요 |
| OpenTofu | 미채택 | Terraform과 호환되나 생태계가 아직 성숙하지 않음 |

## 결과

### 긍정적
- IaC의 두 축(프로비저닝 + 구성관리)을 모두 커버하여 포트폴리오 완성도 향상
- 로컬에서 무료로 Terraform 핵심 개념(State, Plan, Module, Output) 학습 가능
- Ansible 기존 코드를 수정하지 않고 독립적으로 추가 가능
- Terraform → Ansible 연동 파이프라인으로 End-to-End 자동화 구현

### 부정적/리스크
- HCL 언어 학습 곡선이 존재 (YAML과 다른 문법 체계)
- `.tfstate` 파일 관리에 주의 필요 (Git 커밋 금지, 원격 Backend 권장)
- Docker Provider는 프로덕션 배포보다는 학습/테스트에 적합

## 참고 자료
- [Terraform vs Ansible: Red Hat 공식 비교](https://www.redhat.com/en/topics/automation/ansible-vs-terraform)
- [kreuzwerker/docker Provider 문서](https://registry.terraform.io/providers/kreuzwerker/docker/latest)
- [HashiCorp Learn: Terraform Docker Tutorial](https://developer.hashicorp.com/terraform/tutorials/docker-get-started)
