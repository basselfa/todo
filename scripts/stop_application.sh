#!/bin/bash
# Stop application script for GitHub Actions deployment

# Stop Spring Boot application if running
if pgrep -f "java -jar ~/app/current/*.jar" > /dev/null; then
    pkill -f "java -jar ~/app/current/*.jar"
    echo "Application stopped"
else
    echo "No running application found"
fi

# Stop Nginx if running
if systemctl is-active --quiet nginx; then
    sudo systemctl stop nginx
    echo "Nginx stopped"
else
    echo "Nginx not running"
fi

echo "Application stop script completed"