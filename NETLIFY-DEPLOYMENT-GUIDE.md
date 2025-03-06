# Netlify Deployment Guide

This guide explains how to set up automatic deployments from GitHub to Netlify using GitHub Actions.

## Prerequisites

1. A GitHub repository with your React application
2. A Netlify account (free tier is sufficient)

## Steps to Set Up Netlify Deployment

### 1. Create a Netlify Site

1. Log in to your [Netlify account](https://app.netlify.com/)
2. Click on "New site from Git"
3. Select GitHub as your Git provider
4. Authenticate with GitHub and select your repository
5. Configure build settings:
   - Build command: `npm run build`
   - Publish directory: `build`
6. Click on "Deploy site"

### 2. Get Your Netlify API Credentials

1. Go to your Netlify user settings (click on your avatar → User settings)
2. Navigate to "Applications"
3. Scroll down to "Personal access tokens"
4. Click "New access token"
5. Give it a name (e.g., "GitHub Actions")
6. Copy the generated token immediately (you won't be able to see it again)

### 3. Get Your Netlify Site ID

1. Go to your site settings in Netlify
2. Navigate to "Site information"
3. Copy the "API ID" value

### 4. Add Secrets to Your GitHub Repository

1. Go to your GitHub repository
2. Navigate to "Settings" → "Secrets and variables" → "Actions"
3. Add the following secrets:
   - Name: `NETLIFY_AUTH_TOKEN`
   - Value: [Your Netlify personal access token]

   - Name: `NETLIFY_SITE_ID`
   - Value: [Your Netlify site API ID]

### 5. Push Changes to Trigger Deployment

The workflow is already set up in `.github/workflows/netlify-deploy.yml`. Any push to the main branch will trigger a deployment to Netlify.

## How It Works

1. When you push to the main branch, GitHub Actions runs the workflow
2. The workflow:
   - Checks out your code
   - Sets up Node.js
   - Installs dependencies
   - Builds your project
   - Deploys to Netlify

## Additional Configuration Options

### Environment Variables

If your application needs environment variables:

1. Go to your site settings in Netlify
2. Navigate to "Build & deploy" → "Environment"
3. Add your environment variables

### Custom Domain

To set up a custom domain:

1. Go to your site settings in Netlify
2. Navigate to "Domain management"
3. Click on "Add custom domain"
4. Follow the instructions to configure your domain

## Troubleshooting

If your deployment fails:

1. Check the GitHub Actions logs for build errors
2. Verify that your Netlify secrets are correctly set up
3. Make sure your build command and publish directory are correct
4. Check if your application builds locally with `npm run build`

## Resources

- [Netlify Documentation](https://docs.netlify.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Netlify GitHub Action](https://github.com/nwtgck/actions-netlify)