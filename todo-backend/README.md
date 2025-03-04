# Todo Application with Nginx Proxy

This project consists of a Spring Boot backend and a React frontend, connected via an Nginx proxy.

## Setup Overview

1. **Backend (Spring Boot)**
   - Running on port 8080
   - API endpoints are accessible under the `/api` context path
   - PostgreSQL database connection configured

2. **Frontend (React)**
   - Built with relative API URLs (`/api/tasks` instead of absolute URLs)
   - Static files served by Nginx on port 3000

3. **Nginx Proxy**
   - Serves React frontend on port 3000
   - Forwards all `/api/*` requests to the Spring Boot backend

## Running the Application

1. **Start the Spring Boot backend**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Build the React frontend**
   ```bash
   cd frontend
   npm run build
   ```

3. **Start Nginx with the proxy configuration**
   ```bash
   nginx -c /path/to/nginx-proxy.conf
   ```

4. **Access the application**
    - Open your browser and navigate to `https://localhost:3000`

## Using ngrok for External Access

To make your application accessible from the internet:

```bash
ngrok http 3000
```

This will create a public URL that forwards to your local Nginx server on port 3000.