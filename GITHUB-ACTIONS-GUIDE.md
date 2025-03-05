# GitHub Actions CI/CD Pipeline for AWS Free Tier

This document explains how to set up and use the GitHub Actions CI/CD pipeline for deploying the Todo application to AWS Free Tier.

## Overview

This CI/CD pipeline automates the following processes:

1. Building and testing both frontend and backend applications
2. Deploying the application to an AWS EC2 instance (Free Tier)
3. Configuring the environment for different deployment stages

## Workflow Files

The CI/CD pipeline consists of three GitHub Actions workflow files:

1. `build-and-test.yml`: Builds and tests the frontend and backend applications
2. `deploy.yml`: Deploys the application to AWS EC2
3. `configure-environment.yml`: Creates configuration files for different environments

## Prerequisites

Before using this CI/CD pipeline, you need to set up the following:

### 1. AWS Resources

- **EC2 Instance**: t2.micro (Free Tier eligible)
- **RDS Database**: db.t3.micro PostgreSQL (Free Tier eligible)
- **Security Groups**: Configure to allow HTTP/HTTPS traffic

### 2. GitHub Secrets

Add the following secrets to your GitHub repository:

- `SSH_PRIVATE_KEY`: Private SSH key for connecting to EC2
- `KNOWN_HOSTS`: SSH known_hosts entry for your EC2 instance
- `EC2_HOST`: EC2 instance public DNS or IP
- `EC2_USERNAME`: EC2 instance username (usually 'ec2-user' or 'ubuntu')
- `PROD_DB_URL`: JDBC URL for your RDS database
- `PROD_DB_USERNAME`: RDS database username
- `PROD_DB_PASSWORD`: RDS database password
- `PROD_API_URL`: URL for the backend API

## Setting Up SSH Access

1. Generate an SSH key pair:
   ```bash
   ssh-keygen -t rsa -b 4096 -C "github-actions" -f github-actions
   ```

2. Add the public key to your EC2 instance:
   ```bash
   cat github-actions.pub >> ~/.ssh/authorized_keys
   ```

3. Get the known_hosts entry:
   ```bash
   ssh-keyscan -H <EC2_HOST> >> known_hosts
   ```

4. Add the private key and known_hosts content to GitHub secrets.

## AWS Free Tier Resources Setup

### EC2 Setup

1. Launch a t2.micro EC2 instance with Amazon Linux 2 or Ubuntu
2. Install required software:
   ```bash
   sudo yum update -y  # For Amazon Linux
   sudo apt update -y  # For Ubuntu
   
   # Install Java
   sudo apt install -y openjdk-17-jdk  # Ubuntu
   # or
   sudo amazon-linux-extras install java-openjdk17  # Amazon Linux
   
   # Install Nginx
   sudo apt install -y nginx  # Ubuntu
   # or
   sudo amazon-linux-extras install nginx1  # Amazon Linux
   ```

### RDS Setup

1. Create a db.t3.micro PostgreSQL instance
2. Configure security group to allow connections from your EC2 instance
3. Create a database for your application

## Workflow Execution

### Automatic Execution

- The `build-and-test.yml` workflow runs on push to main/master/develop branches and on pull requests
- The `deploy.yml` workflow runs automatically after successful completion of the build-and-test workflow on the main/master branches

### Manual Execution

- The `configure-environment.yml` workflow can be triggered manually from the GitHub Actions tab

## Troubleshooting

If you encounter issues with the deployment:

1. Check the GitHub Actions logs for error messages
2. Verify that all secrets are correctly set
3. Check the EC2 instance logs:
   ```bash
   sudo journalctl -u nginx
   sudo tail -f /var/log/nginx/error.log
   ```

## Extending the Pipeline

You can extend this pipeline by:

1. Adding more test stages
2. Implementing blue-green deployment
3. Adding monitoring and alerting
4. Setting up automatic database migrations