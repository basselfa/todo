# Spring Boot Backend Deployment to AWS

This repository contains a GitHub Actions workflow for deploying the Spring Boot backend application to AWS Elastic Beanstalk.

## Overview

The deployment workflow automatically builds and deploys your Spring Boot backend application to AWS Elastic Beanstalk whenever changes are pushed to the `todo-backend` directory in the main branch. This ensures that your backend is continuously deployed without affecting the frontend deployment.

## Files Included

- `.github/workflows/aws-backend-deploy.yml`: The GitHub Actions workflow file for backend deployment
- `AWS-BACKEND-DEPLOY-GUIDE.md`: Detailed guide on setting up AWS resources and using the workflow
- `setup-backend-aws.sh`: Helper script to create AWS resources needed for deployment

## Getting Started

### Prerequisites

1. AWS account with appropriate permissions
2. GitHub repository with your Spring Boot application
3. Basic knowledge of AWS services and GitHub Actions

### Setup Steps

1. **Make the setup script executable**:
   ```bash
   chmod +x setup-backend-aws.sh
   ```

2. **Run the setup script**:
   ```bash
   ./setup-backend-aws.sh
   ```
   
   This script will:
   - Create necessary IAM roles and policies
   - Create an Elastic Beanstalk application
   - Create an Elastic Beanstalk environment
   - Create an IAM user for GitHub Actions
   - Output the credentials needed for GitHub secrets

3. **Add the required secrets to your GitHub repository**:
   - `AWS_ACCESS_KEY_ID`: The IAM user Access Key ID
   - `AWS_SECRET_ACCESS_KEY`: The IAM user Secret Access Key
   - `AWS_REGION`: The AWS region (e.g., us-east-1)
   - `AWS_EB_APPLICATION_NAME`: Your Elastic Beanstalk application name
   - `AWS_EB_ENVIRONMENT_NAME`: Your Elastic Beanstalk environment name

4. **Push changes to your backend code**:
   The workflow will automatically trigger when changes are pushed to the `todo-backend` directory in the main branch.

## How It Works

1. **Build Process**:
   - The workflow checks out your code
   - Sets up JDK 17
   - Builds the application using Maven
   - Creates a deployment package with the JAR file and deployment scripts

2. **Deployment Process**:
   - Configures AWS credentials
   - Deploys the application to Elastic Beanstalk
   - Verifies the deployment health

3. **Monitoring**:
   - The workflow includes a post-deployment verification step
   - You can monitor the deployment in the GitHub Actions tab
   - More detailed monitoring is available in the AWS Elastic Beanstalk console

## Customization

You can customize the workflow by:

1. **Modifying the build command**:
   - Add or remove Maven options
   - Include additional build steps

2. **Changing deployment parameters**:
   - Adjust the wait time for environment recovery
   - Modify the deployment package structure

3. **Adding environment variables**:
   - Configure application properties through AWS Elastic Beanstalk environment properties

## Troubleshooting

If you encounter issues:

1. Check the GitHub Actions logs for error messages
2. Verify your AWS credentials and permissions
3. Check the AWS Elastic Beanstalk console for environment health and logs
4. See the detailed troubleshooting section in `AWS-BACKEND-DEPLOY-GUIDE.md`

## Resources

- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/)
- [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)