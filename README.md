# my-spring-service

A Spring Boot 3 REST API service built with Java 21.

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+

### Run locally
```bash
./mvnw spring-boot:run
```

The API will start on **http://localhost:8080**

---

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/info` | Service info |
| GET | `/api/v1/items` | List all items |
| GET | `/api/v1/items/{id}` | Get item by ID |
| POST | `/api/v1/items` | Create new item |
| PUT | `/api/v1/items/{id}` | Update item |
| DELETE | `/api/v1/items/{id}` | Delete item |

### Example: Create an item
```bash
curl -X POST http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -d '{"name": "My First Item", "description": "Hello world"}'
```

### Example: List all items
```bash
curl http://localhost:8080/api/v1/items
```

---

## Project Structure

```
src/
├── main/java/com/myservice/app/
│   ├── Application.java          # Entry point
│   ├── controller/               # REST controllers
│   │   ├── ItemController.java
│   │   └── HealthController.java
│   ├── service/                  # Business logic
│   │   └── ItemService.java
│   ├── model/                    # Domain models & DTOs
│   │   ├── Item.java
│   │   └── ApiResponse.java
│   ├── exception/                # Error handling
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   └── config/                   # Configuration
│       └── WebConfig.java
└── test/                         # Unit & integration tests
```

---

## Running Tests
```bash
./mvnw test
```

## Build a JAR (deployable artifact)
```bash
./mvnw clean package
java -jar target/my-spring-service-1.0.0-SNAPSHOT.jar
```

---

## Next Steps
- Replace the in-memory store in `ItemService` with Spring Data JPA + a real database
- Add Spring Security for authentication
- Add API documentation with SpringDoc/OpenAPI (`/swagger-ui.html`)
- Add Docker support with a `Dockerfile`
