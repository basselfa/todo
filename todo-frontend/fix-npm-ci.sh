#!/bin/bash

# This script fixes the npm ci issue by installing the packages and updating the package-lock.json

echo "Fixing npm ci issue..."

# Navigate to the frontend directory
cd todo-frontend

# Install the packages to update package-lock.json
npm install

# Create a commit to save the updated package-lock.json
cd ..
git add todo-frontend/package-lock.json
git commit -m "Fix: Update package-lock.json to include testing libraries"
git push

echo "Done! package-lock.json has been updated with all required dependencies."