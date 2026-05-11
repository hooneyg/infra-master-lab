# 📚 Tech Wiki: The Philosophy of Infra Master Lab

> **"Infrastructure is no longer hardware; it is defined as Software."**  
> 본 프로젝트에 녹여낸 아키텍처 철학과 클라우드 네이티브 운영 전략을 상세히 기록합니다.

---

## 🏗️ 1. Hexagonal Architecture: The Sanctuary of Business Logic

비즈니스 로직은 외부 기술의 변화(DB 교체, 프레임워크 변경 등)로부터 완전히 보호받아야 합니다.

### 🧩 아키텍처 도식
```mermaid
graph LR
    subgraph External ["External World"]
        User["User / Client"]
        DB["RDBMS / NoSQL"]
    end

    subgraph Driving ["Driving Adapters"]
        WebAdapter["Web Adapter<br/>(REST Controller)"]
    end

    subgraph PrimaryPorts ["Input Ports"]
        InPort["Input Port<br/>(Interface)"]
    end

    subgraph Hexagon ["Domain Core"]
        Svc["Domain Service"]
        Entity["Domain Entities"]
    end

    subgraph SecondaryPorts ["Output Ports"]
        OutPort["Output Port<br/>(Interface)"]
    end

    subgraph Driven ["Driven Adapters"]
        DbAdapter["Persistence Adapter<br/>(JPA/MyBatis)"]
    end

    User --> WebAdapter
    WebAdapter --> InPort
    InPort --> Svc
    Svc --> Entity
    Svc --> OutPort
    OutPort -->|의존성 역전 DIP| DbAdapter
    DbAdapter --> DB

    style Hexagon fill:#2563EB,stroke:#1E40AF,color:#FFFFFF,stroke-width:2px
    style PrimaryPorts fill:#10B981,stroke:#059669,color:#FFFFFF
    style SecondaryPorts fill:#10B981,stroke:#059669,color:#FFFFFF
```

### 📘 핵심 원리: 의존성 역전 (DIP)
- **성역화**: 도메인 계층은 외부 라이브러리 의존성 0%를 유지합니다.
- **Port**: 도메인이 외부와 소통하기 위해 정의한 **'계약'**입니다.
- **Adapter**: 특정 기술(JPA, REST 등)을 사용하여 포트를 실제로 구현한 **'플러그인'**입니다.

---

## 🐍 2. Ansible: Python-based Infrastructure as Code

수동 설정에 의한 '눈송이 서버(Snowflake Server)' 현상을 방지하고, 모든 인프라를 코드로 관리합니다.

### ⚙️ 자동화 흐름
```mermaid
graph TD
    subgraph ControlNode ["Ansible Control Node (Python)"]
        Playbook["Playbooks<br/>(YAML)"]
        Inven["Inventory File"]
        Engine["Ansible Engine"]
    end

    subgraph Transport ["Secure Transport"]
        SSH["SSH / Python Interpreter"]
    end

    subgraph ManagedNodes ["Target Nodes"]
        Srv1["Web Server"]
        Srv2["DB Server"]
    end

    Playbook --> Engine
    Inven --> Engine
    Engine --> SSH
    SSH --> Srv1
    SSH --> Srv2

    style ControlNode fill:#FF9900,stroke:#CC7700,color:#FFFFFF
```

---

## ☸️ 3. Kubernetes: Resilience & Zero-Downtime

클라우드 네이티브 인프라의 핵심은 **'가용성'**과 **'자가 치유'**입니다.

### 🔄 무중단 배포 매커니즘 (Rolling Update)
```mermaid
graph TD
    User["Traffic"] --> LB["Ingress / Service"]

    subgraph V1 ["Current (V1)"]
        P1["Pod 1 (Running)"]
        P2["Pod 2 (Terminating)"]
    end

    subgraph V2 ["New (V2)"]
        P3["Pod 3 (Ready)"]
    end

    LB -->|Load Balance| P1
    LB -->|Health Check| P3
    
    style V2 fill:#10B981,stroke:#059669,color:#FFFFFF
    style V1 fill:#EF4444,stroke:#B91C1C,color:#FFFFFF
```

- **Self-Healing**: Pod 장애 시 K8S 컨트롤러가 자동으로 새 인스턴스를 생성하여 가용성을 복구합니다.
- **HPA**: CPU/Memory 부하에 따라 수초 내에 인프라를 자동 확장(Scale-out)합니다.

---
**Designed by Hooney (AI Architect)** 🚀
