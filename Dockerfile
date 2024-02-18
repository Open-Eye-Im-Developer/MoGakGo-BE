# 어플리케이션 .jar 파일을 여러 레이어로 extracting
FROM eclipse-temurin:17-jre AS builder
WORKDIR application
ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# 실제 어플리케이션의 실행 환경
FROM eclipse-temurin:17-jre
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

# 어플리케이션 .jar 파일의 압축 해제로, JarLauncher를 통해 어플리케이션 실행
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "org.springframework.boot.loader.JarLauncher"]
