# Part 3 — Hands-on workbook: 52-hour suggested plan

| Lab | Hours | Deliverable | Pass/fail evidence |
|---|---:|---|---|
| 01 Requirements and ADR | 3 | architecture and risks | explain five tradeoffs |
| 02 ACID and isolation | 5 | PostgreSQL transaction tests | rollback + concurrency tests |
| 03 WebFlux and Reactor | 5 | reactive API | StepVerifier and error tests |
| 04 Outbox + idempotency | 5 | reliable dispatch | crash-window tests |
| 05 Saga orchestration | 6 | workflow and compensation | failed shipment + failed compensation |
| 06 Terraform and EKS | 8 | AWS cluster and workload | plan, kubectl, cost evidence |
| 07 GitLab CI/CD | 6 | OIDC, ECR, Helm | green pipeline + digest release |
| 08 DevSecOps | 5 | scan and policy gates | known vulnerable fixture blocks |
| 09 Observability + recovery | 5 | dashboards, rollback, alerts | incident drill |
| 10 Final assessment | 4 | evidence pack | architecture review and demo |
| **Total** | **52** | | |

## Prerequisites
AWS account with budget and IAM permissions; GitLab project and runner; Java 25 JDK; Gradle compatible with Java 25; Docker; Terraform; AWS CLI; kubectl; Helm; PostgreSQL client; k6. Configure an AWS Budget before provisioning EKS/RDS/NAT. Never put access keys or credentials in Git.

## Lab sequence
Complete the numbered files in this directory in order. Each lab asks you to capture commands, screenshots or logs as evidence. A green `terraform apply` does not prove the application is secure or available.
