# Netlify Deployment Troubleshooting Guide

This guide helps you resolve common issues with Netlify deployments using GitHub Actions.

## Common Issues and Solutions

### 1. Authentication Problems

If you see errors related to authentication:

1. **Check your Netlify API token**:
   - Go to Netlify user settings (click your avatar → User settings)
   - Navigate to Applications → Personal access tokens
   - Create a new token if needed
   - Copy the token and add it as a GitHub secret named `NETLIFY_AUTH_TOKEN`

2. **Verify your site ID**:
   - Go to your site in Netlify
   - Navigate to Site settings → Site information
   - Copy the API ID (not the site name)
   - Add it as a GitHub secret named `NETLIFY_SITE_ID`

### 2. Build Failures

If your build is failing:

1. **React build errors**:
   - We've already set `CI=false` in the workflow to prevent warnings being treated as errors
   - Check the build logs for specific error messages

2. **Node.js version issues**:
   - The workflow uses Node.js 18
   - If your app requires a different version, update the version in the workflow file

3. **Memory issues**:
   - We've increased the Node.js memory limit to 4GB
   - If you still have memory issues, consider optimizing your app or further increasing the limit

### 3. Routing Issues

If your site loads the home page but shows 404 errors when directly accessing other routes:

1. **Check the _redirects file**:
   - Make sure `todo-frontend/public/_redirects` exists with this content:
     ```
     /*    /index.html   200
     ```

2. **Check netlify.toml**:
   - Make sure it includes the redirects configuration:
     ```toml
     [[redirects]]
       from = "/*"
       to = "/index.html"
       status = 200
     ```

### 4. Deployment Directory Issues

If Netlify can't find your build files:

1. **Check the build directory**:
   - Make sure your React app builds to `todo-frontend/build`
   - Verify this matches the `publish-dir` in the workflow

2. **Check the build command**:
   - Make sure `npm run build` works locally
   - If you use a different build command, update it in the workflow

## Manual Deployment

If GitHub Actions deployment isn't working, try deploying manually:

1. Install the Netlify CLI:
   ```bash
   npm install -g netlify-cli
   ```

2. Login to Netlify:
   ```bash
   netlify login
   ```

3. Build your app:
   ```bash
   cd todo-frontend
   npm run build
   ```

4. Deploy manually:
   ```bash
   netlify deploy --dir=build --prod
   ```

## Need More Help?

- Check the [Netlify documentation](https://docs.netlify.com/)
- Review the [GitHub Actions logs](https://github.com/YOUR_USERNAME/todo/actions)
- Contact Netlify support at https://www.netlify.com/support/