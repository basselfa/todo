#!/bin/bash

# Add all the created files
git add deployment/scripts/deploy.sh
git add deployment/scripts/setup.sh
git add deployment/scripts/rollback.sh
git add deployment/scripts/health-check.sh
git add .github/workflows/build-deploy.yml

# Commit the changes
git commit -m "Fix deployment scripts and GitHub workflow"

echo "Changes committed successfully. You can now push with 'git push'."