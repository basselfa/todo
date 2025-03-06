#!/bin/bash

# Colors for better readability
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}   AWS Infrastructure Deployment      ${NC}"
echo -e "${BLUE}======================================${NC}"

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}AWS CLI is not installed. Please install it first.${NC}"
    echo "Visit https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if AWS CLI is configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}AWS CLI is not configured. Please run 'aws configure' first.${NC}"
    exit 1
fi

echo -e "${YELLOW}This script will deploy the AWS infrastructure for your Todo application.${NC}"
echo -e "${YELLOW}You will need to provide some information:${NC}"
echo ""

# Ask for frontend bucket name
read -p "Enter a globally unique name for your frontend S3 bucket: " FRONTEND_BUCKET_NAME

# Ask for backend bucket name
read -p "Enter a globally unique name for your backend S3 bucket: " BACKEND_BUCKET_NAME

# Ask for CloudFront price class
echo "Select CloudFront price class:"
echo "1) PriceClass_100 (Least expensive, US, Canada, Europe)"
echo "2) PriceClass_200 (US, Canada, Europe, Asia, Middle East, Africa)"
echo "3) PriceClass_All (Most expensive, all regions)"
read -p "Enter your choice (1-3): " PRICE_CLASS_CHOICE

case $PRICE_CLASS_CHOICE in
    1) PRICE_CLASS="PriceClass_100" ;;
    2) PRICE_CLASS="PriceClass_200" ;;
    3) PRICE_CLASS="PriceClass_All" ;;
    *) 
        echo -e "${RED}Invalid choice. Using PriceClass_100 as default.${NC}"
        PRICE_CLASS="PriceClass_100"
        ;;
esac

# Ask for AWS region
read -p "Enter AWS region (e.g., us-east-1): " AWS_REGION

echo -e "${YELLOW}Deploying CloudFormation stack...${NC}"

# Deploy CloudFormation stack
STACK_NAME="todo-app-infrastructure"

aws cloudformation create-stack \
    --stack-name $STACK_NAME \
    --template-body file://aws-infrastructure.json \
    --parameters \
        ParameterKey=FrontendBucketName,ParameterValue=$FRONTEND_BUCKET_NAME \
        ParameterKey=BackendBucketName,ParameterValue=$BACKEND_BUCKET_NAME \
        ParameterKey=PriceClass,ParameterValue=$PRICE_CLASS \
    --capabilities CAPABILITY_NAMED_IAM \
    --region $AWS_REGION

if [ $? -ne 0 ]; then
    echo -e "${RED}Failed to create CloudFormation stack.${NC}"
    exit 1
fi

echo -e "${GREEN}CloudFormation stack creation initiated.${NC}"
echo -e "${YELLOW}Waiting for stack creation to complete (this may take 15-20 minutes)...${NC}"

# Wait for stack creation to complete
aws cloudformation wait stack-create-complete --stack-name $STACK_NAME --region $AWS_REGION

if [ $? -ne 0 ]; then
    echo -e "${RED}Stack creation failed or timed out.${NC}"
    echo -e "${YELLOW}Check the AWS CloudFormation console for details.${NC}"
    exit 1
fi

echo -e "${GREEN}Stack creation completed successfully!${NC}"

# Get stack outputs
echo -e "${YELLOW}Retrieving stack outputs...${NC}"
OUTPUTS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs" --output json --region $AWS_REGION)

# Extract values from outputs
FRONTEND_BUCKET=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="FrontendBucketName") | .OutputValue')
BACKEND_BUCKET=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="BackendBucketName") | .OutputValue')
CLOUDFRONT_DIST_ID=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="CloudFrontDistributionId") | .OutputValue')
CLOUDFRONT_DOMAIN=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="CloudFrontDomainName") | .OutputValue')
ACCESS_KEY_ID=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="AccessKeyId") | .OutputValue')
SECRET_ACCESS_KEY=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="SecretAccessKey") | .OutputValue')
EB_URL=$(echo $OUTPUTS | jq -r '.[] | select(.OutputKey=="ElasticBeanstalkEnvironmentURL") | .OutputValue')

# Create GitHub Actions secrets instructions file
cat > github-actions-setup.md << EOF
# GitHub Actions Setup Instructions

Add these secrets to your GitHub repository:

1. Go to your GitHub repository
2. Navigate to Settings > Secrets and variables > Actions
3. Add the following secrets:

| Secret Name | Secret Value |
|-------------|-------------|
| \`AWS_ACCESS_KEY_ID\` | \`$ACCESS_KEY_ID\` |
| \`AWS_SECRET_ACCESS_KEY\` | \`$SECRET_ACCESS_KEY\` |
| \`AWS_REGION\` | \`$AWS_REGION\` |
| \`AWS_S3_BUCKET\` | \`$FRONTEND_BUCKET\` |
| \`AWS_S3_BUCKET_BACKEND\` | \`$BACKEND_BUCKET\` |
| \`AWS_CLOUDFRONT_DISTRIBUTION_ID\` | \`$CLOUDFRONT_DIST_ID\` |

Once these secrets are added, your GitHub Actions workflow will be able to deploy to AWS.
EOF

echo -e "${GREEN}===================================================${NC}"
echo -e "${GREEN}          Infrastructure Deployment Complete!       ${NC}"
echo -e "${GREEN}===================================================${NC}"
echo ""
echo -e "${YELLOW}Frontend bucket:${NC} $FRONTEND_BUCKET"
echo -e "${YELLOW}Backend bucket:${NC} $BACKEND_BUCKET"
echo -e "${YELLOW}CloudFront URL:${NC} https://$CLOUDFRONT_DOMAIN"
echo -e "${YELLOW}Elastic Beanstalk URL:${NC} $EB_URL"
echo ""
echo -e "${BLUE}GitHub Actions setup instructions have been saved to:${NC} github-actions-setup.md"
echo -e "${BLUE}Follow these instructions to set up your GitHub repository for CI/CD.${NC}"
echo ""
echo -e "${GREEN}Your Todo application is now ready for deployment!${NC}"
echo -e "${GREEN}Push your code to GitHub to trigger the deployment pipeline.${NC}"