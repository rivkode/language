# 1단계: 빌드 (JDK 필요)
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Scouter Agent 다운로드 (버전은 최신으로 조정 가능)
ADD https://github.com/scouter-project/scouter/releases/download/v2.20.0/scouter-all-2.20.0.tar.gz /app/scouter.tar.gz
RUN tar -xvf scouter.tar.gz && rm scouter.tar.gz

# Gradle Wrapper 및 설정 복사 (캐시 효율화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# 소스 복사 후 빌드
COPY . .
RUN chmod +x gradlew
# plain jar 생성을 방지하거나, 특정 jar만 빌드되도록 설정하는 것이 좋습니다.
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 (실행 시에는 JRE만 있어도 충분하여 용량이 줄어듭니다)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV TZ=Asia/Seoul

# 에이전트 복사
#COPY --from=builder /app/scouter/agent.java /app/scouter-agent
# agent.java 디렉토리 자체가 아니라 jar만 정확히 복사
COPY --from=builder /app/scouter/agent.java/scouter.agent.jar /app/scouter-agent/scouter.agent.jar
COPY --from=builder /app/scouter/agent.java/conf /app/scouter-agent/conf

# 기본 설정 파일(scouter.conf) 생성 - 뒤의 Deployment에서 환경변수로 덮어쓸 예정
#RUN touch /app/scouter-agent/conf/scouter.conf

# 오디오 저장 디렉토리 생성 및 권한 설정
RUN mkdir -p /app/audio-storage
RUN chmod 755 /app/audio-storage

# root 유저 사용 방지 (보안 권장사항)
RUN useradd -m springuser
RUN chown -R springuser:springuser /app/audio-storage
RUN chown -R springuser:springuser /app/scouter-agent
USER springuser

# 빌드된 JAR 복사 (빌드 단계에서 생성된 특정 jar만 지정)
# *.jar 대신 구체적인 이름을 쓰거나, plain jar가 생성되지 않도록 설정되어 있어야 합니다.
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# JVM 메모리 및 GC 최적화
ENV JAVA_OPTS="-Xms512m -Xmx768m \
  -XX:+UseG1GC \
  -XX:+UseStringDeduplication \
  -XX:MaxMetaspaceSize=128m \
  -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]