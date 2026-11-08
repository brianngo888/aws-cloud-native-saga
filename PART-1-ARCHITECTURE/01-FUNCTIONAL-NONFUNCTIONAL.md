# Functional and non-functional requirements

| ID | Requirement | Acceptance test |
|---|---|---|
| F01 | Create order with idempotency key | Retry same key/payload: same order ID; key with different payload: 409 |
| F02 | Atomic order + lines + outbox write | Force failure on line 2; zero partial rows |
| F03 | Reserve inventory | 100 requests for 1 remaining unit -> exactly 1 successful reservation |
| F04 | Authorize payment exactly once in business effect | Duplicate operation ID cannot double-authorize |
| F05 | Ship only after payment and reservation | Reject out-of-order transition |
| F06 | Compensate failed shipment | Void payment then release stock; durable status tracked |
| F07 | Expose order lifecycle | Pending/Processing/Completed/Cancelled/RequiresAttention |
| F08 | Operator reconciliation | Stuck compensations are queryable and replay-safe |
| F09 | Cancel within allowed state transitions | Rejected transitions return a domain error |
| F10 | Correlate requests and workflow | Trace ID, operation ID and execution ARN visible |

| ID | Proposed target | How to verify |
|---|---|---|
| N01 | API acceptance p95 <300ms at 100 requests/s | k6; excludes async fulfillment; load profile must be recorded |
| N02 | 99.9% monthly API availability objective | Multi-AZ design, probes, SLIs, incident drills; not guaranteed by topology |
| N03 | Zero double business authorizations | duplicate/retry/lost-ack chaos tests |
| N04 | Zero overselling | concurrent conditional write test |
| N05 | Critical unapproved CVEs block release | SCA, image scan, policy gate; allow signed exceptions |
| N06 | No long-lived CI AWS credentials | GitLab OIDC role assumption |
| N07 | Every stuck compensation alertable within 5 minutes | alert drill |
| N08 | Restore verified, not just backups enabled | restore exercise; RPO/RTO chosen after business analysis |
| N09 | Rollback preserves schema compatibility | deploy previous image and test backward-compatible migration |
| N10 | Bounded cloud spend | budget alert and destroy checklist |

## Non-goals
Real payment gateway, PCI handling, customer PII, globally distributed multi-region HA, verified go-live readiness.
