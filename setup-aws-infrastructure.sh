#!/bin/bash

# Colors for better readability
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}===========================================================${NC}"
echo -e "${BLUE}       AWS Infrastructure Setup for GitHub CI/CD           ${NC}"
echo -e "${BLUE}===========================================================${NC}"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}AWS CLI is not installed. Please install it first.${NC}"
    echo "Visit: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if AWS CLI is configured
echo -e "${YELLOW}Checking AWS CLI configuration...${NC}"
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}AWS CLI is not configured properly. Please run 'aws configure' first.${NC}"
    exit 1
fi

echo -e "${GREEN}AWS CLI is configured properly.${NC}"

# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo -e "${BLUE}AWS Account ID: ${AWS_ACCOUNT_ID}${NC}"

# Ask for bucket name
echo -e "${YELLOW}Enter a globally unique name for your S3 bucket:${NC}"
read BUCKET_NAME

# Ask for AWS region
echo -e "${YELLOW}Enter AWS region (default: us-east-1):${NC}"
read AWS_REGION
AWS_REGION=${AWS_REGION:-us-east-1}

# Create S3 bucket
echo -e "${YELLOW}Creating S3 bucket...${NC}"
if aws s3api create-bucket --bucket ${BUCKET_NAME} --region ${AWS_REGION} ${AWS_REGION != "us-east-1" ? "--create-bucket-configuration LocationConstraint=${AWS_REGION}" : ""} &> /dev/null; then
    echo -e "${GREEN}S3 bucket created successfully.${NC}"
else
    echo -e "${RED}Failed to create S3 bucket. It might already exist or the name is taken.${NC}"
    exit 1
fi

# Configure S3 bucket for static website hosting
echo -e "${YELLOW}Configuring S3 bucket for static website hosting...${NC}"
aws s3 website s3://${BUCKET_NAME} --index-document index.html --error-document index.html

# Make bucket public
echo -e "${YELLOW}Making bucket public...${NC}"
aws s3api put-public-access-block --bucket ${BUCKET_NAME} --public-access-block-configuration "BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false"

# Add bucket policy
echo -e "${YELLOW}Adding bucket policy...${NC}"
POLICY='{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::'${BUCKET_NAME}'/*"
    }
  ]
}'

aws s3api put-bucket-policy --bucket ${BUCKET_NAME} --policy "$POLICY"

# Create CloudFront distribution
echo -e "${YELLOW}Creating CloudFront distribution...${NC}"
DISTRIBUTION_RESULT=$(aws cloudfront create-distribution --distribution-config '{
  "CallerReference": "'$(date +%s)'",
  "DefaultRootObject": "index.html",
  "Origins": {
    "Quantity": 1,
    "Items": [
      {
        "Id": "S3Origin",
        "DomainName": "'${BUCKET_NAME}'.s3.amazonaws.com",
        "S3OriginConfig": {
          "OriginAccessIdentity": ""
        }
      }
    ]
  },
  "DefaultCacheBehavior": {
    "TargetOriginId": "S3Origin",
    "ViewerProtocolPolicy": "redirect-to-https",
    "AllowedMethods": {
      "Quantity": 2,
      "Items": ["GET", "HEAD"],
      "CachedMethods": {
        "Quantity": 2,
        "Items": ["GET", "HEAD"]
      }
    },
    "Compress": true,
    "ForwardedValues": {
      "QueryString": false,
      "Cookies": {
        "Forward": "none"
      }
    },
    "MinTTL": 0,
    "DefaultTTL": 86400,
    "MaxTTL": 31536000
  },
  "Comment": "Distribution for '${BUCKET_NAME}'",
  "Enabled": true
}')

# Extract distribution ID and domain name
DISTRIBUTION_ID=$(echo $DISTRIBUTION_RESULT | jq -r '.Distribution.Id')
DISTRIBUTION_DOMAIN=$(echo $DISTRIBUTION_RESULT | jq -r '.Distribution.DomainName')

