# AWS Deployment with GitHub Actions - Step by Step Guide

This guide provides detailed instructions for setting up CI/CD with GitHub Actions to deploy your Todo application to AWS.

## Architecture Overview

The deployment architecture consists of:

1. **Frontend**: React application hosted on S3 and distributed via CloudFront
2. **Backend**: Spring Boot application deployed to Elastic Beanstalk
3. **CI/CD**: GitHub Actions workflow that builds and deploys both components

```
┌────────────────┐     ┌─────────────┐     ┌────────────────┐     ┌───────────────┐
│                │     │             │     │                │     │               │
│  GitHub Repo   │────▶│  GitHub     │────▶│  AWS Services  │────▶│  End Users    │
│                │     │  Actions    │     │                │     │               │
└────────────────┘     └─────────────┘     └────────────────┘     └───────────────┘
                                                   │
                              ┌──────────────────┬─┴───────────────┐
                              │                  │                 │
                      ┌───────▼──────┐   ┌───────▼─────┐   ┌───────▼─────┐
                      │              │   │             │   │             │
                      │  S3 Bucket   │   │ CloudFront  │   │ Elastic     │
                      │  (Frontend)  │   │ (CDN)       │   │ Beanstalk   │
                      │              │   │             │   │ (Backend)   │
                      └──────────────┘   └─────────────┘   └─────────────┘
```

## Step 1: AWS Account Setup

Before you begin, ensure you have:
1. An AWS account with billing set up
2. Administrative access to create resources
3. AWS CLI installed locally (for manual setup)

## Step 2: AWS Resources Setup

### Using the Provided CloudFormation Template

1. **Sign in to AWS Console**:
   - Go to https://console.aws.amazon.com/
   - Sign in with your credentials

2. **Navigate to CloudFormation**:
   - Search for "CloudFormation" in the AWS console
   - Click on "Create stack" > "With new resources"

3. **Upload Template**:
   - Select "Upload a template file"
   - Click "Choose file" and select the `aws-infrastructure.json` file
   - Click "Next"

4. **Specify Stack Details**:
   - Enter a stack name (e.g., "todo-app-stack")
   - Enter parameters:
     - **FrontendBucketName**: A globally unique name for your frontend bucket (e.g., "my-todo-app-frontend-123")
     - **BackendBucketName**: A globally unique name for your backend bucket (e.g., "my-todo-app-backend-123")
     - **PriceClass**: Select the CloudFront price class based on your needs

5. **Configure Stack Options**:
   - Leave defaults or add tags if needed
   - Click "Next"

6. **Review**:
   - Check the "I acknowledge that AWS CloudFormation might create IAM resources" box
   - Click "Create stack"

7. **Wait for Completion**:
   - This process takes 15-20 minutes
   - When status shows "CREATE_COMPLETE", proceed to the next step

