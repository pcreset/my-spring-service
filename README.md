# my-spring-service

A Spring Boot 3 REST API service built with Java 21. Includes a REST API for item management, an Excel file loader that reads from a local data directory, and a React UI with AG Grid for data exploration.

## Prerequisites
- Java 21+
- Maven 3.8+
- Node.js v18+ (for the React UI)

## Running Locally

### Spring Boot API
```powershell
cd MySpringService
mvn spring-boot:run
```
API starts on **http://localhost:8080** using the `local` profile by default.

### React UI
```powershell
cd my-spring-service-ui
npm install --legacy-peer-deps
npm start
```
UI starts on **http://localhost:3000**

---

## API Endpoints

### General
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/info` | Service info |

### Items
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/items` | List all items |
| GET | `/api/v1/items/{id}` | Get item by ID |
| POST | `/api/v1/items` | Create new item |
| PUT | `/api/v1/items/{id}` | Update item |
| DELETE | `/api/v1/items/{id}` | Delete item |

### File Data (Excel Loader)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/v1/data/files` | List available .xlsx files |
| GET | `/api/v1/data/load?file=name.xlsx` | Load Excel file as JSON |

Place `.xlsx` files in `C:/devtools/MyData` (local) to use the file loader.

---

## Project Structure

```
MySpringService/
├── src/main/java/com/myservice/app/
│   ├── Application.java              # Entry point
│   ├── config/
│   │   └── WebConfig.java            # CORS configuration
│   ├── controller/
│   │   ├── FileDataController.java   # Excel file loader endpoints
│   │   ├── HealthController.java     # Health & info endpoints
│   │   └── ItemController.java       # Item CRUD endpoints
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── model/
│   │   ├── ApiResponse.java          # Standard response envelope
│   │   └── Item.java
│   └── service/
│       ├── FileDataService.java      # Excel reading logic (Apache POI)
│       └── ItemService.java          # Item business logic (in-memory)
├── src/main/resources/
│   ├── application.properties        # Common config, sets active profile
│   ├── application-local.properties  # Local dev settings
│   └── application-prod.properties   # Production settings
└── src/test/
    ├── FileDataControllerTest.java
    └── ItemControllerTest.java

my-spring-service-ui/
├── src/
│   ├── App.jsx                       # Main React component with AG Grid
│   ├── App.css
│   └── index.jsx
├── index.html
├── vite.config.js
└── package.json
```

---

## Spring Profiles

| Profile | Usage |
|---------|-------|
| `local` | Default — uses `C:/devtools/MyData`, verbose logging |
| `prod` | Azure/cloud — reads config from environment variables |

### Running with production profile
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Key environment variables for production
| Variable | Description |
|----------|-------------|
| `APP_DATA_DIRECTORY` | Path to Excel files on the server |
| `ALLOWED_ORIGINS` | Frontend URL for CORS |

---

## Running Tests
```powershell
mvn test
```

## Build a deployable JAR
```powershell
mvn clean package
java -jar target/my-spring-service-1.0.0-SNAPSHOT.jar
```

---

## Roadmap
- [ ] Replace in-memory `ItemService` with Spring Data JPA + database
- [ ] Add Spring Security with JWT authentication
- [ ] Add Swagger/OpenAPI docs (`/swagger-ui.html`)
- [ ] Add Docker support
- [ ] Deploy to Azure App Service
- [ ] Build Claude AI Agent integration
