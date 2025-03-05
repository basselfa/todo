# Todo Application CI/CD Pipeline for AWS Free Tier

This repository contains a Todo application with a React frontend and Spring Boot backend, configured for deployment to AWS Free Tier using CI/CD.

## Project Structure

- `todo-frontend/`: React application
- `todo-backend/`: Spring Boot API
- `scripts/`: Deployment scripts for AWS CodeDeploy
- `buildspec.yml`: AWS CodeBuild configuration
- `appspec.yml`: AWS CodeDeploy configuration

## AWS Free Tier Resources Used

1. **AWS CodePipeline**: Manages the CI/CD workflow
2. **AWS CodeBuild**: Builds the application
3. **AWS CodeDeploy**: Deploys the application
4. **AWS Elastic Beanstalk** or **EC2**: Hosts the application
5. **Amazon RDS (PostgreSQL)**: Database
6. **Amazon S3**: Stores build artifacts and frontend assets
7. **AWS CloudFront** (optional): CDN for frontend assets

## Setup Instructions

### 1. AWS Resources Setup

#### Database (RDS)
1. Create an Amazon RDS PostgreSQL instance
   - Instance class: db.t3.micro (Free Tier eligible)
   - Storage: 20GB
   - Create a database named `todo`
   - Note the endpoint, username, and password

#### S3 Bucket
1. Create an S3 bucket for build artifacts
2. Create an S3 bucket for frontend static assets (if deploying separately)
3. Configure the frontend bucket for static website hosting

#### EC2 or Elastic Beanstalk
1. Create an EC2 instance (t2.micro) or Elastic Beanstalk environment
2. Configure security groups to allow HTTP/HTTPS traffic
3. Ensure the instance can connect to your RDS database

### 2. CI/CD Pipeline Setup

#### CodeBuild Project
1. Create a CodeBuild project using the provided `buildspec.yml`
2. Configure environment variables:
   - `RDS_HOSTNAME`: Your RDS endpoint
   - `RDS_PORT`: PostgreSQL port (default: 5432)
   - `RDS_DB_NAME`: Database name (todo)
   - `RDS_USERNAME`: Database username
   - `RDS_PASSWORD`: Database password

#### CodeDeploy Application
1. Create a CodeDeploy application
2. Create a deployment group targeting your EC2 instance or Elastic Beanstalk environment
3. Configure to use the provided `appspec.yml`

#### CodePipeline
1. Create a CodePipeline with the following stages:
   - Source: Connect to your repository
   - Build: Use your CodeBuild project
   - Deploy: Use your CodeDeploy application

### 3. Deployment

Once the pipeline is set up, it will automatically:
1. Build the frontend and backend
2. Package the application
3. Deploy to your EC2 instance or Elastic Beanstalk environment

## Manual Deployment

If you need to deploy manually:

1. Build the frontend:
   ```
   cd todo-frontend
   npm install
   npm run build
   ```

2. Build the backend:
   ```
   cd todo-backend
   ./mvnw clean package
   ```

3. Deploy the artifacts to your EC2 instance

## Troubleshooting

- Check CloudWatch Logs for CodeBuild and CodeDeploy logs
- Check the application logs in `/var/log/todo-app/` on the EC2 instance
- Verify RDS connectivity from the EC2 instance

## Security Considerations

- Store sensitive information (database credentials, API keys) in AWS Secrets Manager
- Use IAM roles with least privilege
- Enable HTTPS for all public endpoints