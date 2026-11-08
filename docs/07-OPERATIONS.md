# Operations, observability and recovery

## SLO proposal
API order acceptance p95 < 300ms, availability 99.9% measured monthly, zero known duplicate captures.
Measure in staging before accepting targets.

## Required telemetry
Traceparent propagation, orderId and executionArn in structured logs (exclude PII),
metrics for saga success/failure/compensation duration, RDS saturation, Lambda errors,
EKS restart counts, HTTP 5xx, and security findings.

## Runbook: stuck saga
1. Find orderId and Step Functions execution ARN.
2. Inspect last completed operation and durable idempotency records.
3. Determine whether payment side effect committed using provider ledger.
4. Reconcile missing operations; never blindly replay payment.
5. Document recovery action, operator, and customer outcome.

## Runbook: bad release
Stop rollout; `helm history order`; `helm rollback order <revision>`;
verify readiness, error rate, and compatibility with schema changes.

## Go-live checklist
Multi-AZ and failover drills; DB backup/restore; WAF/ingress; Pod Identity; network policies;
image signatures; vulnerability exception review; penetration testing; load testing;
runbook drills; monitoring alerts; on-call; budget alerts; documented RTO/RPO.