echo -e "${GREEN}CloudFront distribution created successfully.${NC}"
echo -e "${BLUE}Distribution ID: ${DISTRIBUTION_ID}${NC}"
echo -e "${BLUE}Distribution Domain: ${DISTRIBUTION_DOMAIN}${NC}"

# Create IAM user for GitHub Actions
echo -e "${YELLOW}Creating IAM user for GitHub Actions...${NC}"
USER_NAME="github-actions-deployer"

# Create user
aws iam create-user --user-name ${USER_NAME}

# Create policy for the user
POLICY_DOCUMENT='{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:ListBucket",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::'${BUCKET_NAME}'",
        "arn:aws:s3:::'${BUCKET_NAME}'/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "cloudfront:CreateInvalidation"
      ],
      "Resource": [
        "arn:aws:cloudfront::'${AWS_ACCOUNT_ID}':distribution/'${DISTRIBUTION_ID}'"
      ]
    }
  ]
}'

POLICY_NAME="GitHubActionsDeployPolicy"
aws iam create-policy --policy-name ${POLICY_NAME} --policy-document "$POLICY_DOCUMENT"

# Attach policy to user
POLICY_ARN="arn:aws:iam::${AWS_ACCOUNT_ID}:policy/${POLICY_NAME}"
aws iam attach-user-policy --user-name ${USER_NAME} --policy-arn ${POLICY_ARN}

# Create access key
ACCESS_KEY_RESULT=$(aws iam create-access-key --user-name ${USER_NAME})
ACCESS_KEY_ID=$(echo $ACCESS_KEY_RESULT | jq -r '.AccessKey.AccessKeyId')
SECRET_ACCESS_KEY=$(echo $ACCESS_KEY_RESULT | jq -r '.AccessKey.SecretAccessKey')

echo -e "${GREEN}IAM user created successfully.${NC}"

# Summary
echo -e "${BLUE}===========================================================${NC}"
echo -e "${BLUE}                   SETUP COMPLETE                          ${NC}"
echo -e "${BLUE}===========================================================${NC}"
echo -e "${GREEN}Add these secrets to your GitHub repository:${NC}"
echo -e "${YELLOW}AWS_ACCESS_KEY_ID:${NC} ${ACCESS_KEY_ID}"
echo -e "${YELLOW}AWS_SECRET_ACCESS_KEY:${NC} ${SECRET_ACCESS_KEY}"
echo -e "${YELLOW}AWS_REGION:${NC} ${AWS_REGION}"
echo -e "${YELLOW}AWS_S3_BUCKET:${NC} ${BUCKET_NAME}"
echo -e "${YELLOW}AWS_CLOUDFRONT_DISTRIBUTION_ID:${NC} ${DISTRIBUTION_ID}"
echo -e "${BLUE}===========================================================${NC}"
echo -e "${GREEN}Your website will be available at:${NC}"
echo -e "${BLUE}https://${DISTRIBUTION_DOMAIN}${NC}"
echo -e "${YELLOW}Note: It may take up to 15 minutes for the CloudFront distribution to deploy.${NC}"
echo -e "${BLUE}===========================================================${NC}"

# Save information to a file
echo "AWS_ACCESS_KEY_ID: ${ACCESS_KEY_ID}" > aws-deployment-info.txt
echo "AWS_SECRET_ACCESS_KEY: ${SECRET_ACCESS_KEY}" >> aws-deployment-info.txt
echo "AWS_REGION: ${AWS_REGION}" >> aws-deployment-info.txt
echo "AWS_S3_BUCKET: ${BUCKET_NAME}" >> aws-deployment-info.txt
echo "AWS_CLOUDFRONT_DISTRIBUTION_ID: ${DISTRIBUTION_ID}" >> aws-deployment-info.txt
echo "Website URL: https://${DISTRIBUTION_DOMAIN}" >> aws-deployment-info.txt

echo -e "${GREEN}Information saved to aws-deployment-info.txt${NC}"
echo -e "${RED}IMPORTANT: Keep this file secure as it contains sensitive information!${NC}"