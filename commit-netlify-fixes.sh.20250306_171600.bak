#!/bin/bash

# Script to commit Netlify deployment fixes
echo "Committing Netlify deployment fixes..."

# Add the modified files
git add todo-frontend/netlify.toml
git add todo-frontend/public/_redirects

# Commit the changes
git commit -m "Fix Netlify deployment configuration

- Fix syntax error in netlify.toml (removed extra double quote)
- Correct HTTP status code in _redirects file (changed from 2000 to 200)"

echo "Changes committed successfully. You can push them to your repository with 'git push'""