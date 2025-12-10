# Notes

_The service provides functionality for storing and retrieving notes linked to an organization or a citizen._

## Getting Started

### Prerequisites

- **Java 25 or higher**
- **Maven**
- **MariaDB**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/Sundsvallskommun/api-service-notes.git
cd api-service-notes
```

2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

```bash
mvn spring-boot:run
```

- Using Gradle:

```bash
gradle bootRun
```

## Dependencies

This microservice has no dependencies to other services

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Usage

### API Endpoints

See the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/api/2281/notes
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

```yaml
server:
  port: 8080
```

- **Database Settings**

```yaml
spring:
  datasource:
    url: jdbc:mysql://database_server:port/your_database
    username: your_db_username
    password: your_db_password
  jpa:
    properties:
      jakarta: 
        persistence:
          schema-generation:
            database:
              action: jpa database action on startup (should be set to 'validate' in production env)
```

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
spring:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Code status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-notes&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-notes)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-notes&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-notes)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-notes&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-notes)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-notes&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-notes)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-notes&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-notes)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-notes&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-notes)

---

© 2024 Sundsvalls kommun
