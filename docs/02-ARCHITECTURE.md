# Architecture, boundaries, and decisions

Client -> EKS Ingress/API -> Order WebFlux -> RDS PostgreSQL -> Step Functions -> Lambda operations -> DynamoDB.
ECR stores immutable container images; GitLab builds and scans; Terraform provisions foundational AWS resources.

## Important boundaries
- R2DBC transaction commits only PostgreSQL changes. It does not atomically start a Step Functions execution.
- Use a **transactional outbox** with a publisher and deduplication for production; current sample deliberately lacks this.
- Each Lambda records a per-order operation in DynamoDB. Current sample does not maintain real inventory counters or process real payments.
- The sample does not synchronize Step Functions terminal status back to PostgreSQL.
- EKS workloads require IAM permission for `states:StartExecution` via EKS Pod Identity, not node-wide credentials.
- API Gateway, external ingress, WAF, RDS, GitLab OIDC roles, and runtime observability must be added before real go-live.

## ADR-001: Orchestration
Step Functions Standard provides durable history and explicit compensation branches. Cost and AWS coupling are accepted.

## ADR-002: Database
PostgreSQL supports relational invariants and isolation demonstrations. DynamoDB provides idempotency records and conditional writes.

## ADR-003: Delivery
Terraform owns cloud infrastructure; Helm owns application deployments; GitLab controls promotion.
