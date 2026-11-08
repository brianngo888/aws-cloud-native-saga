# Terms and explanations — examples and verification

## Atomicity (A in ACID)
Either *all* statements in a local DB transaction commit or none do. Example: insert order, two line items and an outbox event; intentionally violate a constraint on line 2. Verify zero rows for that order. A remote Lambda call is NOT rolled back by this DB transaction.

## Consistency (C)
Every committed transaction preserves the invariants encoded by schema constraints and application rules. Example: CHECK(quantity > 0), foreign keys and allowed order states. It does not mean that replicas or different microservices are instantly synchronized.

## Isolation (I)
Concurrent transactions must follow the guarantees of the selected isolation level. PostgreSQL READ COMMITTED does not automatically prevent all read-modify-write races. Test with two concurrent buyers for one item. Use a conditional UPDATE ... WHERE available >= requested or a suitable lock, then assert no negative stock.

## Durability (D)
After commit, DB crash/restart should not erase acknowledged committed changes under the configured durability guarantees. PostgreSQL WAL and managed backups help, but durability is not a promise against every region-wide disaster. Test persistence after restarting a database; separately test backup restoration.

## Saga
A sequence of local transactions across services, with compensating business actions after failures. Reserve -> authorize -> ship; if shipping fails: void authorization -> release stock. Compensation is a new operation and may itself fail. No global serializable transaction is implied.

## Idempotency
Repeating the same request has the same intended business effect. Example: payment authorization with operationId `pay-order-123`; retry must not charge twice. A GET request being repeatable is not enough: business commands must persist operation records atomically.

## Outbox
Insert a business record and an event in the same local database transaction. A dispatcher publishes the event later, retrying safely. Fixes the DB-commit-versus-remote-call crash window; consumers still require deduplication.

## Eventual consistency
The order API can report PROCESSING while inventory/payment services have newer local state. The system converges if retries and compensations complete. Define a timeout and REQUIRES_ATTENTION state for non-convergence.

## EKS / Helm / Terraform
EKS manages Kubernetes control plane; you operate workloads and many cluster add-ons. Helm templates and releases Kubernetes manifests. Terraform manages infrastructure and remote state; avoid two controllers managing the same resource.

## OIDC / IRSA / Pod Identity
OIDC allows GitLab jobs to exchange a short-lived identity token for scoped AWS credentials. EKS Pod Identity gives pods AWS credentials without static secrets (when correctly configured). These are different trust relationships.

## SAST / SCA / CVE / SBOM
SAST examines source patterns; SCA inventories third-party dependencies and known vulnerabilities; CVE is a public vulnerability identifier, not a severity; SBOM lists software components. A clean scan is not proof of no vulnerability.

## SLI / SLO / SLA / RTO / RPO
SLI: measured reliability signal (e.g. request success rate). SLO: internal objective (e.g. 99.9%). SLA: contractual promise. RTO: maximum acceptable restore time. RPO: maximum acceptable data-loss window. Test all claims rather than equating configuration with results.

## Trace ID / correlation ID
A trace ID connects distributed spans; a correlation ID is a business/request identifier that may persist across async boundaries. Propagate W3C traceparent and explicitly map IDs to Step Functions executions.
