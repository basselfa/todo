# AWS Backend Deployment Guide

This guide explains how to deploy the Todo application backend to AWS using GitHub Actions and AWS CodeDeploy.

## Prerequisites

1. AWS Account with appropriate permissions
2. GitHub repository with the Todo application code
3. AWS services set up:
   - S3 bucket for deployment artifacts
   - EC2 instance(s) for hosting the application
   - CodeDeploy application and deployment group
   - IAM roles and policies for CodeDeploy and EC2

## GitHub Secrets Required

Add the following secrets to your GitHub repository:

- `AWS_ACCESS_KEY_ID`: Your AWS access key
- `AWS_SECRET_ACCESS_KEY`: Your AWS secret key
- `AWS_REGION`: The AWS region (e.g., us-east-1)
- `AWS_S3_BUCKET`: The name of your S3 bucket for deployment artifacts

## AWS Resources Setup

### 1. Create an S3 Bucket

```bash
aws s3 mb s3://your-deployment-bucket-name --region your-region
```

### 2. Create IAM Roles

Create a service role for CodeDeploy:

```bash
aws iam create-role --role-name CodeDeployServiceRole --assume-role-policy-document file://codedeploy-trust.json
aws iam attach-role-policy --role-name CodeDeployServiceRole --policy-arn arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole
```

Create an instance profile for EC2:

```bash
aws iam create-role --role-name EC2CodeDeployRole --assume-role-policy-document file://ec2-trust.json
aws iam attach-role-policy --role-name EC2CodeDeployRole --policy-arn arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess
aws iam create-instance-profile --instance-profile-name EC2CodeDeployProfile
aws iam add-role-to-instance-profile --instance-profile-name EC2CodeDeployProfile --role-name EC2CodeDeployRole
```

### 3. Launch EC2 Instance

Launch an EC2 instance with the following:

- Amazon Linux 2 or Ubuntu
- Instance profile: EC2CodeDeployProfile
- User data script to install CodeDeploy agent

### 4. Create CodeDeploy Application and Deployment Group

```bash
aws deploy create-application --application-name todo-app
aws deploy create-deployment-group \
  --application-name todo-app \
  --deployment-group-name todo-backend-group \
  --deployment-config-name CodeDeployDefault.OneAtATime \
  --ec2-tag-filters Key=Name,Value=todo-backend,Type=KEY_AND_VALUE \
  --service-role-arn arn:aws:iam::your-account-id:role/CodeDeployServiceRole
```

## Application Structure

The deployment package includes:

- `app.jar`: The Spring Boot application
- `appspec.yml`: CodeDeploy configuration
- `scripts/`: Deployment scripts
  - `before_install.sh`: Prepares the environment
  - `after_install.sh`: Configures the application
  - `start_application.sh`: Starts the application
  - `stop_application.sh`: Stops the application

## Deployment Process

1. GitHub Actions workflow is triggered on push to main branch
2. The workflow builds the Spring Boot application
3. Creates a deployment package with the JAR file and scripts
4. Uploads the package to S3
5. Triggers a CodeDeploy deployment
6. CodeDeploy deploys the application to the EC2 instance(s)

## Troubleshooting

- Check GitHub Actions workflow logs
- Check CodeDeploy deployment logs on the EC2 instance at `/var/log/aws/codedeploy-agent/`
- Check application logs at `~/app/logs/app.log`

## Manual Deployment

You can also deploy manually using the AWS CLI:

```bash
aws deploy create-deployment \
  --application-name todo-app \
  --deployment-group-name todo-backend-group \
  --deployment-config-name CodeDeployDefault.OneAtATime \
  --s3-location bucket=your-bucket-name,bundleType=zip,key=backend-deployment.zip \
  --file-exists-behavior OVERWRITE
```