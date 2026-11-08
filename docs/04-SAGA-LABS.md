# Saga practical exercises

1. Build Lambda archive: `cd lambdas && gradle lambdaZip`.
2. Terraform apply with the archive path, EKS version, and your trusted CIDR.
3. Start Step Functions manually with:
```json
{"orderId":"demo-001","productId":"laptop","quantity":1,"amount":100,"simulateFailure":false}
```
4. Repeat with a new orderId and `simulateFailure:true` to trigger Ship -> Void -> Release -> Fail.
5. Inspect execution history and DynamoDB records for `orderId#action`.
6. Retry the same action to verify `attribute_not_exists(pk)` deduplication.

**Scope warning:** the example Lambda only records actions; it does not debit stock or transfer money.
For production implement reservation state machine, payment provider idempotency, outbox, reconciliation,
compensation retries, and DLQ/operations escalation. Step Functions may retry an action after its side effect.
