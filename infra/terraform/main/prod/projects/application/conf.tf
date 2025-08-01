terraform {
  required_version = "1.12.2"
  backend "s3" {
    region = "ap-northeast-2"

    bucket  = "carparazzi-infrastructure"
    key     = "infrastructure/terraform/main/prod/projects/service_http_api"
    encrypt = true

    dynamodb_table = "carparazzi-terraform-lock"

  }
  required_providers {
    aws = {
      version = "6.0.0"
      source  = "hashicorp/aws"
    }
  }
}

provider "aws" {
  region              = "ap-northeast-2"
}
