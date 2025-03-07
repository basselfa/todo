#!/bin/bash

# Script to commit Netlify deployment fixes
echo "Committing Netlify deployment fixes..."

# Add the modified files
git add .github/workflows/netlify-deploy.yml
git add todo-frontend/fix-npm-ci.sh

# Commit the changes
git commit -m "Fix Netlify deployment workflow and npm ci issues

- Changed npm ci to npm install in GitHub workflow to fix dependency issues
- Changed Netlify action from nwtgck/actions-netlify@v2 to netlify/actions/cli@v1
- Added script to fix npm ci issues by updating package-lock.json"

echo "Changes committed successfully. You can push them to your repository with 'git push'"
echo "Then run 'bash todo-frontend/fix-npm-ci.sh' to update the package-lock.json file."