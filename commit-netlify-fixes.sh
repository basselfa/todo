#!/bin/bash

# Script to commit Netlify deployment fixes
echo "Committing Netlify deployment fixes..."

# Add the modified files
git add todo-frontend/netlify.toml
git add todo-frontend/public/_redirects
git add .github/workflows/netlify-deploy.yml

# Commit the changes
git commit -m "Fix Netlify deployment configuration

- Fix syntax error in netlify.toml (removed extra double quote)
- Correct HTTP status code in _redirects file (changed from 2000 to 200)
- Update GitHub workflow to use specific version of Netlify CLI (netlify/actions/cli@v1)
- Ensure _redirects file is copied to build directory"

echo "Changes committed successfully. You can push them to your repository with 'git push'""