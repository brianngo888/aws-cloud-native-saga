# Requirements and acceptance criteria

## Functional
FR-01 Create order with product, quantity and amount; return a durable order ID.
FR-02 Reject invalid quantities and amounts.
FR-03 Reserve inventory without overselling.
FR-04 Authorize, ship, capture, and compensate failed workflows.
FR-05 Preserve operation idempotency across retries.
FR-06 Provide queryable order and execution status (status synchronization is **not implemented** in starter code).

## Non-functional targets (not verified SLAs)
NFR-01 Order acceptance p95 < 300 ms under an agreed load profile.
NFR-02 99.9% availability design target with multi-AZ infrastructure.
NFR-03 No duplicate charge for repeated order operation IDs.
NFR-04 No plaintext credentials in Git.
NFR-05 Every release passes tests and vulnerability gates.
NFR-06 RPO/RTO defined and validated by restore exercises before go-live.
NFR-07 Trace orderId across API, workflow, and Lambdas.
NFR-08 All IaC changes reviewed and reproducible.

## Production exit gates
Security assessment, payment-provider certification, capacity/load test, failure recovery drill,
backup/restore drill, rollback drill, operational on-call ownership, and business sign-off.
