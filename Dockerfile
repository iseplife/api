FROM eclipse-temurin:11-jre-alpine

ARG DD_GIT_REPOSITORY_URL
ARG DD_GIT_COMMIT_SHA

VOLUME /tmp
ADD /target/iseplife-api-0.0.1-SNAPSHOT.jar iseplife-api.jar

ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar

ENV DD_GIT_REPOSITORY_URL=${DD_GIT_REPOSITORY_URL} 
ENV DD_GIT_COMMIT_SHA=${DD_GIT_COMMIT_SHA}

ENTRYPOINT java -javaagent:/dd-java-agent.jar -Ddd.logs.injection=true -Ddd.profiling.enabled -Ddd.service=api -Ddd.env=prod -Djava.security.egd=file:/dev/./urandom -jar /iseplife-api.jar
