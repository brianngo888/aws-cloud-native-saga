# Architecture decisions and threat model

## ADR-001: EKS for Java API, Lambda for short activities
EKS is requested for hands-on Kubernetes operations. This adds nodes, upgrades, networking, IAM and add-on ownership. Lambda avoids idle compute for short stateless activities. Alternative: ECS Fargate plus Step Functions is simpler for many real teams.

## ADR-002: PostgreSQL + R2DBC
Relational constraints and local transactions teach ACID. R2DBC is non-blocking but transaction context must remain within a Reactor subscription; never call subscribe() inside business services. A blocking JDBC client would need bounded elastic scheduling and loses end-to-end nonblocking benefits.

## ADR-003: Step Functions Standard
Durable orchestration and explicit retries/compensations. Standard workflows are at-least-once in effect for some integration/retry situations: design idempotent activities. Never assume one invocation means one side effect.

## ADR-004: Outbox before Step Functions start
Write order and outbox atomically; dispatcher uses deterministic execution name. Duplicate starts and ambiguous network outcomes must be reconciled. A Step Functions execution name alone does not replace business-level idempotency.

## ADR-005: GitLab OIDC, ECR digest and scoped IAM
No AWS access keys in GitLab variables. Validate issuer, audience, project/ref conditions in AWS trust policy. Deploy immutable image digest, not `latest`.

## Threats to address
1. Internet -> ingress: TLS, authentication, authorization, rate limiting, request limits.
2. GitLab runner -> AWS: restrict trust conditions, protect branches/environments, review Terraform plan.
3. Pod -> AWS: per-service Pod Identity, least privilege, no broad wildcard IAM.
4. Pod -> DB: Secrets Manager, TLS, SG isolation, rotate credentials.
5. Supply chain: pin dependencies, SCA, image scanning, SBOM, provenance, signed releases where feasible.
6. Operations: audit logs without secrets or card data, alert on dead-letter/reconciliation backlog.

## Availability and cost
Multi-AZ EKS nodes and RDS improve resilience but do not prove 99.9%. Budget for EKS control plane, NAT gateways, worker nodes, RDS, CloudWatch, data transfer, and Step Functions transitions. Destroy idle labs promptly. Do not expose a database publicly.