8. **Note the Outputs**:
   - Go to the "Outputs" tab of your stack
   - Record all the values (you'll need them for GitHub)

### Alternative: Using the Deployment Script

If you prefer to use the provided script:

1. Make the script executable:
   ```bash
   chmod +x deploy-aws-infrastructure.sh
   ```

2. Run the script:
   ```bash
   ./deploy-aws-infrastructure.sh
   ```

3. Follow the interactive prompts to create your resources.

## Step 3: GitHub Repository Setup

1. **Push Your Code**:
   - Ensure your Todo application code is in a GitHub repository
   - Make sure the repository structure matches the expected paths in the workflow file

2. **Add GitHub Secrets**:
   - Go to your GitHub repository
   - Navigate to Settings > Secrets and variables > Actions
   - Click "New repository secret"
   - Add each of these secrets:

   | Secret Name | Value (from CloudFormation outputs) |
   |-------------|-------------------------------------|
   | `AWS_ACCESS_KEY_ID` | AccessKeyId output |
   | `AWS_SECRET_ACCESS_KEY` | SecretAccessKey output |
   | `AWS_REGION` | The region you deployed to (e.g., us-east-1) |
   | `AWS_S3_BUCKET` | FrontendBucketName output |
   | `AWS_S3_BUCKET_BACKEND` | BackendBucketName output |
   | `AWS_CLOUDFRONT_DISTRIBUTION_ID` | CloudFrontDistributionId output |

## Step 4: GitHub Actions Workflow

1. **Create Workflow Directory**:
   - Create a `.github/workflows` directory in your repository if it doesn't exist

2. **Create Workflow File**:
   - Create a file named `aws-deploy.yml` in the workflows directory
   - Copy the workflow content from the provided file

3. **Commit and Push**:
   - Commit these changes to your repository
   - Push to GitHub to trigger the workflow

## Step 5: Monitor Deployment

1. **Check Workflow Status**:
   - Go to the "Actions" tab in your GitHub repository
   - You should see the "Deploy to AWS" workflow running

2. **Troubleshoot if Needed**:
   - If the workflow fails, check the logs for error messages
   - Common issues include:
     - Incorrect secrets
     - Missing permissions
     - Resource naming conflicts

3. **Verify Deployment**:
   - Once the workflow completes successfully:
     - Frontend: Access via the CloudFront URL (CloudFrontDomainName output)
     - Backend: Access via the Elastic Beanstalk URL (ElasticBeanstalkEnvironmentURL output)

## Step 6: Configure Application Settings

### Backend Configuration

1. **Environment Variables**:
   - In the AWS Console, go to Elastic Beanstalk
   - Select your environment ("todo-app-env")
   - Go to Configuration > Software
   - Add environment properties for:
     - `SPRING_PROFILES_ACTIVE=prod`
     - Database connection details (if using external DB)
     - Any other environment-specific settings

2. **Database Setup**:
   - If your application uses a database, set up an RDS instance
   - Configure security groups to allow access from Elastic Beanstalk

### Frontend Configuration

1. **API Endpoint**:
   - Update your frontend API configuration to point to the Elastic Beanstalk URL
   - For React applications, you can use environment variables at build time

## Step 7: Custom Domain Setup (Optional)

1. **Register Domain**:
   - Use Route 53 or another domain registrar

2. **Create SSL Certificate**:
   - Use AWS Certificate Manager to create a certificate

3. **Update CloudFront**:
   - Add your custom domain and certificate to the CloudFront distribution

4. **Create DNS Records**:
   - Create CNAME or ALIAS records pointing to your CloudFront distribution

## Maintenance and Updates

1. **Continuous Deployment**:
   - Any push to the main branch will trigger a new deployment

2. **Manual Deployment**:
   - You can manually trigger the workflow from the Actions tab

3. **Monitoring**:
   - Set up CloudWatch alarms for monitoring
   - Enable S3 and CloudFront access logs

4. **Cost Management**:
   - Monitor AWS costs regularly
   - Consider setting up AWS Budget alerts

## Security Best Practices

1. **IAM Permissions**:
   - Review and restrict the permissions of the deployment user
   - Follow the principle of least privilege

2. **Secret Rotation**:
   - Rotate your AWS access keys periodically
   - Update the GitHub secrets when keys are rotated

3. **HTTPS**:
   - Ensure all traffic uses HTTPS
   - Configure proper security headers

4. **Content Security Policy**:
   - Implement CSP headers for your frontend

## Troubleshooting Common Issues

1. **Deployment Fails**:
   - Check GitHub Actions logs
   - Verify AWS credentials are correct
   - Ensure resource names are unique

2. **Frontend Loads but API Calls Fail**:
   - Check CORS configuration
   - Verify API endpoint URLs
   - Check network requests in browser console

3. **Backend Application Errors**:
   - Check Elastic Beanstalk logs
   - Verify environment variables
   - Check application health dashboard

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/)
- [S3 Static Website Hosting](https://docs.aws.amazon.com/AmazonS3/latest/dev/WebsiteHosting.html)
- [CloudFront Developer Guide](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/)
- [Elastic Beanstalk Developer Guide](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/)