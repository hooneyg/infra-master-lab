# ╔══════════════════════════════════════════════════════════════════╗
# ║         🐳 Dockerfile (Multi-stage Build for Java 21)           ║
# ║                                                                  ║
# ║  [빌드 전략]                                                     ║
# ║  빌드 환경과 실행 환경을 분리하여 공격 표면을 줄이고 이미지 크기 최적화 ║
# ╚══════════════════════════════════════════════════════════════════╝

# --- Step 1: Build Stage (컴파일 단계) ---
FROM eclipse-temurin:21-jdk-alpine AS builder 
WORKDIR /build                                 

# [핵심] 빌드 시점에 docker-compose가 넘겨줄 모듈명을 인자로 받음 (기본값: business-service)
ARG MODULE_NAME=business-service

COPY . .
RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew                         

# 주입받은 특정 모듈만 찝어서 빌드 (예: :config-service:bootJar)
RUN ./gradlew :${MODULE_NAME}:bootJar -x test

# --- Step 2: Run Stage (실행 단계) ---
FROM eclipse-temurin:21-jre-alpine             
WORKDIR /app                                    

# [핵심] 실행(복사) 단계에서도 주입받은 모듈명을 사용하기 위해 다시 선언 (기본값: business-service)
ARG MODULE_NAME=business-service

RUN addgroup -S hooney && adduser -S hooney -G hooney
USER hooney                                     

# 주입받은 모듈 내부의 빌드 결과물 폴더에서 정확히 그 JAR만 복사!
COPY --from=builder /build/${MODULE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080 8081 8888
