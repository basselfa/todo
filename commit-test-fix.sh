#!/bin/bash

# Add the updated package.json file
git add todo-frontend/package.json

# Commit the changes
git commit -m \"Add missing testing libraries to fix Jest tests\"

echo \"Changes committed successfully. Run 'git push' to push the changes to your repository.\"