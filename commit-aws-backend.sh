#!/bin/bash

# Make scripts executable
chmod +x setup-aws-backend.sh

# Add files to git
git add .github/workflows/aws-backend-deploy.yml
git add AWS-BACKEND-DEPLOYMENT.md
git add setup-aws-backend.sh

# Commit changes
git commit -m "Add AWS backend deployment configuration"

# Push changes (uncomment to push automatically)
# git push

echo "AWS backend deployment configuration has been committed."
echo "Run 'git push' to push the changes to your repository."
echo ""
echo "To set up AWS infrastructure, run:"
echo "./setup-aws-backend.sh"