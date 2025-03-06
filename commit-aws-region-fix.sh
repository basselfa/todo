#!/bin/bash

# Add the modified workflow file
git add .github/workflows/aws-backend-deploy.yml

# Commit the changes
git commit -m "Fix AWS region configuration in GitHub workflow"

echo "Changes committed. Push with 'git push' to apply them."