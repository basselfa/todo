# Netlify Deployment Troubleshooting Guide

This guide helps troubleshoot common issues with Netlify deployments for this React application.

## Common Issues and Solutions

### 1. OpenSSL Issues with Node.js

**Symptom:** Error messages containing `error:0308010C:digital envelope routines::unsupported`

**Solution:**
- The application uses React Scripts 4.0.3 which is not fully compatible with Node.js 18+
- We've implemented these fixes:
  - Added `--openssl-legacy-provider` flag to NODE_OPTIONS
  - Created an .npmrc file with the same setting
  - Added a build:ci script in package.json
  - Set Node.js version to 16.14.0 in .nvmrc

### 2. Client-Side Routing Issues

**Symptom:** Routes work on initial load but show 404 when refreshed

**Solution:**
- Ensure the `_redirects` file in the public directory contains:
  ```
  /*    /index.html   200
  ```
- Check netlify.toml for proper redirect rules

### 3. Build Directory Issues

**Symptom:** Netlify can't find the build output

**Solution:**
- Verify the correct publish directory in netlify.toml
- Check that the GitHub Actions workflow is building to the correct directory
- Run a debug step to list the contents of the build directory

### 4. Node.js Version Compatibility

**Symptom:** Unexpected build errors related to Node.js features

**Solution:**
- Use Node.js 16.x for this project (we've set 16.14.0 specifically)
- Update the .nvmrc file if you need a different version
- Make sure the GitHub Actions workflow uses the same Node version

### 5. Environment Variable Issues

**Symptom:** Features dependent on environment variables don't work

**Solution:**
- Check environment variables in netlify.toml
- Add required variables to the GitHub Actions workflow
- For sensitive variables, use Netlify UI and GitHub Secrets

## Verifying a Successful Deployment

After deployment, check:

1. The Netlify deployment logs for any errors
2. That client-side routing works by navigating to a route and refreshing
3. That all features dependent on environment variables work correctly

## Getting Help

If you continue to experience issues:

1. Check the Netlify deploy logs in the Netlify dashboard
2. Review the GitHub Actions workflow run logs
3. Look for specific error messages and search for solutions
4. Consult the Netlify documentation: https://docs.netlify.com/
