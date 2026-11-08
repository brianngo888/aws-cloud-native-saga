# GitLab CI/CD and security implementation plan

Current `.gitlab-ci.yml` implements Terraform validation, unit test, Docker build, and Trivy scanning.
It does **not** yet publish to ECR or deploy to EKS. Those stages require GitLab OIDC federation
and scoped IAM roles, plus runner configuration. The Docker-in-Docker job needs a suitably
configured runner; avoid privileged runners for shared untrusted workloads.

## Required production jobs
- Secret detection, SAST (Semgrep), SCA (OSV/Trivy), SBOM (CycloneDX or Syft), IaC scan (Checkov), image scan.
- Fail release for unapproved critical/high vulnerabilities; track risk-accepted exceptions and expiry.
- Authenticate GitLab to AWS using OIDC, assume short-lived role, never store AWS keys in GitLab.
- Push image to ECR, resolve immutable digest, deploy via Helm to staging, run smoke tests.
- Manual approval for prod; rollback via Helm for code only; DB schema rollback uses expand/contract.
- Enable ECR enhanced scanning / Inspector where supported and centralize findings.

## Evidence
Store test reports, scan results, SBOM, deployment digest, GitLab job URL, and sign-off record.
No vulnerability scanner guarantees a vulnerability-free product.
