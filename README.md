# Auctions Application

A REST API for managing auctions in an electronic marketplace, built with Spring Boot.

## Features

- User Authentication and Authorization
- Lot Management with Photo Upload
- Auction Management
- Bid Management
- Role-based Access Control (Admin/User)
- API Documentation with OpenAPI/Swagger
- Secure File Storage
- Comprehensive Test Coverage
- Docker Support

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI/Swagger
- Docker & Docker Compose
- Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (if running locally)
- Flyway 11.3.1

## Quick Start

### Using Docker Compose (Recommended)

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/auctions-application.git
   cd auctions-application
   ```

2. Start the application:
   ```bash
   docker-compose up
   ```

The application will be available at `http://localhost:8080/api`
Swagger UI will be available at `http://localhost:8080/api/swagger-ui.html`

### Local Development

1. Create the database:
   ```bash
   createdb auctions_db
   ```

1. Initialize the database:
   ```bash
   flyway -configFiles=db/conf/flyway.dev.conf baseline migrate
   ```
   
2. Update application-dev.yml with your database credentials if different from defaults.

3. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## Default Admin User

On first startup, the application automatically creates a default admin user:

- Username: `admin`
- Password: `admin123`
- Email: `admin@auctions.com`

You can use these credentials to:
1. Access the API
2. Create additional users
3. Create and manage lots 
4. Create and manage auctions 
5. Create and manage bids

For security reasons, it's recommended to:
1. Change the default admin password after first login
2. Create new admin users for production use

## API Documentation

The API documentation is available through Swagger UI when running in development mode:
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api/v3/api-docs`

### Main Endpoints

#### Authentication
- POST `/api/auth/login` - Authenticate user

#### Users (Admin only)
- GET `/api/admin/users` - List all users
- GET `/api/admin/users/{id}` - Get user by ID
- POST `/api/admin/users` - Create user
- PUT `/api/admin/users/{id}` - Update user
- DELETE `/api/admin/users/{id}` - Delete user

#### Lots
- GET `/api/lots` - List all lots
- GET `/api/lots/{id}` - Get lot by ID
- POST `/api/lots` - Create lot
- GET `/api/lots/{id}/photo` - Get lot photo by ID
- POST `/api/lots/{id}/photo` - Set lot photo
- PUT `/api/lots/{id}` - Update lot
- DELETE `/api/lots/{id}` - Delete lot

#### Auctions
- GET `/api/auctions` - List all auctions
- GET `/api/auctions/{id}` - Get auction by ID
- POST `/api/auctions` - Create auction
- PUT `/api/auctions/{id}` - Update auction
- DELETE `/api/auctions/{id}` - Delete auction

#### Bids
- POST `/api/bids` - Create bid
- DELETE `/api/bids/{id}` - Delete bid

## Configuration

### Environment Variables

#### Required for Production
- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing key (min 32 characters)

#### Optional
- `SPRING_PROFILES_ACTIVE` - Application profile (dev/prod, default: dev)
- `APP_FILE_STORAGE_LOCATION` - Upload directory (default: uploads)
- `JWT_EXPIRATION` - Token expiration in ms (default: 86400000)

### Application Profiles

#### Development (dev)
- Enhanced logging
- Swagger UI enabled
- H2 Console enabled
- Stack traces in error responses
- Automatic schema updates

#### Production (prod)
- Minimal logging
- Swagger UI disabled
- Security headers enabled
- No stack traces in responses
- Manual schema management

## Security

- JWT-based authentication
- Role-based authorization
- Password encryption using BCrypt
- CORS configuration
- XSS protection
- SQL injection prevention
- File upload restrictions

## Testing

Run the test suite:
```bash
mvn test
```

The project includes:
- Unit tests for services
- Integration tests for controllers
- Security tests
- File upload tests
- Functional tests

## Production Deployment

1. Build the Docker image:
   ```bash
   docker build -t auctions-application .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/auctions_db \
     -e SPRING_DATASOURCE_USERNAME=your-username \
     -e SPRING_DATASOURCE_PASSWORD=your-password \
     -e JWT_SECRET=your-production-secret \
     -e SPRING_PROFILES_ACTIVE=prod \
     auctions-application
   ```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/auctions/
│   │       ├── config/         # Configuration classes
│   │       ├── domain/         # Domain objects
│   │       ├── exception/      # Exception handling
│   │       ├── mapper/         # Mappers
│   │       ├── persistence/    # Data access
│   │       ├── security/       # Security config
│   │       ├── service/        # Business logic
│   │       └── web/            # REST controllers and API DTOs
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
└── test/
    └── java/
        └── com/auctions/
            ├── config/        # Test configuration
            ├── functional/    # Functional tests
            ├── persistence/   # Persistence tests
            ├── service/       # Service tests
            ├── util/          # Test utilities
            └── web/           # Web layer tests
```

## Authentication

### Login

To access the API endpoints, you first need to obtain a JWT token by logging in:

```bash
# Using cURL
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your-username",
    "password": "your-password"
  }'
```

The response will contain your JWT token:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "your-username",
  "isAdmin": false
}
```

Use this token in subsequent requests in the Authorization header:
```bash
# Example: Get all customers
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Using Postman

1. Send a POST request to `http://localhost:8080/api/auth/login`
2. Set the request body to raw JSON:
   ```json
   {
     "username": "your-username",
     "password": "your-password"
   }
   ```
3. Copy the token from the response
4. For subsequent requests, add an Authorization header:
   - Type: Bearer Token
   - Token: Your JWT token

### Register (Optional)

To create a new user account:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new-user",
    "password": "password123",
    "email": "user@example.com"
  }'
```