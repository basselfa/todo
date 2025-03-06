#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}===== AWS Elastic Beanstalk Backend Setup Script =====${NC}"
echo "This script will help you set up AWS resources for backend deployment."
echo

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}AWS CLI is not installed. Please install it first.${NC}"
    echo "Visit: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${YELLOW}AWS CLI is not configured with credentials.${NC}"
    echo "Please run 'aws configure' to set up your AWS credentials."
    exit 1
fi

echo -e "${GREEN}Step 1: Choose AWS Region${NC}"
echo "Available regions:"
echo "1) us-east-1 (N. Virginia)"
echo "2) us-east-2 (Ohio)"
echo "3) us-west-1 (N. California)"
echo "4) us-west-2 (Oregon)"
echo "5) eu-west-1 (Ireland)"
echo "6) eu-central-1 (Frankfurt)"
echo "7) ap-northeast-1 (Tokyo)"
echo "8) ap-southeast-1 (Singapore)"
echo "9) ap-southeast-2 (Sydney)"
echo "10) Other (specify)"

read -p "Select region (1-10): " region_choice

case $region_choice in
    1) region="us-east-1" ;;
    2) region="us-east-2" ;;
    3) region="us-west-1" ;;
    4) region="us-west-2" ;;
    5) region="eu-west-1" ;;
    6) region="eu-central-1" ;;
    7) region="ap-northeast-1" ;;
    8) region="ap-southeast-1" ;;
    9) region="ap-southeast-2" ;;
    10) read -p "Enter region code: " region ;;
    *) echo -e "${RED}Invalid choice. Exiting.${NC}"; exit 1 ;;
esac

echo -e "${GREEN}Using region: $region${NC}"
echo

# Application name
echo -e "${GREEN}Step 2: Set Application Name${NC}"
read -p "Enter Elastic Beanstalk application name (default: todo-backend): " app_name
app_name=${app_name:-todo-backend}
echo

# Environment name
echo -e "${GREEN}Step 3: Set Environment Name${NC}"
read -p "Enter Elastic Beanstalk environment name (default: todo-backend-env): " env_name
env_name=${env_name:-todo-backend-env}
echo

# Environment type
echo -e "${GREEN}Step 4: Choose Environment Type${NC}"
echo "1) Web server environment"
echo "2) Worker environment"
read -p "Select environment type (default: 1): " env_type_choice
env_type_choice=${env_type_choice:-1}

if [ "$env_type_choice" -eq 1 ]; then
    env_type="WebServer"
else
    env_type="Worker"
fi
echo

# Create IAM role for Elastic Beanstalk
echo -e "${GREEN}Step 5: Creating IAM roles...${NC}"

# Create trust policy document for EC2
cat > trust-policy-ec2.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

# Create trust policy document for Elastic Beanstalk
cat > trust-policy-eb.json << EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "elasticbeanstalk.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

# Create IAM role for EC2
aws iam create-role --role-name aws-elasticbeanstalk-ec2-role \
  --assume-role-policy-document file://trust-policy-ec2.json > /dev/null 2>&1

# Attach policies to EC2 role
aws iam attach-role-policy --role-name aws-elasticbeanstalk-ec2-role \
  --policy-arn arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier > /dev/null 2>&1
aws iam attach-role-policy --role-name aws-elasticbeanstalk-ec2-role \
  --policy-arn arn:aws:iam::aws:policy/AWSElasticBeanstalkWorkerTier > /dev/null 2>&1
aws iam attach-role-policy --role-name aws-elasticbeanstalk-ec2-role \
  --policy-arn arn:aws:iam::aws:policy/AWSElasticBeanstalkMulticontainerDocker > /dev/null 2>&1

# Create instance profile
aws iam create-instance-profile --instance-profile-name aws-elasticbeanstalk-ec2-role > /dev/null 2>&1
aws iam add-role-to-instance-profile --instance-profile-name aws-elasticbeanstalk-ec2-role \
  --role-name aws-elasticbeanstalk-ec2-role > /dev/null 2>&1

