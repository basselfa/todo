#!/bin/bash

# Add all changes
git add /home/basselfa/todo/todo-backend/src/main/java/com/todo/config/WebConfig.java
git add /home/basselfa/todo/todo-backend/src/main/resources/application.properties
git add /home/basselfa/todo/todo-backend/src/main/resources/keystore.p12
git add /home/basselfa/todo/todo-frontend/public/config.js

# Commit the changes
git commit -m "Fix CORS and HTTPS configuration

1. Added CORS configuration to allow requests from Netlify
2. Configured backend to use HTTPS with self-signed certificate
3. Updated frontend to use HTTPS API URL
4. Fixed strict-origin-when-cross-origin referrer policy issues"

echo "Changes committed successfully!"