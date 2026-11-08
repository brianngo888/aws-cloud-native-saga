# Part 2 — Source map and implementation status

The included code is the earlier 39-file starter, with a working-looking structure but **NOT an end-to-end verified production application**. Inspect these paths:

- `services/order-service/` — Java 25 Spring WebFlux/R2DBC REST API; `OrderService.java` currently writes an order and then starts Step Functions. **Missing transactional outbox, idempotency key, production-grade auth, migration tooling, structured observability.**
- `lambdas/` — Java Lambda example. **Runtime packaging and AWS runtime compatibility require verification.**
- `workflows/order-saga.asl.json` — Step Functions definition. Verify all error and compensation paths.
- `infrastructure/terraform/` — EKS/VPC/ECR/DynamoDB/Step Functions example. **RDS, GitLab OIDC trust, Pod Identity, state backend, secrets and production controls are incomplete.**
- `deploy/helm/` — starter Kubernetes manifests; probe and security settings need review.
- `.gitlab-ci.yml` — validate/test/build/scan scaffold. **Publish and deploy are intentionally disabled.**

## Mandatory implementation order
1. Create RDS PostgreSQL and private access; apply migrations.
2. Implement order + line + outbox atomic write and idempotency uniqueness constraint.
3. Add outbox dispatcher with retries, leasing and reconciliation.
4. Implement inventory conditional reserve/release; payment authorize/void; shipping create; all idempotent.
5. Connect workflow input/output and final order status callback/reconciler.
6. Add tests for each failure window, including duplicate messages and compensation failure.
7. Add GitLab OIDC, scoped IAM, ECR publish, digest Helm deployment and rollback.
8. Add monitoring, threat model review, restore tests and runbooks.

**Do not interpret these TODOs as implemented.** The workbook specifies evidence required to complete them.