# Create IAM role for Elastic Beanstalk service
aws iam create-role --role-name aws-elasticbeanstalk-service-role \
  --assume-role-policy-document file://trust-policy-eb.json > /dev/null 2>&1

# Attach policies to Elastic Beanstalk service role
aws iam attach-role-policy --role-name aws-elasticbeanstalk-service-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkEnhancedHealth > /dev/null 2>&1
aws iam attach-role-policy --role-name aws-elasticbeanstalk-service-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkService > /dev/null 2>&1

echo -e "${GREEN}IAM roles created successfully.${NC}"
echo

# Create Elastic Beanstalk application
echo -e "${GREEN}Step 6: Creating Elastic Beanstalk application...${NC}"
aws elasticbeanstalk create-application --application-name "$app_name" \
  --region "$region" > /dev/null 2>&1

# Check if application was created successfully
if [ $? -ne 0 ]; then
    echo -e "${YELLOW}Application might already exist or there was an error.${NC}"
else
    echo -e "${GREEN}Application created successfully.${NC}"
fi
echo

# Create Elastic Beanstalk environment
echo -e "${GREEN}Step 7: Creating Elastic Beanstalk environment...${NC}"
echo "This may take several minutes..."

aws elasticbeanstalk create-environment \
  --application-name "$app_name" \
  --environment-name "$env_name" \
  --solution-stack-name "64bit Amazon Linux 2 v3.5.1 running Corretto 17" \
  --option-settings \
    Namespace=aws:autoscaling:launchconfiguration,OptionName=IamInstanceProfile,Value=aws-elasticbeanstalk-ec2-role \
    Namespace=aws:elasticbeanstalk:environment,OptionName=ServiceRole,Value=aws-elasticbeanstalk-service-role \
    Namespace=aws:elasticbeanstalk:environment,OptionName=EnvironmentType,Value=SingleInstance \
  --region "$region" > /dev/null 2>&1

# Check if environment was created successfully
if [ $? -ne 0 ]; then
    echo -e "${RED}Failed to create environment. Check AWS console for details.${NC}"
    exit 1
else
    echo -e "${GREEN}Environment creation initiated.${NC}"
fi
echo

# Create IAM user for GitHub Actions
echo -e "${GREEN}Step 8: Creating IAM user for GitHub Actions...${NC}"
aws iam create-user --user-name github-actions-deployer > /dev/null 2>&1

# Attach policies to the user
aws iam attach-user-policy --user-name github-actions-deployer \
  --policy-arn arn:aws:iam::aws:policy/AWSElasticBeanstalkFullAccess > /dev/null 2>&1
aws iam attach-user-policy --user-name github-actions-deployer \
  --policy-arn arn:aws:iam::aws:policy/AmazonS3FullAccess > /dev/null 2>&1

# Create access key for the user
key_output=$(aws iam create-access-key --user-name github-actions-deployer)
access_key=$(echo $key_output | jq -r .AccessKey.AccessKeyId)
secret_key=$(echo $key_output | jq -r .AccessKey.SecretAccessKey)

echo -e "${GREEN}IAM user created successfully.${NC}"
echo

# Clean up temporary files
rm -f trust-policy-ec2.json trust-policy-eb.json

# Output the information
echo -e "${GREEN}===== Setup Complete! =====${NC}"
echo
echo -e "${YELLOW}Add these secrets to your GitHub repository:${NC}"
echo
echo "AWS_ACCESS_KEY_ID: $access_key"
echo "AWS_SECRET_ACCESS_KEY: $secret_key"
echo "AWS_REGION: $region"
echo "AWS_EB_APPLICATION_NAME: $app_name"
echo "AWS_EB_ENVIRONMENT_NAME: $env_name"
echo
echo -e "${GREEN}Your Elastic Beanstalk environment is being created.${NC}"
echo "Check the AWS Elastic Beanstalk console to monitor the status."
echo "The environment creation process typically takes 5-10 minutes."
echo
echo -e "${GREEN}After environment creation is complete, you can push to your GitHub repository to deploy.${NC}"