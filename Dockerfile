FROM maven:3.5-jdk-10-slim as builder
COPY . .
RUN mvn package -Dmaven.test.skip=true

FROM openjdk:10-jre-slim
WORKDIR /app
COPY --from=builder target/iseplive-api-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/customer-0.0.1-SNAPSHOT.jar"]