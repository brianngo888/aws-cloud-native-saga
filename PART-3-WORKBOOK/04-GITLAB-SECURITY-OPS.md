# Lab 04 — GitLab CI/CD, DevSecOps and operations (12–16 hours)

## CI/CD stages
1. `terraform fmt/validate` + tflint/checkov (pinned versions).
2. Java unit + integration tests, Jacoco threshold as appropriate; Gradle dependency scanning (SCA).
3. SAST and secrets detection; block known test secret fixture.
4. Build container from pinned base image; generate SBOM; scan with Trivy/Grype; fail on unapproved critical findings.
5. GitLab OIDC to scoped AWS role; verify audience/project/ref conditions and short-lived credentials.
6. Push immutable digest to ECR; scan ECR findings; deploy via Helm with digest.
7. Readiness/smoke tests; controlled rollout; rollback with Helm and DB-compatible migration.
8. Record commit SHA, image digest, scan report, change approval and deployment outcome.

## Security gate test
Introduce a deliberately vulnerable test-only dependency in an isolated branch. Expected pipeline red. Remove it; expected pipeline green. Scan results are evidence of policy enforcement, not proof of complete safety.

## Observability
Instrument HTTP, R2DBC, Step Functions execution ID and Lambda logs. Record trace ID, correlation ID and business operation ID; do not log secrets or card data. Configure latency/error/availability SLIs and alarm for stuck compensation and outbox age.

## Rollback drill
Deploy v2; inject a failing health check; confirm no traffic cutover to bad pods and rollback to v1. Verify DB schema remains compatible. Capture elapsed recovery time, error rate and evidence.

## Disaster recovery drill
Restore DB snapshot to a separate environment, verify checksum/sample orders and document observed RTO/RPO. Backups existing is not the same as tested restore.

## Exit questions
What is the trust boundary of GitLab OIDC? Why scan dependencies and image separately? Why deploy by digest? What happens to a backward-incompatible DB migration on rollback?
