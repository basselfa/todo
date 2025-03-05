#!/bin/bash
# Before install script for GitHub Actions deployment

# Update system packages
if [ -f /etc/debian_version ]; then
    # Debian/Ubuntu
    sudo apt update -y
    sudo apt install -y openjdk-17-jdk nginx
elif [ -f /etc/redhat-release ]; then
    # RHEL/CentOS/Amazon Linux
    sudo yum update -y
    sudo yum install -y java-17-amazon-corretto nginx
fi

# Create directories if they don't exist
mkdir -p ~/app/current
mkdir -p ~/app/static
mkdir -p ~/app/logs

# Clean up previous deployment if exists
if [ -d "~/app/current/static" ]; then
    rm -rf ~/app/current/static/*
fi

# Stop services if running
if systemctl is-active --quiet nginx; then
    sudo systemctl stop nginx
fi

if pgrep -f "java -jar ~/app/current/*.jar" > /dev/null; then
    pkill -f "java -jar ~/app/current/*.jar"
fi

echo "Before Install completed"