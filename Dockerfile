# ╔══════════════════════════════════════════════════════════════════╗
# ║         🐳 Dockerfile (Multi-stage Build for Java 21)           ║
# ║                                                                  ║
# ║  [빌드 전략]                                                     ║
# ║  빌드 환경과 실행 환경을 분리하여 공격 표면을 줄이고 이미지 크기 최적화 ║
# ╚══════════════════════════════════════════════════════════════════╝

# --- Step 1: Build Stage (최종 이미지에 포함되지 않는 빌드 전용 단계) ---
FROM eclipse-temurin:21-jdk-alpine AS builder 
# 컴파일을 위해 JDK가 포함된 알파인 리눅스 이미지 사용
WORKDIR /build                                 
# 작업 디렉토리를 /build로 설정

# Gradle 의존성 해결 및 소스 컴파일을 위해 전체 프로젝트 파일 복사
COPY . .
RUN chmod +x ./gradlew                         
# 실행 권한 부여

# 부트 JAR 빌드 실행 (테스트 단계는 CI 파이프라인에서 별도로 수행하므로 제외하여 빌드 속도 향상)
RUN ./gradlew :business-service:bootJar -x test

# --- Step 2: Run Stage (실제 운영 환경에 배포될 최소화된 이미지 단계) ---
FROM eclipse-temurin:21-jre-alpine             
# 실행 시에는 JDK 대신 가벼운 JRE만 포함하여 용량 최적화
WORKDIR /app                                    
# 실행 디렉토리 설정

# [보안 강화] 루트 권한 탈취 시 시스템 전체가 위험해지는 것을 방지하기 위해 비권한 유저 생성
RUN addgroup -S hooney && adduser -S hooney -G hooney
USER hooney                                     
# 이후 모든 명령은 'hooney' 유저 권한으로 실행됨

# 빌드 스테이지에서 생성된 최종 실행 JAR 파일만 추출하여 복사 (용량 획기적 감소)
COPY --from=builder /build/business-service/build/libs/*.jar app.jar

# JVM 환경 설정 및 운영 프로파일 지정하여 애플리케이션 엔트리 포인트 설정
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
EXPOSE 8081                                     
# 문서상으로 8081 포트 사용을 명시
