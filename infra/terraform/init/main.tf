resource "aws_s3_bucket" "bucket" {
  bucket = "carparazzi-infrastructure"

  tags = {
    Crew       = "carparazzi"
    Team       = "infra"
    Service    = "infrastructure"
    Repository = "carparazzi"
  }
}

resource "aws_s3_bucket_ownership_controls" "ownership" {
  bucket = aws_s3_bucket.bucket.id
  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}


resource "aws_dynamodb_table" "terraform-lock" {
  name = "carparazzi-terraform-lock"

  read_capacity  = 1
  write_capacity = 1

  hash_key = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }
}

