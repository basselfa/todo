# AWS Deployment Guide

This guide explains how to set up AWS resources for deploying your Todo application using GitHub Actions CI/CD.

## 1. Set Up AWS Resources

### Create an S3 Bucket for Static Website Hosting

1. Log in to the [AWS Management Console](https://aws.amazon.com/console/)
2. Go to the S3 service
3. Click "Create bucket"
4. Enter a globally unique bucket name
5. Choose your preferred AWS Region
6. Unblock all public access (since this is a public website)
7. Enable "Static website hosting" under bucket properties
8. Set index document to "index.html" and error document to "index.html"

### Create a CloudFront Distribution

1. Go to the CloudFront service
2. Click "Create Distribution"
3. For "Origin Domain", select your S3 bucket
4. For "Origin Access", choose "Origin access control settings (recommended)"
5. Create a new OAC and accept the defaults
6. Under "Default cache behavior", set:
   - Viewer protocol policy: Redirect HTTP to HTTPS
   - Allowed HTTP methods: GET, HEAD
   - Cache policy: CachingOptimized
7. Under "Settings":
   - Default root object: index.html
   - Standard logging: Off (or enable if you want logs)
8. Click "Create distribution"
9. After creation, note the distribution ID for later

### Create IAM User for Deployments

1. Go to the IAM service
2. Click "Users" and then "Add users"
3. Enter a username like "github-actions-deployer"
4. Select "Access key - Programmatic access"
5. Click "Next: Permissions"
6. Click "Attach existing policies directly"
7. Create a new policy with the following JSON:

```json
{
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
                "arn:aws:s3:::YOUR-BUCKET-NAME",
                "arn:aws:s3:::YOUR-BUCKET-NAME/*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "cloudfront:CreateInvalidation"
            ],
            "Resource": [
                "arn:aws:cloudfront::YOUR-ACCOUNT-ID:distribution/YOUR-DISTRIBUTION-ID"
            ]
        }
    ]
}
```

8. Replace placeholders with your actual bucket name, AWS account ID, and distribution ID
9. Review and create the user
10. **IMPORTANT**: Download the access key ID and secret access key (you'll only see them once)

## 2. Configure GitHub Repository Secrets

1. Go to your GitHub repository
2. Navigate to "Settings" > "Secrets and variables" > "Actions"
3. Add the following secrets:
   - `AWS_ACCESS_KEY_ID`: The access key ID from the IAM user
   - `AWS_SECRET_ACCESS_KEY`: The secret access key from the IAM user
   - `AWS_REGION`: The AWS region you used (e.g., us-east-1)
   - `AWS_S3_BUCKET`: The name of your S3 bucket
   - `AWS_CLOUDFRONT_DISTRIBUTION_ID`: Your CloudFront distribution ID

## 3. Deploy Your Application

Once you've set up the AWS resources and GitHub secrets:

1. Commit and push your code to the main branch
2. GitHub Actions will automatically:
   - Build your application
   - Deploy it to S3
   - Invalidate the CloudFront cache

Your application will be available at your CloudFront domain (e.g., `https://d1234abcd.cloudfront.net`).

## Troubleshooting

### Deployment Fails with Access Denied
- Check that your IAM user has the correct permissions
- Verify that your GitHub secrets are correctly set
- Make sure your S3 bucket policy allows the IAM user to access it

### CloudFront Shows Old Content
- Check if the invalidation completed successfully
- Try a hard refresh in your browser (Ctrl+F5)
- Check CloudFront distribution settings

### Build Errors
- Check your application's build logs in GitHub Actions
- Verify that all dependencies are correctly installed
- Test the build locally before pushing