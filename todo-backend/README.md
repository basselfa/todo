# Todo Application Backend

This project consists of a Spring Boot backend that provides a REST API for a Todo application.

## Setup Overview

1. **Backend (Spring Boot)**
   - Running on port 8080
   - API endpoints are accessible under the `/api` context path
   - H2 database by default, with PostgreSQL configuration available

## Running with Docker

### Building the Docker Image

```bash
cd todo-backend
docker build -t todo-backend .
```

### Running the Container

```bash
docker run -p 8080:8080 -v $(pwd)/data:/app/data todo-backend
```

### Using Docker Compose

```bash
docker-compose up -d
```

## Running Without Docker

1. **Start the Spring Boot backend**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Access the application**
   - API is available at `http://localhost:8080/api`
   - H2 Console is available at `http://localhost:8080/api/h2-console`

## API Endpoints

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `DELETE /api/tasks/{id}` - Delete a task

## Database Configuration

### H2 Database (Default)
The application uses an H2 file-based database by default, stored in the `data` directory.

### PostgreSQL Configuration
To use PostgreSQL instead of H2, uncomment the PostgreSQL configuration in:
1. `application.properties`
2. `docker-compose.yml` if using Docker

## Using ngrok for External Access

To make your application accessible from the internet:

```bash
ngrok http 8080
```

This will create a public URL that forwards to your backend server on port 8080..