# Simple AWS Deployment Guide 🚀

This guide explains how to set up CI/CD for your Todo application using GitHub Actions and AWS.

## What You'll Need

1. An AWS account
2. GitHub repository with your code
3. AWS CLI installed and configured on your local machine

## Setup Process

### Step 1: Deploy AWS Infrastructure

Run the deployment script to create all necessary AWS resources:

```bash
chmod +x deploy-simple-aws.sh
./deploy-simple-aws.sh
```

This script will:
- Create an S3 bucket for your frontend
- Create an S3 bucket for your backend deployment files
- Set up a CloudFront distribution for the frontend
- Create an Elastic Beanstalk environment for the backend
- Create an IAM user with necessary permissions
- Output all the required information

### Step 2: Add GitHub Secrets

After running the script, you'll get values for these secrets. Add them to your GitHub repository:

1. Go to your GitHub repository
2. Navigate to Settings > Secrets and variables > Actions
3. Add these secrets:

| Secret Name | Description |
|-------------|-------------|
| `AWS_ACCESS_KEY_ID` | Access key for deployment |
| `AWS_SECRET_ACCESS_KEY` | Secret key for deployment |
| `AWS_REGION` | AWS region (e.g., us-east-1) |
| `AWS_S3_BUCKET` | Frontend S3 bucket name |
| `AWS_S3_BUCKET_BACKEND` | Backend S3 bucket name |
| `AWS_CLOUDFRONT_DISTRIBUTION_ID` | CloudFront distribution ID |

### Step 3: Push Your Code

Once you've set up the AWS resources and GitHub secrets:

1. Commit any changes to your code
2. Push to your main branch
3. Go to the "Actions" tab in your GitHub repository to monitor the deployment

## How It Works

The workflow file (`.github/workflows/aws-simple-deploy.yml`) defines two jobs:

### Frontend Deployment

1. Checks out your code
2. Sets up Node.js
3. Installs dependencies
4. Builds the React application
5. Deploys the build folder to S3
6. Invalidates CloudFront cache

### Backend Deployment

1. Checks out your code
2. Sets up Java
3. Builds the application with Maven
4. Creates a deployment package
5. Uploads the package to S3
6. Deploys to Elastic Beanstalk

## Accessing Your Application

After successful deployment:

- **Frontend**: Access via your CloudFront URL (`https://[distribution-id].cloudfront.net`)
- **Backend**: Access via your Elastic Beanstalk URL (`http://[environment-name].elasticbeanstalk.com`)

## Troubleshooting

If your deployment fails:

1. Check the GitHub Actions logs for error messages
2. Verify that all GitHub secrets are set correctly
3. Ensure your AWS resources are properly configured
4. Check AWS CloudWatch logs for backend errors

## Making Changes

After making changes to your code:

1. Commit and push to the main branch
2. GitHub Actions will automatically deploy the changes

## Cleaning Up

To delete all AWS resources when you're done:

```bash
aws cloudformation delete-stack --stack-name YOUR_STACK_NAME
```

Replace `YOUR_STACK_NAME` with the name you gave your stack during setup.

## Need Help?

If you encounter any issues, check:
- GitHub Actions documentation: https://docs.github.com/en/actions
- AWS documentation: https://docs.aws.amazon.com