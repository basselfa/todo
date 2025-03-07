# SonarCloud Setup for Frontend Project

This document provides instructions for setting up SonarCloud analysis for your frontend project.

## What is SonarCloud?

SonarCloud is a cloud-based code quality and security service. It performs automatic reviews with static analysis of code to detect bugs, code smells, and security vulnerabilities.

## Setup Instructions

### 1. Create a SonarCloud Account

1. Go to [SonarCloud](https://sonarcloud.io/) and sign up with your GitHub account
2. Create a new organization or use an existing one
3. Create a new project for your repository

### 2. Generate a SonarCloud Token

1. In SonarCloud, go to your user account → Security
2. Generate a new token and save it (you'll need it for GitHub secrets)

### 3. Add Required GitHub Secrets

Add the following secrets to your GitHub repository:

1. Go to your GitHub repository → Settings → Secrets and variables → Actions
2. Add these secrets:
   - `SONAR_TOKEN`: Your SonarCloud token
   - `SONAR_ORGANIZATION`: Your SonarCloud organization key (found in SonarCloud organization settings)
   - `SONAR_PROJECT_KEY`: Your SonarCloud project key (typically in format: `organization_projectname`)

### 4. Understanding the Configuration

The SonarCloud integration includes:

- A `sonar-project.properties` file that configures the analysis
- A GitHub workflow job that runs the analysis before deployment
- Code coverage reporting through Jest

### 5. Viewing Results

After a successful workflow run:

1. Go to your SonarCloud dashboard
2. Select your project
3. Review the analysis results including:
   - Code quality metrics
   - Code coverage
   - Security vulnerabilities
   - Code smells

## Troubleshooting

If you encounter issues with the SonarCloud analysis:

1. Check that all required secrets are correctly set in GitHub
2. Verify the `sonar-project.properties` file paths match your project structure
3. Ensure tests are generating coverage reports in the expected location
4. Check the GitHub Actions workflow logs for specific error messages

## Best Practices

- Aim for at least 80% code coverage
- Address critical and blocker issues promptly
- Set up quality gates in SonarCloud to enforce minimum standards
- Review SonarCloud results regularly as part of your development process