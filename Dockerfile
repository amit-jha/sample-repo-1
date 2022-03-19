FROM openjdk:17-slim as build
COPY target/SearchApi-0.0.1-SNAPSHOT.jar SearchApi-0.0.1-SNAPSHOT.jar
COPY store /tmp/store
ENTRYPOINT ["java", "-jar", "/SearchApi-0.0.1-SNAPSHOT.jar"]