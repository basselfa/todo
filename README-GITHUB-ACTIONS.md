# Todo Application CI/CD with GitHub Actions

This repository contains a Todo application with a CI/CD pipeline implemented using GitHub Actions for deployment to AWS Free Tier resources.

## Project Structure

- **todo-frontend**: React-based frontend application
- **todo-backend**: Spring Boot backend application
- **.github/workflows**: GitHub Actions workflow files
- **scripts**: Deployment scripts

## CI/CD Pipeline

The CI/CD pipeline consists of three main workflows:

1. **Build and Test**: Builds and tests both frontend and backend applications
2. **Deploy**: Deploys the application to AWS EC2 (Free Tier)
3. **Configure Environment**: Sets up environment-specific configurations

### Workflow Triggers

- **Build and Test**: Triggered on push to main/master/develop branches and on pull requests
- **Deploy**: Triggered automatically after successful completion of the Build and Test workflow on main/master branches
- **Configure Environment**: Triggered manually from the GitHub Actions tab

## Setting Up the Pipeline

### Prerequisites

1. **GitHub Repository**: Push your code to a GitHub repository
2. **AWS Account**: Create an AWS account with Free Tier access
3. **AWS Resources**:
   - EC2 instance (t2.micro)
   - RDS database (db.t3.micro)
   - Security groups configured for your application

### GitHub Secrets

Add the following secrets to your GitHub repository:

- `SSH_PRIVATE_KEY`: Private SSH key for connecting to EC2
- `KNOWN_HOSTS`: SSH known_hosts entry for your EC2 instance
- `EC2_HOST`: EC2 instance public DNS or IP
- `EC2_USERNAME`: EC2 instance username (usually 'ec2-user' or 'ubuntu')
- `PROD_DB_URL`: JDBC URL for your RDS database
- `PROD_DB_USERNAME`: RDS database username
- `PROD_DB_PASSWORD`: RDS database password
- `PROD_API_URL`: URL for the backend API

### EC2 Instance Setup

1. Launch a t2.micro EC2 instance with Amazon Linux 2 or Ubuntu
2. Configure security groups to allow HTTP/HTTPS traffic
3. Set up SSH access for GitHub Actions

## AWS Free Tier Resources

This CI/CD pipeline is designed to work with AWS Free Tier resources:

- **EC2**: t2.micro instance for hosting the application
- **RDS**: db.t3.micro PostgreSQL database
- **S3**: Optional for storing static assets

## Local Development

### Frontend

```bash
cd todo-frontend
npm install
npm start
```

### Backend

```bash
cd todo-backend
./mvnw spring-boot:run
```

## Deployment

The application is automatically deployed to AWS EC2 when changes are pushed to the main/master branch and all tests pass.

For manual deployment:

1. Go to the "Actions" tab in your GitHub repository
2. Select the "Configure Environment" workflow
3. Click "Run workflow" and select the environment (production)
4. After the environment is configured, the application will be deployed automatically

## Documentation

For more detailed information, see:

- [GitHub Actions Guide](GITHUB-ACTIONS-GUIDE.md): Detailed guide on using GitHub Actions
- [AWS Deployment](AWS-DEPLOYMENT.md): Information about AWS resources and configuration
- [Deployment Guide](DEPLOYMENT-GUIDE.md): Step-by-step deployment instructions

## Troubleshooting

If you encounter issues with the deployment:

1. Check the GitHub Actions logs for error messages
2. Verify that all secrets are correctly set
3. Check the EC2 instance logs:
   ```bash
   sudo journalctl -u nginx
   sudo tail -f /var/log/nginx/error.log
   ```

## License

[Include your license information here]