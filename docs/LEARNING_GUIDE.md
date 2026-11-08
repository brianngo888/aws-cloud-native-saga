# Learning guide — AWS cloud-native distributed transactions

## 1. Requirements
Functional: create order; reserve stock; authorize payment; arrange shipment; cancel/compensate; query order status. Acceptance: no oversell; no duplicate charge; traceable state transitions.
Non-functional learning targets: p95 order acceptance <300ms under a defined test load; availability design goal 99.9%; least-privilege IAM; structured logs/traces; immutable deployments; security gates. These are targets, not validated outcomes.

## 2. Architecture decisions
- Order API: Java 25 Spring WebFlux on EKS.
- Order data: PostgreSQL with R2DBC for local ACID.
- Inventory/payment state: DynamoDB conditional writes and idempotency keys.
- Distributed workflow: Step Functions Standard; Lambda for short-lived tasks.
- Infrastructure: Terraform; app deployments: Helm/GitLab CI.
- Identity: GitLab OIDC for CI, EKS Pod Identity for supported workloads.

## 3. ACID labs
**Atomicity:** Insert order and items in one R2DBC transaction; force item constraint failure; assert no records committed.
**Consistency:** Enforce positive quantity and valid order status; assert invalid writes rejected.
**Isolation:** Start stock=1, issue 100 simultaneous reservations; verify one success; compare READ COMMITTED and SERIALIZABLE and retry serialization errors.
**Durability:** Commit order, restart app, query order; separately discuss database crash recovery, backups and RPO.

## 4. Reactor labs
Explain Mono vs Flux; compare map/flatMap/concatMap; avoid blocking event loops; bridge AWS SDK async CompletableFuture with Mono.fromFuture; demonstrate error mapping and timeout behavior.

## 5. Saga labs
Reserve -> authorize -> ship -> capture -> complete. On failure, reconcile outcome and compensate in reverse order. A timeout is ambiguous: check durable operation status before retry. Compensation is a new business transaction, not a global rollback.

## 6. Terraform labs
Bootstrap state bucket and locking, then provision VPC, EKS, ECR, RDS, DynamoDB, IAM, Lambda, Step Functions. Apply least privilege, encryption and tagging. Review terraform plan before apply.

## 7. GitLab CI/CD labs
Validate -> unit/integration tests -> SAST/SCA/secrets/IaC -> image build/SBOM -> image scan -> ECR push -> Helm deploy -> smoke tests -> rollback. Use OIDC short-lived AWS credentials; pin tool versions and images.

## 8. Security labs
Demonstrate CVE detection in an isolated branch; fail policy gate on unapproved critical findings; review high findings and exceptions; scan images and Terraform; verify non-root pods, RBAC, secret storage, audit logs. Passing scans do not guarantee safety.

## 9. Observability and operations
Correlate orderId, saga execution ID, traceId; instrument OpenTelemetry; create CloudWatch alarms; inject duplicate calls, timeouts, payment response loss, pod termination, and compensation failures. Record runbooks and recovery evidence.

## 10. Completion evidence
Each chapter: What / Why / How / Code / Failure / Verification / Trade-offs. Attach test output, workflow history, Terraform plan, deployment evidence and scan reports.

## 11. Suggested sequence
First establish secure AWS infrastructure and GitLab deploy pipeline; next implement order ACID, then Saga; finally security gates, observability and resilience verification. Expect ~47 focused hours plus AWS setup/troubleshooting.
