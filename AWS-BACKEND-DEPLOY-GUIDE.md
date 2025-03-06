# AWS Backend Deployment Guide

This guide explains how to set up and use the automated GitHub Actions workflow to deploy the Spring Boot backend application to AWS Elastic Beanstalk.

## What This Deployment Does

The GitHub Actions workflow `.github/workflows/aws-backend-deploy.yml` will:

1. Build the Spring Boot application using Maven
2. Package it with deployment scripts
3. Deploy it to AWS Elastic Beanstalk
4. Verify the deployment health

## Prerequisites

Before using this workflow, you need to set up the following:

1. An AWS account with appropriate permissions
2. An Elastic Beanstalk application and environment
3. IAM credentials for deployment

## Step 1: Set Up AWS Resources

### Create an Elastic Beanstalk Application and Environment

1. Log in to the AWS Management Console
2. Go to the Elastic Beanstalk service
3. Click "Create application"
4. Enter an application name (e.g., "todo-backend")
5. Create a new environment:
   - Web server environment
   - Platform: Java (Corretto 17)
   - Platform branch: Java 17 running on 64bit Amazon Linux 2
   - Sample application (you'll replace this with your own)
6. Configure service access:
   - Create a new service role or use an existing one
   - Create EC2 instance profile or use an existing one
7. Configure networking, database (if needed), and monitoring options
8. Review and click "Submit"

### Create an IAM User for Deployment

1. Go to the IAM service in AWS Console
2. Click "Users" → "Add users"
3. Enter a name (e.g., "github-actions-deployer")
4. Select "Access key - Programmatic access"
5. Attach the following policies:
   - AWSElasticBeanstalkFullAccess
   - AmazonS3FullAccess (needed for deployment artifacts)
   - CloudWatchLogsFullAccess (for monitoring)
6. Complete the user creation
7. **Important**: Save the Access Key ID and Secret Access Key

## Step 2: Add Secrets to GitHub Repository

1. Go to your GitHub repository
2. Navigate to Settings → Secrets and variables → Actions
3. Add the following secrets:

| Secret Name | Description |
|-------------|-------------|
| `AWS_ACCESS_KEY_ID` | The IAM user Access Key ID |
| `AWS_SECRET_ACCESS_KEY` | The IAM user Secret Access Key |
| `AWS_REGION` | The AWS region (e.g., us-east-1) |
| `AWS_EB_APPLICATION_NAME` | Your Elastic Beanstalk application name |
| `AWS_EB_ENVIRONMENT_NAME` | Your Elastic Beanstalk environment name |

## Step 3: Application Configuration

### Environment Variables

To configure environment-specific settings:

1. Go to your Elastic Beanstalk environment
2. Navigate to Configuration → Software
3. Under "Environment properties", add key-value pairs for:
   - Database connection strings
   - API keys
   - Other environment-specific configuration

### Database Connection

If your application requires a database:

1. Go to your Elastic Beanstalk environment
2. Navigate to Configuration → Database
3. Add a database instance
4. The connection details will be provided as environment variables to your application

## Step 4: Triggering Deployments

The workflow runs automatically when:

1. You push changes to the `main` branch that affect files in the `todo-backend` directory
2. You manually trigger it from the "Actions" tab in GitHub

## Step 5: Monitoring Deployments

1. Go to the "Actions" tab in your GitHub repository
2. Click on the running or completed workflow
3. View the logs for each step
4. After deployment, check the Elastic Beanstalk console for:
   - Environment health
   - Deployment status
   - Logs

## Troubleshooting

### Common Issues

1. **Deployment Fails**:
   - Check the GitHub Actions logs for specific errors
   - Verify your AWS credentials are correct
   - Ensure the Elastic Beanstalk environment exists and is healthy

2. **Application Starts but Doesn't Work**:
   - Check CloudWatch Logs for application errors
   - Verify environment variables are set correctly
   - Check security groups and network settings

3. **Database Connection Issues**:
   - Verify database credentials in environment variables
   - Check security groups allow traffic from your application to the database
   - Ensure the database is running and accessible

### Viewing Logs

1. Go to the CloudWatch service in AWS Console
2. Navigate to Log groups
3. Find the log group for your Elastic Beanstalk environment
4. Check the latest logs for errors or warnings

## Additional Resources

- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/)
- [Spring Boot Deployment Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)