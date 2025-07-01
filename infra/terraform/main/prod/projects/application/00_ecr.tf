locals {
  service_http_api = {
    organization = "carparazzi"
    github       = "github.com/drakejin/carparazzi"
    service_name = "service_http_api"
  }
}


resource "aws_ecr_repository" "ecr_service_http_api" {
  name                 = "${local.service_http_api.organization}/${local.service_http_api.service_name}"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = false
  }

  tags = merge(local.tags, {
    Service    = local.service_http_api.service_name
    Repository = local.service_http_api.github
  })
}


resource "aws_ecr_lifecycle_policy" "ecr_service_http_api_policy" {
  repository = aws_ecr_repository.ecr_service_http_api.name

  policy = <<EOF
{
    "rules": [
        {
            "rulePriority": 1,
            "description": "Keep last 100 images",
            "selection": {
                "tagStatus": "tagged",
                "tagPatternList": ["*"],
                "countType": "imageCountMoreThan",
                "countNumber": 100
            },
            "action": {
                "type": "expire"
            }
        }
    ]
}
EOF
}
