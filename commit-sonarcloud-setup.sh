#!/bin/bash

# Script to commit SonarCloud integration files

echo "Adding SonarCloud configuration files to git..."
git add sonar-project.properties
git add .github/workflows/netlify-deploy.yml
git add SONARCLOUD-SETUP.md

echo "Committing changes..."
git commit -m "Add SonarCloud integration to Netlify CI/CD pipeline"

echo "Changes committed successfully!"
echo ""
echo "Next steps:"
echo "1. Push changes to your repository: git push"
echo "2. Set up the required GitHub secrets (SONAR_TOKEN, SONAR_ORGANIZATION, SONAR_PROJECT_KEY)"
echo "3. See SONARCLOUD-SETUP.md for detailed instructions"