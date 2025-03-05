#!/bin/bash
# Start application script for GitHub Actions deployment

# Start Nginx service
sudo systemctl start nginx
sudo systemctl enable nginx

# Find the Spring Boot jar file
JAR_FILE=$(find ~/app/current -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: No JAR file found in ~/app/current"
    exit 1
fi

# Create directory for logs if it doesn't exist
mkdir -p ~/app/logs

# Start the Spring Boot application
nohup java -jar $JAR_FILE \
    --spring.profiles.active=prod \
    --server.port=8080 \
    > ~/app/logs/app.log 2>&1 &

# Wait for application to start
sleep 10

# Check if application is running
if pgrep -f "java -jar $JAR_FILE" > /dev/null; then
    echo "Application started successfully"
else
    echo "ERROR: Application failed to start"
    exit 1
fi

echo "Application deployment completed successfully"