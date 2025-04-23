# ERAP Service

A Spring Boot-based microservice for handling electronic document processing and digital signatures.

## Overview

ERAP Service is a Java-based microservice that provides functionality for electronic document processing, digital signatures, and integration with external services. The service is built using Spring Boot 3.x and follows modern microservice architecture patterns.

## Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Cloud 2023.0.2
- PostgreSQL Database
- Spring Data JPA
- Spring WebFlux
- OpenFeign for HTTP clients
- Lombok for reducing boilerplate code
- SpringDoc OpenAPI for API documentation
- Vault Java Driver for secrets management
- XML Security (Apache Santuario) for XML processing
- KalkanCrypt for cryptographic operations

## Project Structure

```
src/main/java/kz/bdl/erapservice/
├── controller/     # REST API endpoints
├── service/        # Business logic implementation
├── repository/     # Data access layer
├── entity/         # Database entities
├── dto/            # Data Transfer Objects
├── mapper/         # Object mapping utilities
├── external/       # External service integrations
├── util/           # Utility classes
├── exception/      # Custom exceptions
└── signature/      # Digital signature handling
```

## Prerequisites

- Java 21 or higher
- PostgreSQL database
- Gradle 8.x or higher
- Docker (optional, for containerization)

## Configuration

The application can be configured through environment variables or application.yaml. Key configuration areas include:

- Database connection
- External service endpoints
- Logging levels
- Signature algorithms
- Secret storage paths

## Environment Variables

- `SPRING_DATASOURCE_URL`: PostgreSQL database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `FEIGN_CLIENT_CONNECT_TIMEOUT`: Feign client connection timeout
- `FEIGN_CLIENT_READ_TIMEOUT`: Feign client read timeout
- `ROOT_LOGGING_LEVEL`: Root logging level
- `SERVICE_SMARTBRIDGE_HOST`: Smart Bridge service host URL
- `SECRET_STORAGE_PATH`: Path to secrets storage file

## Building the Project

```bash
./gradlew build
```

## Running the Application

```bash
./gradlew bootRun
```

Or using Docker:

```bash
docker build -t erap-service .
docker run -p 8080:8080 erap-service
```

## API Documentation

Once the application is running, the API documentation can be accessed at:
```
http://localhost:8080/erap-service/swagger-ui.html
```

## Logging

Logs are written to `/app/logs/erap-service.log` with the following features:
- Rolling file policy
- Maximum file size: 100MB
- Maximum history: 10 files
- Configurable log levels
- Structured logging format

## Security

The service implements various security measures:
- Digital signature verification
- Secure secret storage
- XML security processing
- Cryptographic operations using KalkanCrypt

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary and confidential. Unauthorized copying or distribution is prohibited. 