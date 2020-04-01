FROM openjdk:11-jre-slim

VOLUME /tmp
ADD /target/iseplife-api-0.0.1-SNAPSHOT.jar iseplife-api.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/iseplife-api.jar"]
