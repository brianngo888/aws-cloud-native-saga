variable "region" { type=string; default="us-east-2" }
variable "project" { type=string; default="saga-lab" }
variable "environment" { type=string; default="dev" }
variable "kubernetes_version" { type=string; description="Choose a version supported by EKS in your region" }
variable "admin_cidrs" { type=list(string); description="Restrict EKS public endpoint to trusted IP ranges" }
variable "lambda_zip_path" { type=string; description="Absolute path to lambdas.zip" }
