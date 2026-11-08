terraform { required_version=">= 1.9.0"; required_providers { aws={source="hashicorp/aws",version="~> 6.0"} } }
# Production: configure a remote S3 backend with bucket versioning, encryption and locking.
