# ACID exercises — PostgreSQL

## Atomicity
BEGIN; INSERT INTO orders VALUES (...); INSERT a row violating CHECK(quantity > 0); ROLLBACK.
Verify neither change remains. In Java, use `TransactionalOperator` and an R2DBC connection.

## Consistency
`CHECK(quantity > 0)` rejects invalid orders. Invariant: inventory never negative.

## Isolation
Two concurrent sessions compete for one unit:
```sql
UPDATE inventory_lab SET available_quantity=available_quantity-1
WHERE product_id='laptop' AND available_quantity>=1;
```
Exactly one row update should succeed. Repeat with READ COMMITTED and SERIALIZABLE,
record serialization failures and retry at the transaction boundary.

## Durability
Commit a row; restart the API process and verify the row. This proves persistence across
app restart, NOT resilience to storage-region failure. For stronger evidence test RDS failover and restore.

## Critical distinction
ACID consistency is not distributed read consistency. A Saga is not globally atomic or isolated.
