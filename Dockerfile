FROM eclipse-temurin:11-jre-alpine

VOLUME /tmp
ADD /target/iseplife-api-0.0.1-SNAPSHOT.jar iseplife-api.jar
ADD /firebase-auth.json firebase-auth.json
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/iseplife-api.jar"]