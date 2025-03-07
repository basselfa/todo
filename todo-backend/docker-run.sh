#!/bin/bash

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Building Todo Backend Docker Image...${NC}"
docker build -t todo-backend .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}Build successful!${NC}"
    echo -e "${YELLOW}Running container...${NC}"
    docker run -p 8080:8080 -v $(pwd)/data:/app/data todo-backend
else
    echo -e "${RED}Build failed!${NC}"
    exit 1
fi