# Lab 02 — Saga, idempotency and failure injection (6–11 hours)

## Business sequence
Create order -> reserve stock -> authorize simulated payment -> create shipment -> complete.

## Failure matrix
| Injection | Expected result | Evidence |
|---|---|---|
| Duplicate POST same key | one order | DB unique index and API response |
| Same key different payload | 409 conflict | API test |
| DB commit succeeds, process dies before publish | outbox dispatcher resumes | pending event eventually processed |
| Reserve stock succeeds, Lambda times out | retry does not reserve twice | operation ledger |
| Payment succeeds, response lost | retry does not authorize twice | authorization ID unchanged |
| Shipping fails | void payment, release stock | Step Functions history + DB states |
| Void fails temporarily | retries; REQUIRES_ATTENTION if exhausted | alert and operator runbook |
| Duplicate compensation | no additional release | idempotency operation ledger |
| Out-of-order event | state transition rejected | test output |

## Workflow review
1. Validate `workflows/order-saga.asl.json` in AWS Step Functions console or CLI validation.
2. Confirm every Task has explicit timeout, retry with backoff/jitter policy where appropriate, and catch path.
3. Add distinct business errors (out of stock) vs transient errors (throttle/network timeout).
4. Persist status transitions and reconciliation record; a state machine execution completing is not equivalent to business settlement.
5. Inject each row above and save execution history and state changes.

## Exit questions
Why do compensations require idempotency? What if a void succeeds but acknowledgement is lost? How do you reconcile stuck executions? What does Step Functions Standard guarantee and not guarantee?
