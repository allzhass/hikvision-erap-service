FROM amazoncorretto:21
WORKDIR /app
USER root

# Install curl and telnet
RUN yum update -y && \
    yum install -y curl telnet && \
    yum clean all

COPY build/libs/erap-service-0.1.1.jar /app/erap-service-0.1.1.jar

ENTRYPOINT ["java", "-jar", "/app/erap-service-0.1.1.jar"]
