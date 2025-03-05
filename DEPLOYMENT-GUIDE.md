# AWS Free Tier CI/CD Deployment Guide

This guide provides step-by-step instructions to set up the CI/CD pipeline for the Todo application on AWS Free Tier.

## Prerequisites

1. AWS Account with Free Tier access
2. AWS CLI installed and configured
3. Git repository for your code

## Step 1: Deploy Infrastructure with CloudFormation

```bash
# Create an EC2 key pair for SSH access
aws ec2 create-key-pair --key-name todo-app-key --query 'KeyMaterial' --output text > todo-app-key.pem
chmod 400 todo-app-key.pem

# Deploy the CloudFormation stack
aws cloudformation create-stack \
  --stack-name todo-app-stack \
  --template-body file://cloudformation-template.yml \
  --parameters \
    ParameterKey=DatabaseUsername,ParameterValue=postgres \
    ParameterKey=DatabasePassword,ParameterValue=YourSecurePassword \
    ParameterKey=DatabaseName,ParameterValue=todo \
    ParameterKey=EnvironmentName,ParameterValue=dev \
  --capabilities CAPABILITY_IAM
```

## Step 2: Set Up CodePipeline

1. Go to AWS Management Console
2. Navigate to CodePipeline service
3. Click "Create pipeline"
4. Enter pipeline name: "todo-app-pipeline"
5. Select "New service role" and allow AWS to create a role
6. Click "Next"

### Source Stage:
1. Source provider: GitHub (Version 2) or AWS CodeCommit
2. Connect to your repository
3. Select your repository and branch
4. Click "Next"

### Build Stage:
1. Build provider: AWS CodeBuild
2. Select "Create build project"
3. Project name: "todo-app-build"
4. Environment: Managed image, Amazon Linux 2, Standard runtime
5. Service role: Create a new service role
6. Buildspec: Use the buildspec.yml in your repository
7. Click "Create build project"
8. Click "Next"

### Deploy Stage:
1. Deploy provider: AWS CodeDeploy
2. Application name: Choose the CodeDeploy application created by CloudFormation
3. Deployment group: Choose the deployment group created by CloudFormation
4. Click "Next"

5. Review the pipeline configuration and click "Create pipeline"

## Step 3: First Deployment

1. Commit and push your code to the repository
2. The pipeline will automatically start
3. Monitor the pipeline execution in the AWS CodePipeline console
4. Check the logs in CloudWatch if any issues occur

## Step 4: Access Your Application

Once deployment is complete, you can access your application at:

1. Backend API: http://{EC2-Public-IP}/api/
2. Frontend: http://{EC2-Public-IP}/

## Troubleshooting

### Common Issues:

1. **Deployment Fails**: 
   - Check CodeDeploy logs in CloudWatch
   - SSH into the EC2 instance and check /var/log/aws/codedeploy-agent/

2. **Application Not Starting**:
   - Check application logs in /var/log/todo-app/
   - Ensure environment variables are set correctly

3. **Database Connection Issues**:
   - Verify security group rules allow traffic from EC2 to RDS
   - Check database credentials in environment variables

## Monitoring and Maintenance

1. Set up CloudWatch alarms for EC2 and RDS metrics
2. Configure SNS notifications for pipeline failures
3. Regularly check CloudWatch logs for application issues

## Cost Optimization

To stay within Free Tier limits:
1. Use t2.micro instances only
2. Keep RDS storage under 20GB
3. Monitor S3 usage and clean up old artifacts
4. Turn off resources when not in use during development