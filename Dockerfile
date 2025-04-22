FROM amazoncorretto:21
WORKDIR /app
USER root

COPY build/libs/erap-service-0.0.3.jar /app/erap-service-0.0.3.jar

ENTRYPOINT ["java", "-jar", "/app/erap-service-0.0.3.jar"]
