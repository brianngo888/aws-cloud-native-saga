# Solution Architect practical assessment — evidence-based

## Demo script (30–45 minutes)
1. Draw system context and data ownership; name synchronous and asynchronous boundaries.
2. Explain ACID using one failed SQL transaction and a concurrency test.
3. Show successful Saga, then shipping failure and compensation.
4. Demonstrate lost acknowledgement, retries and idempotency record.
5. Show Terraform plan, private network design, IAM roles and EKS pod placement.
6. Show GitLab scan failing on a deliberately vulnerable fixture and successful signed-off deployment.
7. Show traces, SLO dashboard, incident alert, Helm rollback and backup restore evidence.
8. Explain cost and operational alternatives (ECS vs EKS, Lambda vs containers, choreography vs orchestration).

## Scoring (0–4 each; 40 total)
Requirements, architecture tradeoffs, ACID, Saga, concurrency, infrastructure, CI/CD, security, observability, recovery. 0=no evidence; 1=definition; 2=working demo; 3=failure-tested; 4=can explain limitations and alternatives. Target >=32 with no score below 2 before claiming lab proficiency. This rubric does not guarantee job readiness or production approval.

## Evidence pack
Store anonymized test output, AWS resource diagram, Terraform plan summary, CI pipeline URLs, SBOM, scan report, Step Functions execution history, CloudWatch dashboards, k6 report, incident runbook and restore record. Never commit AWS secrets, tokens, database passwords or customer data.
