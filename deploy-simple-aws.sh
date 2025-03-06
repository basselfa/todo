#!/bin/bash

echo "=== Simple AWS Infrastructure Deployment ==="
echo "This script will deploy the AWS infrastructure for your Todo application."
echo

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed. Please install it first:"
    echo "https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "AWS CLI is not configured. Please run 'aws configure' first."
    exit 1
fi

# Ask for bucket names
read -p "Enter a globally unique name for your frontend S3 bucket: " FRONTEND_BUCKET_NAME
read -p "Enter a globally unique name for your backend S3 bucket: " BACKEND_BUCKET_NAME
read -p "Enter a name for your CloudFormation stack: " STACK_NAME

# Default to 'todo-stack' if no stack name provided
STACK_NAME=${STACK_NAME:-todo-stack}

echo
echo "Deploying CloudFormation stack..."
echo "This may take 15-20 minutes to complete."
echo

# Deploy the CloudFormation stack
aws cloudformation create-stack \
  --stack-name "$STACK_NAME" \
  --template-body file://aws-simple-infrastructure.json \
  --parameters \
    ParameterKey=FrontendBucketName,ParameterValue="$FRONTEND_BUCKET_NAME" \
    ParameterKey=BackendBucketName,ParameterValue="$BACKEND_BUCKET_NAME" \
  --capabilities CAPABILITY_NAMED_IAM

echo
echo "Stack creation initiated. Waiting for completion..."
echo "This may take 15-20 minutes."
echo

# Wait for the stack to be created
aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME"

if [ $? -eq 0 ]; then
    echo "Stack created successfully!"
    echo
    echo "=== GitHub Secrets Setup ==="
    echo "Add these secrets to your GitHub repository:"
    echo

    # Get the outputs from the stack
    OUTPUTS=$(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --query "Stacks[0].Outputs" --output json)
    
    # Extract the values
    AWS_ACCESS_KEY_ID=$(echo "$OUTPUTS" | grep -A 3 "DeploymentUserAccessKeyId" | grep "OutputValue" | cut -d'"' -f4)
    AWS_SECRET_ACCESS_KEY=$(echo "$OUTPUTS" | grep -A 3 "DeploymentUserSecretAccessKey" | grep "OutputValue" | cut -d'"' -f4)
    AWS_S3_BUCKET=$(echo "$OUTPUTS" | grep -A 3 "FrontendBucketName" | grep "OutputValue" | cut -d'"' -f4)
    AWS_S3_BUCKET_BACKEND=$(echo "$OUTPUTS" | grep -A 3 "BackendBucketName" | grep "OutputValue" | cut -d'"' -f4)
    AWS_CLOUDFRONT_DISTRIBUTION_ID=$(echo "$OUTPUTS" | grep -A 3 "CloudFrontDistributionId" | grep "OutputValue" | cut -d'"' -f4)
    AWS_REGION=$(echo "$OUTPUTS" | grep -A 3 "Region" | grep "OutputValue" | cut -d'"' -f4)
    
    echo "AWS_ACCESS_KEY_ID: $AWS_ACCESS_KEY_ID"
    echo "AWS_SECRET_ACCESS_KEY: $AWS_SECRET_ACCESS_KEY"
    echo "AWS_S3_BUCKET: $AWS_S3_BUCKET"
    echo "AWS_S3_BUCKET_BACKEND: $AWS_S3_BUCKET_BACKEND"
    echo "AWS_CLOUDFRONT_DISTRIBUTION_ID: $AWS_CLOUDFRONT_DISTRIBUTION_ID"
    echo "AWS_REGION: $AWS_REGION"
    
    echo
    echo "=== Application URLs ==="
    CLOUDFRONT_URL=$(echo "$OUTPUTS" | grep -A 3 "CloudFrontDomainName" | grep "OutputValue" | cut -d'"' -f4)
    BEANSTALK_URL=$(echo "$OUTPUTS" | grep -A 3 "ElasticBeanstalkEnvironmentURL" | grep "OutputValue" | cut -d'"' -f4)
    
    echo "Frontend: https://$CLOUDFRONT_URL"
    echo "Backend: $BEANSTALK_URL"
    
    echo
    echo "=== Next Steps ==="
    echo "1. Add the secrets to your GitHub repository"
    echo "2. Push your code to the main branch"
    echo "3. The GitHub Actions workflow will automatically deploy your application"
else
    echo "Stack creation failed. Check the AWS CloudFormation console for details."
fi