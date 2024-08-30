FROM eclipse-temurin:11-jre-alpine

ARG REPO_URL=""
ARG COMMIT=""
LABEL repo_url=${REPO_URL}
LABEL commit=${COMMIT}

VOLUME /tmp
ADD /target/iseplife-api-0.0.1-SNAPSHOT.jar iseplife-api.jar

ADD https://dtdg.co/latest-java-tracer dd-java-agent.jar
ENV COMMIT_SHA=${COMMIT}
ENV COMMIT_REPO_URL=${REPO_URL}

ENTRYPOINT java -javaagent:/dd-java-agent.jar -Ddd.logs.injection=true -Ddd.git.commit.sha=${COMMIT_SHA} -Ddd.git.repository_url=${COMMIT_REPO_URL} -Ddd.service=api -Ddd.env=prod -Djava.security.egd=file:/dev/./urandom -jar /iseplife-api.jar