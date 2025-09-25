# Dockerfile (수정된 버전)
# 1단계: 빌드
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

# Gradle Wrapper 및 설정 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies --no-daemon || return 0

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행
FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul

#  오디오 저장 디렉토리 생성 및 권한 설정
RUN mkdir -p /app/audio-storage
RUN chmod 755 /app/audio-storage

# root 유저 사용 방지
RUN useradd -m springuser
# 오디오 디렉토리 소유권을 springuser로 변경
RUN chown -R springuser:springuser /app/audio-storage
USER springuser

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# JVM 메모리 및 GC 최적화
ENV JAVA_OPTS="-Xms512m -Xmx768m \
  -XX:+UseG1GC \
  -XX:+UseStringDeduplication \
  -XX:MaxMetaspaceSize=128m \
  -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]