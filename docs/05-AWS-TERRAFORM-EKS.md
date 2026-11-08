# Real AWS deployment walkthrough

Prerequisites: AWS account with approved IAM permissions, Terraform, AWS CLI, kubectl, Helm,
Docker, JDK 25 for the API, JDK 21 for the Lambda build, Gradle 9.2.1, and cost budget/alerts.

1. Set AWS profile and verify: `aws sts get-caller-identity`.
2. Select a supported EKS Kubernetes version in your target region.
3. Build Lambda zip: `cd lambdas && gradle lambdaZip`.
4. Copy `terraform.tfvars.example` to `terraform.tfvars`; set your actual public IP /32 and absolute zip path.
5. Create and configure encrypted versioned S3 remote state with locking BEFORE shared-team use.
6. `terraform -chdir=infrastructure/terraform init`.
7. `terraform -chdir=infrastructure/terraform plan -out=tfplan` and review all resources and charges.
8. `terraform -chdir=infrastructure/terraform apply tfplan`.
9. `aws eks update-kubeconfig --name <cluster_name> --region <region>`.
10. Build and push the Order image to ECR, capture the pushed sha256 digest.
11. Provision RDS PostgreSQL in private subnets and migrate `schema.sql` (not yet automated here).
12. Configure EKS Pod Identity for Step Functions access, and a DB secret provider.
13. Set `awsRegion` and `sagaArn` to match the Step Functions deployment, then run `helm upgrade --install order deploy/helm/order-service -f values.yaml --set image.repository=... --set image.digest=sha256:...`.
14. `kubectl rollout status deployment/order` and port-forward the service to test `/actuator/health`.
15. Check Step Functions execution and CloudWatch logs.

**Not one-click production:** RDS, EKS Pod Identity association, DB secret provisioning, ingress, GitLab OIDC,
monitoring and network hardening remain explicit implementation tasks. Never apply to production as-is.

Cleanup: `helm uninstall order`; `terraform destroy` only after confirming backups, state, and shared resource ownership.
