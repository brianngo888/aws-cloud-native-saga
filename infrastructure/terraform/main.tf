provider "aws" { region = var.region }
data "aws_caller_identity" "current" {}
locals { name = "${var.project}-${var.environment}" }
module "vpc" {
 source = "terraform-aws-modules/vpc/aws"
 version = "~> 6.0"
 name = local.name
 cidr = "10.42.0.0/16"
 azs = slice(data.aws_availability_zones.available.names,0,2)
 private_subnets = ["10.42.1.0/24","10.42.2.0/24"]
 public_subnets = ["10.42.101.0/24","10.42.102.0/24"]
 enable_nat_gateway = true
 single_nat_gateway = var.environment != "prod"
 enable_dns_hostnames = true
 public_subnet_tags = { "kubernetes.io/role/elb" = "1" }
 private_subnet_tags = { "kubernetes.io/role/internal-elb" = "1" }
}
data "aws_availability_zones" "available" { state = "available" }
module "eks" {
 source = "terraform-aws-modules/eks/aws"
 version = "~> 21.0"
 name = local.name
 kubernetes_version = var.kubernetes_version
 endpoint_public_access = true
 endpoint_public_access_cidrs = var.admin_cidrs
 vpc_id = module.vpc.vpc_id
 subnet_ids = module.vpc.private_subnets
 enable_cluster_creator_admin_permissions = true
 eks_managed_node_groups = {
  general = { instance_types=["t3.large"]; min_size=2; max_size=4; desired_size=2 }
 }
}
resource "aws_ecr_repository" "order" {
 name = "${local.name}-order"
 image_tag_mutability = "IMMUTABLE"
 image_scanning_configuration { scan_on_push = true }
 encryption_configuration { encryption_type = "AES256" }
}
resource "aws_dynamodb_table" "saga" {
 name = "${local.name}-saga"
 billing_mode = "PAY_PER_REQUEST"
 hash_key = "pk"
 attribute { name="pk"; type="S" }
 point_in_time_recovery { enabled = true }
}
resource "aws_iam_role" "lambda" {
 name = "${local.name}-lambda"
 assume_role_policy = jsonencode({Version="2012-10-17",Statement=[{Effect="Allow",Principal={Service="lambda.amazonaws.com"},Action="sts:AssumeRole"}]})
}
resource "aws_iam_role_policy_attachment" "lambda_logs" {
 role = aws_iam_role.lambda.name
 policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
resource "aws_iam_role_policy" "lambda_db" {
 name="dynamodb";role=aws_iam_role.lambda.id
 policy=jsonencode({Version="2012-10-17",Statement=[{Effect="Allow",Action=["dynamodb:PutItem"],Resource=aws_dynamodb_table.saga.arn}]})
}
resource "aws_lambda_function" "saga" {
 function_name="${local.name}-saga"
 role=aws_iam_role.lambda.arn
 runtime="java21"
 handler="com.example.saga.SagaHandler::handleRequest"
 filename=var.lambda_zip_path
 source_code_hash=filebase64sha256(var.lambda_zip_path)
 timeout=20
 memory_size=512
 environment { variables={SAGA_TABLE=aws_dynamodb_table.saga.name} }
}
resource "aws_iam_role" "step" {
 name="${local.name}-step"
 assume_role_policy=jsonencode({Version="2012-10-17",Statement=[{Effect="Allow",Principal={Service="states.amazonaws.com"},Action="sts:AssumeRole"}]})
}
resource "aws_iam_role_policy" "step_lambda" {
 name="invoke";role=aws_iam_role.step.id
 policy=jsonencode({Version="2012-10-17",Statement=[{Effect="Allow",Action=["lambda:InvokeFunction"],Resource=aws_lambda_function.saga.arn}]})
}
resource "aws_sfn_state_machine" "order" {
 name="${local.name}-order"
 role_arn=aws_iam_role.step.arn
 type="STANDARD"
 definition=templatefile("${path.module}/../../workflows/order-saga.asl.json",{saga_lambda_arn=aws_lambda_function.saga.arn})
}
output "cluster_name" { value=module.eks.cluster_name }
output "ecr_url" { value=aws_ecr_repository.order.repository_url }
output "saga_arn" { value=aws_sfn_state_machine.order.arn }
