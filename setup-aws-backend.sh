#!/bin/bash

# AWS Backend Infrastructure Setup Script

set -e

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "AWS CLI is not installed. Please install it first."
    exit 1
fi

# Check if AWS credentials are configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "AWS credentials are not configured. Please run 'aws configure' first."
    exit 1
fi

# Configuration - Change these values as needed
AWS_REGION="us-east-1"
STACK_NAME="todo-backend-stack"
EC2_KEY_NAME="todo-backend-key"
EC2_INSTANCE_TYPE="t2.micro"
S3_BUCKET_NAME="todo-backend-deployments-$(date +%s)"
APPLICATION_NAME="todo-app"
DEPLOYMENT_GROUP="todo-backend-group"

echo "Creating CloudFormation stack for Todo Backend infrastructure..."

# Create CloudFormation template
cat > cloudformation-backend.yml << 'EOL'
AWSTemplateFormatVersion: '2010-09-09'
Description: 'Todo Backend Infrastructure'

Parameters:
  InstanceType:
    Type: String
    Default: t2.micro
    Description: EC2 instance type
  KeyName:
    Type: String
    Description: Name of an existing EC2 KeyPair to enable SSH access
  S3BucketName:
    Type: String
    Description: Name of S3 bucket for deployment artifacts

Resources:
  # IAM Roles
  CodeDeployServiceRole:
    Type: 'AWS::IAM::Role'
    Properties:
      AssumeRolePolicyDocument:
        Version: '2012-10-17'
        Statement:
          - Effect: Allow
            Principal:
              Service: codedeploy.amazonaws.com
            Action: 'sts:AssumeRole'
      ManagedPolicyArns:
        - 'arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole'

  EC2Role:
    Type: 'AWS::IAM::Role'
    Properties:
      AssumeRolePolicyDocument:
        Version: '2012-10-17'
        Statement:
          - Effect: Allow
            Principal:
              Service: ec2.amazonaws.com
            Action: 'sts:AssumeRole'
      ManagedPolicyArns:
        - 'arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess'

  EC2InstanceProfile:
    Type: 'AWS::IAM::InstanceProfile'
    Properties:
      Roles:
        - !Ref EC2Role

  # S3 Bucket
  DeploymentBucket:
    Type: 'AWS::S3::Bucket'
    Properties:
      BucketName: !Ref S3BucketName
      VersioningConfiguration:
        Status: Enabled

  # Security Group
  SecurityGroup:
    Type: 'AWS::EC2::SecurityGroup'
    Properties:
      GroupDescription: Security group for Todo backend
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 22
          ToPort: 22
          CidrIp: 0.0.0.0/0
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0
        - IpProtocol: tcp
          FromPort: 8080
          ToPort: 8080
          CidrIp: 0.0.0.0/0

  # EC2 Instance
  EC2Instance:
    Type: 'AWS::EC2::Instance'
    Properties:
      ImageId: !FindInMap [RegionMap, !Ref 'AWS::Region', AMI]
      InstanceType: !Ref InstanceType
      SecurityGroups:
        - !Ref SecurityGroup
      KeyName: !Ref KeyName
      IamInstanceProfile: !Ref EC2InstanceProfile
      Tags:
        - Key: Name
          Value: todo-backend
      UserData:
        Fn::Base64: !Sub |
          #!/bin/bash
          yum update -y
          yum install -y ruby
          yum install -y aws-cli
          cd /home/ec2-user
          aws s3 cp s3://aws-codedeploy-${AWS::Region}/latest/install . --region ${AWS::Region}
          chmod +x ./install
          ./install auto
          service codedeploy-agent start

  # CodeDeploy Application
  CodeDeployApplication:
    Type: 'AWS::CodeDeploy::Application'
    Properties:
      ApplicationName: todo-app

  # CodeDeploy Deployment Group
  DeploymentGroup:
    Type: 'AWS::CodeDeploy::DeploymentGroup'
    Properties:
      ApplicationName: !Ref CodeDeployApplication
      ServiceRoleArn: !GetAtt CodeDeployServiceRole.Arn
      DeploymentGroupName: todo-backend-group
      DeploymentConfigName: CodeDeployDefault.OneAtATime
      Ec2TagFilters:
        - Key: Name
          Value: todo-backend
          Type: KEY_AND_VALUE

