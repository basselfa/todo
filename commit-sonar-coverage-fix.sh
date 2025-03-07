#!/bin/bash

# Add the files
git add sonar-project.properties
git add todo-backend/pom.xml
git add .github/workflows/sonarcloud.yml

# Commit the changes
git commit -m "Fix SonarCloud coverage reporting configuration"

echo "Changes committed successfully. Please push to your repository with: git push"