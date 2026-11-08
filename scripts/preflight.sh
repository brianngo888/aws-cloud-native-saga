#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 -m json.tool workflows/order-saga.asl.json >/dev/null
if command -v terraform >/dev/null; then terraform fmt -check -recursive infrastructure/terraform; fi
if command -v helm >/dev/null; then helm lint deploy/helm/order-service; fi
if command -v gradle >/dev/null; then (cd services/order-service && gradle --no-daemon test); else echo 'Gradle unavailable: build not verified'; fi
printf 'Preflight complete; AWS deployment and security verification still required.\n'
