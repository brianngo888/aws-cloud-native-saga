# Lab 03 — Terraform, AWS, EKS (8 hours minimum)

## Before provisioning
1. Configure AWS Budget alerts; record account ID and region locally (do not commit identifiers).
2. Set a restricted admin CIDR and review `terraform.tfvars.example`.
3. Build Lambda ZIP before Terraform plan, as `filebase64sha256()` requires the artifact.
4. `cd infrastructure/terraform && terraform fmt -recursive && terraform init -backend=false && terraform validate`.
5. Configure a real encrypted remote backend (S3 with versioning and state locking supported by your Terraform version) and scoped CI permissions. Do not share state files.
6. `terraform plan -out=plan.out` then review IAM, network exposure, cost and destroy impact.

## Deployment
7. `terraform apply plan.out` in a dedicated sandbox AWS account.
8. `aws eks update-kubeconfig --region <REGION> --name <CLUSTER>`.
9. `kubectl get nodes -o wide` and `kubectl get pods -A`.
10. Validate DNS, private routing, Pod Identity, secrets, TLS and RDS connectivity before deploying order service.
11. `helm lint deploy/helm/order-service`; deploy immutable ECR digest with Helm after completing CI.
12. Verify probes, `kubectl rollout status`, logs, security contexts, PDB, topology spread and resource limits.

## Safety and cost
The starter Terraform does not yet provision every required production resource. Never run apply expecting full working deployment. EKS control plane, nodes, NAT and RDS can accrue charges while idle. `terraform destroy` only after verifying no shared resources or retained data are needed; confirm residual ECR/logs/backups manually.

## Exit questions
Why private subnets? When do you need NAT versus VPC endpoints? What breaks if the AZ goes down? Why should the API service not use cluster-admin IAM?
