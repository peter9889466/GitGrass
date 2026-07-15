# 1. Build Stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사 (의존성 캐싱 목적)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Gradle 실행 권한 부여 및 의존성 사전 다운로드 (빌드 속도 향상)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 실행 가능한 JAR 빌드 (테스트 제외)
COPY src src
RUN ./gradlew bootJar -x test --no-daemon

# 2. Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 스테이지에서 생성된 최종 bootJar 결과물만 복사
COPY --from=build /app/build/libs/gitgrass-backend-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션이 외부와 통신할 포트 설정
EXPOSE 8080

# 컨테이너 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
