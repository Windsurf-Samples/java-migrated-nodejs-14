# Reaction Commerce Java API

A modern Java implementation of the Reaction Commerce headless e-commerce platform, built with Spring Boot, MongoDB, and GraphQL.

## Features

- **Headless E-commerce**: Complete API-first e-commerce platform
- **GraphQL API**: Modern GraphQL API with real-time subscriptions
- **Reactive Programming**: Built with Spring WebFlux for high performance
- **MongoDB**: Document-based database with reactive drivers
- **Plugin Architecture**: Modular and extensible design
- **Docker Support**: Containerized for easy deployment
- **Security**: JWT-based authentication and authorization
- **Testing**: Comprehensive test suite with Testcontainers

## Tech Stack

- **Java 17** - Modern Java with latest features
- **Spring Boot 3.2** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Data MongoDB Reactive** - Reactive MongoDB integration
- **Spring GraphQL** - GraphQL implementation
- **Spring Security** - Security framework
- **MongoDB 7.0** - Document database
- **Maven** - Build tool
- **Docker** - Containerization

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- MongoDB (if running locally)

### Running with Docker (Recommended)

1. Clone the repository:
```bash
git clone <repository-url>
cd reaction-commerce-java
```

2. Copy environment file:
```bash
cp .env.example .env
```

3. Start the application:
```bash
docker-compose up -d
```

4. Access the application:
- GraphQL API: http://localhost:3000/graphql
- GraphiQL UI: http://localhost:3000/graphiql
- Health Check: http://localhost:3000/actuator/health

### Running Locally

1. Start MongoDB:
```bash
docker-compose -f docker-compose.dev.yml up -d
```

2. Run the application:
```bash
mvn spring-boot:run
```

### Development Mode

For development with auto-reload:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

## API Documentation

### GraphQL Endpoints

- **Query Endpoint**: `POST /graphql`
- **GraphiQL UI**: `GET /graphiql` (development only)

### REST Endpoints

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

## Testing

Run all tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn test -Dtest="**/*IntegrationTest"
```

## Building

Build the application:
```bash
mvn clean package
```

Build Docker image:
```bash
docker build -t reaction-commerce-java .
```

## Configuration

The application can be configured through:

1. **Environment Variables** (recommended for production)
2. **application.yml** (for development)
3. **Command line arguments**

See `.env.example` for available environment variables.

## Project Structure

```
src/
├── main/
│   ├── java/com/reactioncommerce/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # GraphQL controllers
│   │   ├── service/         # Business logic services
│   │   ├── repository/      # Data access layer
│   │   ├── model/           # Domain models
│   │   ├── graphql/         # GraphQL resolvers
│   │   ├── security/        # Security configuration
│   │   └── exception/       # Exception handling
│   └── resources/
│       ├── graphql/         # GraphQL schema files
│       ├── static/          # Static resources
│       └── application.yml  # Configuration
└── test/
    ├── java/
    │   ├── integration/     # Integration tests
    │   └── unit/           # Unit tests
    └── resources/          # Test resources
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for your changes
5. Ensure all tests pass
6. Submit a pull request

## Migration from Node.js Version

This Java implementation is designed to be compatible with the existing Node.js Reaction Commerce API. Key migration considerations:

- **Database Schema**: Uses the same MongoDB collections and document structure
- **GraphQL Schema**: Maintains API compatibility with existing clients
- **Plugin System**: Modular architecture similar to the Node.js version
- **Authentication**: Compatible JWT token format

## License

This project is licensed under the GNU General Public License v3.0 - see the LICENSE file for details.

## Support

- Documentation: [Coming Soon]
- Issues: GitHub Issues
- Community: [Coming Soon]
