#!/bin/bash
# After install script for GitHub Actions deployment

# Make all scripts executable
chmod +x ~/app/deployment/scripts/*.sh

# Copy application files to the right locations
cp -r ~/app/deployment/frontend/* ~/app/current/static/
cp ~/app/deployment/app.jar ~/app/current/

# Configure Nginx to serve static files and proxy API requests
cat > ~/app/nginx-config.conf << 'EOL'
server {
    listen 80;
    server_name _;

    # Static frontend files
    location / {
        root ~/app/current/static;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # API proxy
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOL

# Install Nginx configuration
sudo cp ~/app/nginx-config.conf /etc/nginx/conf.d/todo-app.conf

# Ensure proper permissions
chmod +x ~/app/current/*.jar

echo "After Install completed"