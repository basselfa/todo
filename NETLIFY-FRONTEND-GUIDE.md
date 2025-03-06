# Netlify Deployment Guide for Todo Frontend

This guide explains how to set up and use the Netlify deployment workflow for the Todo application frontend.

## What's Included

The GitHub Actions workflow in `.github/workflows/netlify-deploy.yml` automatically builds and deploys your React frontend to Netlify whenever changes are pushed to the `todo-frontend` directory.

## Prerequisites

Before you can use this workflow, you need to:

1. Create a Netlify account
2. Set up a new site on Netlify
3. Generate an API token
4. Add required secrets to your GitHub repository

## Step-by-Step Setup

### 1. Create a Netlify Account

If you don't already have one, sign up at [netlify.com](https://www.netlify.com/).

### 2. Create a New Site on Netlify

You can do this manually first:

1. Log in to your Netlify dashboard
2. Click "New site from Git"
3. Connect to GitHub and select your repository
4. Configure the build settings:
   - Build command: `npm run build`
   - Publish directory: `build`
5. Click "Deploy site"

This creates your site, but future deployments will be handled by GitHub Actions.

### 3. Get Your Netlify API Credentials

To allow GitHub Actions to deploy to your Netlify site:

1. Go to your Netlify user settings (click your avatar, then User settings)
2. Navigate to "Applications"
3. Under "Personal access tokens", click "New access token"
4. Give it a name like "GitHub Actions" and create the token
5. **Copy the token immediately** - you won't be able to see it again!
6. Go to your site settings in Netlify and copy the "API ID" (Site ID)

### 4. Add Secrets to GitHub Repository

1. Go to your GitHub repository
2. Navigate to Settings > Secrets and variables > Actions
3. Add these secrets:
   - `NETLIFY_AUTH_TOKEN`: Your personal access token
   - `NETLIFY_SITE_ID`: Your Netlify site API ID

## How It Works

The workflow:

1. Runs when changes are pushed to the `todo-frontend` directory
2. Sets up Node.js and installs dependencies
3. Builds the React application
4. Deploys the build to Netlify
5. Adds deployment comments to PRs and commits

## Key Features

- **Path-based triggers**: Only runs when frontend code changes
- **PR previews**: Creates preview deployments for pull requests
- **Caching**: Uses npm caching to speed up builds
- **Working directory**: Sets the working directory to `todo-frontend`

## Troubleshooting

If deployments fail, check:

1. GitHub Action logs for build errors
2. Netlify site settings to ensure API credentials are correct
3. That the `NETLIFY_AUTH_TOKEN` and `NETLIFY_SITE_ID` secrets are set correctly
4. That your React build is working locally with `npm run build`

## Additional Configuration

You can customize the deployment by:

1. Changing environment variables in the workflow file
2. Adding custom domains in Netlify settings
3. Configuring redirects with a `_redirects` file in the `public` folder
4. Setting up custom headers with a `netlify.toml` file

## Resources

- [Netlify Documentation](https://docs.netlify.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [nwtgck/actions-netlify](https://github.com/nwtgck/actions-netlify) - The action used for deployment