Mappings:
  RegionMap:
    us-east-1:
      AMI: ami-0e731c8a588258d0d
    us-east-2:
      AMI: ami-0c20d96b50541c279
    us-west-1:
      AMI: ami-0487b1fe60c1fd1a2
    us-west-2:
      AMI: ami-0efcece6bed30fd98
    eu-west-1:
      AMI: ami-0db0b1312a24e8101
    eu-central-1:
      AMI: ami-0a261c0e5f51090b1
    ap-northeast-1:
      AMI: ami-0e2bf1ada70fd3f33
    ap-southeast-1:
      AMI: ami-0d058fe428540cd89

Outputs:
  InstanceId:
    Description: ID of the EC2 instance
    Value: !Ref EC2Instance
  PublicDNS:
    Description: Public DNS name of the EC2 instance
    Value: !GetAtt EC2Instance.PublicDnsName
  PublicIP:
    Description: Public IP address of the EC2 instance
    Value: !GetAtt EC2Instance.PublicIp
  S3BucketName:
    Description: Name of the S3 bucket for deployment artifacts
    Value: !Ref DeploymentBucket
  CodeDeployApplication:
    Description: Name of the CodeDeploy application
    Value: !Ref CodeDeployApplication
  DeploymentGroup:
    Description: Name of the CodeDeploy deployment group
    Value: !Ref DeploymentGroup
EOL

# Create EC2 key pair if it doesn't exist
if ! aws ec2 describe-key-pairs --key-names "$EC2_KEY_NAME" --region "$AWS_REGION" &> /dev/null; then
    echo "Creating EC2 key pair $EC2_KEY_NAME..."
    aws ec2 create-key-pair --key-name "$EC2_KEY_NAME" --region "$AWS_REGION" --query 'KeyMaterial' --output text > "${EC2_KEY_NAME}.pem"
    chmod 400 "${EC2_KEY_NAME}.pem"
    echo "Key pair created and saved to ${EC2_KEY_NAME}.pem"
else
    echo "Key pair $EC2_KEY_NAME already exists"
fi

# Create CloudFormation stack
echo "Creating CloudFormation stack $STACK_NAME..."
aws cloudformation create-stack \
    --stack-name "$STACK_NAME" \
    --template-body file://cloudformation-backend.yml \
    --parameters \
        ParameterKey=InstanceType,ParameterValue="$EC2_INSTANCE_TYPE" \
        ParameterKey=KeyName,ParameterValue="$EC2_KEY_NAME" \
        ParameterKey=S3BucketName,ParameterValue="$S3_BUCKET_NAME" \
    --capabilities CAPABILITY_IAM \
    --region "$AWS_REGION"

echo "Waiting for stack creation to complete..."
aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME" --region "$AWS_REGION"

# Get outputs from CloudFormation stack
echo "Getting stack outputs..."
PUBLIC_IP=$(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$AWS_REGION" --query "Stacks[0].Outputs[?OutputKey=='PublicIP'].OutputValue" --output text)
PUBLIC_DNS=$(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$AWS_REGION" --query "Stacks[0].Outputs[?OutputKey=='PublicDNS'].OutputValue" --output text)
S3_BUCKET=$(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$AWS_REGION" --query "Stacks[0].Outputs[?OutputKey=='S3BucketName'].OutputValue" --output text)

# Output results
echo "=== Todo Backend Infrastructure Setup Complete ==="
echo "EC2 Instance Public IP: $PUBLIC_IP"
echo "EC2 Instance Public DNS: $PUBLIC_DNS"
echo "S3 Bucket for Deployments: $S3_BUCKET"
echo "CodeDeploy Application: $APPLICATION_NAME"
echo "CodeDeploy Deployment Group: $DEPLOYMENT_GROUP"
echo ""
echo "To connect to your EC2 instance:"
echo "ssh -i ${EC2_KEY_NAME}.pem ec2-user@$PUBLIC_IP"
echo ""
echo "Add these GitHub secrets for CI/CD:"
echo "AWS_REGION: $AWS_REGION"
echo "AWS_S3_BUCKET: $S3_BUCKET"
echo ""
echo "Note: You'll need to add your AWS access credentials as GitHub secrets:"
echo "AWS_ACCESS_KEY_ID"
echo "AWS_SECRET_ACCESS_KEY"