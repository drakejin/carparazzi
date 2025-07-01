# Carparazzi API Specification v1.0

## 📋 Document Overview

**문서 제목**: Carparazzi REST API 명세서
**버전**: 1.0
**작성일**: 2025-07-01
**Base URL**: `https://api.carparazzi.com/api/v1`
**Content-Type**: `application/json`

---

## 🔐 Authentication

모든 API 요청은 JWT 토큰을 통한 인증이 필요합니다.

```http
Authorization: Bearer <jwt_token>
```

---

## 📝 Common Response Format

### Success Response
```json
{
  "success": true,
  "data": {
    // 응답 데이터
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable error message",
    "details": "Additional error details (optional)"
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

### Error Codes

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `INVALID_REQUEST` | 400 | 잘못된 요청 형식 |
| `UNAUTHORIZED` | 401 | 인증 실패 |
| `FORBIDDEN` | 403 | 권한 없음 |
| `NOT_FOUND` | 404 | 리소스를 찾을 수 없음 |
| `CONFLICT` | 409 | 리소스 충돌 |
| `VALIDATION_ERROR` | 422 | 입력 데이터 검증 실패 |
| `FILE_TOO_LARGE` | 413 | 파일 크기 초과 |
| `UNSUPPORTED_FORMAT` | 415 | 지원하지 않는 파일 형식 |
| `RATE_LIMIT_EXCEEDED` | 429 | 요청 한도 초과 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |
| `SERVICE_UNAVAILABLE` | 503 | 서비스 일시 중단 |

---

## 🔑 1. Authentication APIs

### 1.1 사용자 정보 조회

```http
GET /auth/me
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user_id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "nickname": "사용자닉네임",
    "status": "ACTIVE",
    "created_at": "2025-07-01T10:30:00Z",
    "last_login_at": "2025-07-01T10:25:00Z"
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

---

## 📹 2. Video Upload APIs

### 2.1 영상 업로드

```http
POST /videos/upload
```

**Request (multipart/form-data):**
```
file: [video file] (required)
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "upload_id": "550e8400-e29b-41d4-a716-446655440001",
    "original_filename": "blackbox_20250701.mp4",
    "file_size_bytes": 1073741824,
    "file_format": "mp4",
    "upload_status": "COMPLETED",
    "uploaded_at": "2025-07-01T10:30:00Z",
    "s3_key": "uploads/usr_12345/video_20250701.mp4"
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

**Error Responses:**
- `413 FILE_TOO_LARGE` - 파일 크기가 2GB 초과
- `415 UNSUPPORTED_FORMAT` - 지원하지 않는 파일 형식 (mp4, avi, mov만 지원)

### 2.2 업로드된 영상 목록 조회

```http
GET /videos?page=1&size=10&status=COMPLETED
```

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 1)
- `size` (optional): 페이지 크기 (기본값: 10, 최대: 50)
- `status` (optional): 업로드 상태 필터 (UPLOADING, COMPLETED, FAILED)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "videos": [
      {
        "upload_id": "550e8400-e29b-41d4-a716-446655440001",
        "original_filename": "blackbox_20250701.mp4",
        "file_size_bytes": 1073741824,
        "duration_seconds": 3600,
        "file_format": "mp4",
        "upload_status": "COMPLETED",
        "uploaded_at": "2025-07-01T10:30:00Z",
        "analysis_status": "COMPLETED"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total_elements": 25,
      "total_pages": 3,
      "has_next": true,
      "has_previous": false
    }
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

### 2.3 특정 영상 정보 조회

```http
GET /videos/{upload_id}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "upload_id": "550e8400-e29b-41d4-a716-446655440001",
    "original_filename": "blackbox_20250701.mp4",
    "file_size_bytes": 1073741824,
    "duration_seconds": 3600,
    "file_format": "mp4",
    "upload_status": "COMPLETED",
    "uploaded_at": "2025-07-01T10:30:00Z",
    "completed_at": "2025-07-01T10:32:00Z"
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

---

## 🔍 3. Analysis APIs

### 3.1 분석 작업 시작

```http
POST /analysis/start
```

**Request Body:**
```json
{
  "upload_id": "550e8400-e29b-41d4-a716-446655440001"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "job_id": "550e8400-e29b-41d4-a716-446655440002",
    "upload_id": "550e8400-e29b-41d4-a716-446655440001",
    "job_status": "QUEUED",
    "started_at": "2025-07-01T10:30:00Z",
    "estimated_completion_time": "2025-07-01T10:40:00Z"
  },
  "timestamp": "2025-07-01T10:30:00Z"
}
```

**Error Responses:**
- `409 CONFLICT` - 이미 분석 중인 영상
- `404 NOT_FOUND` - 존재하지 않는 upload_id

### 3.2 분석 작업 상태 조회

```http
GET /analysis/{job_id}/status
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "job_id": "550e8400-e29b-41d4-a716-446655440002",
    "job_status": "PROCESSING",
    "progress_percentage": 65,
    "current_stage": "AI 위반행위 감지 중",
    "started_at": "2025-07-01T10:30:00Z",
    "estimated_completion_time": "2025-07-01T10:40:00Z",
    "processing_metadata": {
      "total_segments": 12,
      "processed_segments": 8,
      "current_segment": 9
    }
  },
  "timestamp": "2025-07-01T10:35:00Z"
}
```

### 3.3 분석 결과 조회

```http
GET /analysis/{job_id}/results
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "job_id": "550e8400-e29b-41d4-a716-446655440002",
    "job_status": "COMPLETED",
    "started_at": "2025-07-01T10:30:00Z",
    "completed_at": "2025-07-01T10:37:30Z",
    "processing_time_ms": 450000,
    "total_violations_detected": 3,
    "violations": [
      {
        "violation_id": "550e8400-e29b-41d4-a716-446655440003",
        "violation_type": "SIGNAL_VIOLATION",
        "timestamp_seconds": 185.423,
        "confidence_score": 0.9234,
        "severity_level": "HIGH",
        "description": "적색 신호에서 직진 감지",
        "detected_at": "2025-07-01T10:35:00Z"
      },
      {
        "violation_id": "550e8400-e29b-41d4-a716-446655440004",
        "violation_type": "LANE_VIOLATION",
        "timestamp_seconds": 432.156,
        "confidence_score": 0.8756,
        "severity_level": "MEDIUM",
        "description": "실선 차선 침범 감지",
        "detected_at": "2025-07-01T10:35:30Z"
      }
    ]
  },
  "timestamp": "2025-07-01T10:40:00Z"
}
```

### 3.4 사용자의 분석 작업 목록 조회

```http
GET /analysis?page=1&size=10&status=COMPLETED
```

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 1)
- `size` (optional): 페이지 크기 (기본값: 10, 최대: 50)
- `status` (optional): 작업 상태 필터 (QUEUED, PROCESSING, COMPLETED, FAILED)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "jobs": [
      {
        "job_id": "550e8400-e29b-41d4-a716-446655440002",
        "upload_id": "550e8400-e29b-41d4-a716-446655440001",
        "original_filename": "blackbox_20250701.mp4",
        "job_status": "COMPLETED",
        "total_violations_detected": 3,
        "processing_time_ms": 450000,
        "started_at": "2025-07-01T10:30:00Z",
        "completed_at": "2025-07-01T10:37:30Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 10,
      "total_elements": 15,
      "total_pages": 2,
      "has_next": true,
      "has_previous": false
    }
  },
  "timestamp": "2025-07-01T10:40:00Z"
}
```

---

## ⚠️ 4. Violation APIs

### 4.1 특정 분석의 위반 이벤트 목록 조회

```http
GET /violations?job_id={job_id}&type=SIGNAL_VIOLATION&severity=HIGH
```

**Query Parameters:**
- `job_id` (required): 분석 작업 ID
- `type` (optional): 위반 유형 필터
- `severity` (optional): 심각도 필터 (LOW, MEDIUM, HIGH, CRITICAL)
- `min_confidence` (optional): 최소 신뢰도 (0.0 ~ 1.0)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "violations": [
      {
        "violation_id": "550e8400-e29b-41d4-a716-446655440003",
        "job_id": "550e8400-e29b-41d4-a716-446655440002",
        "violation_type": "SIGNAL_VIOLATION",
        "timestamp_seconds": 185.423,
        "confidence_score": 0.9234,
        "severity_level": "HIGH",
        "description": "적색 신호에서 직진 감지",
        "detection_metadata": {
          "bounding_boxes": [
            {
              "object": "traffic_light",
              "x": 120,
              "y": 80,
              "width": 40,
              "height": 80,
              "confidence": 0.95
            }
          ],
          "frame_analysis": {
            "light_color": "red",
            "vehicle_movement": "forward"
          }
        },
        "detected_at": "2025-07-01T10:35:00Z"
      }
    ]
  },
  "timestamp": "2025-07-01T10:40:00Z"
}
```

### 4.2 특정 위반 이벤트 상세 조회

```http
GET /violations/{violation_id}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "violation_id": "550e8400-e29b-41d4-a716-446655440003",
    "job_id": "550e8400-e29b-41d4-a716-446655440002",
    "violation_type": "SIGNAL_VIOLATION",
    "timestamp_seconds": 185.423,
    "confidence_score": 0.9234,
    "severity_level": "HIGH",
    "description": "적색 신호에서 직진 감지",
    "detection_metadata": {
      "bounding_boxes": [
        {
          "object": "traffic_light",
          "x": 120,
          "y": 80,
          "width": 40,
          "height": 80,
          "confidence": 0.95
        },
        {
          "object": "vehicle",
          "x": 200,
          "y": 150,
          "width": 120,
          "height": 80,
          "confidence": 0.98
        }
      ],
      "frame_analysis": {
        "light_color": "red",
        "vehicle_movement": "forward",
        "intersection_type": "signalized",
        "weather_condition": "clear"
      }
    },
    "detected_at": "2025-07-01T10:35:00Z"
  },
  "timestamp": "2025-07-01T10:40:00Z"
}
```

---

## 🎬 5. Evidence Clip APIs

### 5.1 증거 클립 생성 요청

```http
POST /evidence/clips
```

**Request Body:**
```json
{
  "violation_id": "550e8400-e29b-41d4-a716-446655440003",
  "custom_start_time": 175.0,
  "custom_end_time": 305.0
}
```

**Note:** `custom_start_time`과 `custom_end_time`은 선택사항이며, 제공하지 않으면 기본 트리밍 로직 적용 (위반 시점 10초 전 ~ 2분 후)

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "clip_id": "550e8400-e29b-41d4-a716-446655440005",
    "violation_id": "550e8400-e29b-41d4-a716-446655440003",
    "job_id": "550e8400-e29b-41d4-a716-446655440002",
    "start_time_seconds": 175.423,
    "end_time_seconds": 305.423,
    "duration_seconds": 130.0,
    "clip_status": "PROCESSING",
    "created_at": "2025-07-01T10:40:00Z",
    "expires_at": "2025-07-08T10:40:00Z"
  },
  "timestamp": "2025-07-01T10:40:00Z"
}
```

### 5.2 증거 클립 목록 조회

```http
GET /evidence/clips?job_id={job_id}&status=READY
```

**Query Parameters:**
- `job_id` (optional): 특정 분석 작업의 클립만 조회
- `violation_id` (optional): 특정 위반 이벤트의 클립만 조회
- `status` (optional): 클립 상태 필터 (PROCESSING, READY, FAILED, EXPIRED)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "clips": [
      {
        "clip_id": "550e8400-e29b-41d4-a716-446655440005",
        "violation_id": "550e8400-e29b-41d4-a716-446655440003",
        "violation_type": "SIGNAL_VIOLATION",
        "violation_description": "적색 신호에서 직진 감지",
        "start_time_seconds": 175.423,
        "end_time_seconds": 305.423,
        "duration_seconds": 130.0,
        "file_size_bytes": 52428800,
        "clip_status": "READY",
        "created_at": "2025-07-01T10:40:00Z",
        "expires_at": "2025-07-08T10:40:00Z"
      }
    ]
  },
  "timestamp": "2025-07-01T10:45:00Z"
}
```

### 5.3 증거 클립 상세 정보 조회

```http
GET /evidence/clips/{clip_id}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "clip_id": "550e8400-e29b-41d4-a716-446655440005",
    "violation_id": "550e8400-e29b-41d4-a716-446655440003",
    "job_id": "550e8400-e29b-41d4-a716-446655440002",
    "violation_type": "SIGNAL_VIOLATION",
    "violation_description": "적색 신호에서 직진 감지",
    "start_time_seconds": 175.423,
    "end_time_seconds": 305.423,
    "duration_seconds": 130.0,
    "file_size_bytes": 52428800,
    "clip_status": "READY",
    "s3_key": "clips/job_12345/signal_violation_185.mp4",
    "created_at": "2025-07-01T10:40:00Z",
    "expires_at": "2025-07-08T10:40:00Z"
  },
  "timestamp": "2025-07-01T10:45:00Z"
}
```

---

## 📥 6. Download APIs

### 6.1 단일 클립 다운로드 URL 생성

```http
GET /downloads/clips/{clip_id}/url
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "download_url": "https://carparazzi-clips.s3.amazonaws.com/clips/job_12345/signal_violation_185.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
    "expires_at": "2025-07-01T11:45:00Z",
    "file_size_bytes": 52428800,
    "filename": "signal_violation_185_20250701.mp4"
  },
  "timestamp": "2025-07-01T10:45:00Z"
}
```

### 6.2 분석 결과 전체 다운로드 (ZIP)

```http
GET /downloads/analysis/{job_id}/zip
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "download_url": "https://carparazzi-clips.s3.amazonaws.com/archives/job_12345_analysis_results.zip?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...",
    "expires_at": "2025-07-01T11:45:00Z",
    "file_size_bytes": 157286400,
    "filename": "analysis_results_20250701.zip",
    "included_clips": [
      {
        "clip_id": "550e8400-e29b-41d4-a716-446655440005",
        "violation_type": "SIGNAL_VIOLATION",
        "filename": "signal_violation_185.mp4"
      },
      {
        "clip_id": "550e8400-e29b-41d4-a716-446655440006",
        "violation_type": "LANE_VIOLATION",
        "filename": "lane_violation_432.mp4"
      }
    ]
  },
  "timestamp": "2025-07-01T10:45:00Z"
}
```

### 6.3 다운로드 기록 조회

```http
GET /downloads/history?page=1&size=20
```

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 1)
- `size` (optional): 페이지 크기 (기본값: 20, 최대: 100)
- `type` (optional): 다운로드 유형 필터 (SINGLE, BATCH, ZIP)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "downloads": [
      {
        "download_id": "550e8400-e29b-41d4-a716-446655440007",
        "clip_id": "550e8400-e29b-41d4-a716-446655440005",
        "download_type": "SINGLE",
        "violation_type": "SIGNAL_VIOLATION",
        "downloaded_at": "2025-07-01T10:45:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total_elements": 45,
      "total_pages": 3,
      "has_next": true,
      "has_previous": false
    }
  },
  "timestamp": "2025-07-01T10:50:00Z"
}
```

---

## 📊 7. Statistics APIs

### 7.1 사용자 통계 조회

```http
GET /statistics/user
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "total_uploads": 15,
    "total_analyses": 15,
    "total_violations_detected": 42,
    "total_clips_generated": 42,
    "total_downloads": 28,
    "violation_type_breakdown": {
      "SIGNAL_VIOLATION": 12,
      "LANE_VIOLATION": 8,
      "COLLISION": 3,
      "SUDDEN_BRAKING": 15,
      "SPEEDING": 4
    },
    "last_upload_at": "2025-07-01T10:30:00Z",
    "account_created_at": "2025-06-15T09:20:00Z"
  },
  "timestamp": "2025-07-01T10:50:00Z"
}
```

### 7.2 시스템 통계 조회 (관리자용)

```http
GET /statistics/system
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "total_users": 1250,
    "total_videos_processed": 3420,
    "total_violations_detected": 8945,
    "average_processing_time_ms": 425000,
    "popular_violation_types": [
      {
        "type": "SUDDEN_BRAKING",
        "count": 2340,
        "percentage": 26.2
      },
      {
        "type": "SIGNAL_VIOLATION",
        "count": 1890,
        "percentage": 21.1
      }
    ],
    "daily_upload_trend": [
      {
        "date": "2025-07-01",
        "upload_count": 45,
        "analysis_count": 43
      }
    ]
  },
  "timestamp": "2025-07-01T10:50:00Z"
}
```

---

## 🔧 8. System APIs

### 8.1 서비스 상태 확인

```http
GET /health
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "status": "healthy",
    "version": "1.0.0",
    "timestamp": "2025-07-01T10:50:00Z",
    "services": {
      "database": "healthy",
      "redis": "healthy",
      "s3": "healthy",
      "sqs": "healthy",
      "ai_engine": "healthy"
    },
    "metrics": {
      "active_analysis_jobs": 3,
      "queue_size": 12,
      "average_response_time_ms": 245
    }
  },
  "timestamp": "2025-07-01T10:50:00Z"
}
```

---

## 📋 Data Models

### User
```json
{
  "user_id": "uuid",
  "email": "string",
  "nickname": "string",
  "status": "ACTIVE | INACTIVE | SUSPENDED",
  "created_at": "timestamp",
  "last_login_at": "timestamp"
}
```

### VideoUpload
```json
{
  "upload_id": "uuid",
  "user_id": "uuid",
  "original_filename": "string",
  "s3_key": "string",
  "file_size_bytes": "number",
  "duration_seconds": "number",
  "file_format": "mp4 | avi | mov",
  "upload_status": "UPLOADING | COMPLETED | FAILED",
  "uploaded_at": "timestamp",
  "completed_at": "timestamp"
}
```

### AnalysisJob
```json
{
  "job_id": "uuid",
  "upload_id": "uuid",
  "job_status": "QUEUED | PROCESSING | COMPLETED | FAILED",
  "started_at": "timestamp",
  "completed_at": "timestamp",
  "processing_time_ms": "number",
  "total_segments": "number",
  "total_violations_detected": "number",
  "error_message": "string",
  "processing_metadata": "object"
}
```

### ViolationEvent
```json
{
  "violation_id": "uuid",
  "job_id": "uuid",
  "violation_type": "SIGNAL_VIOLATION | LANE_VIOLATION | COLLISION | SUDDEN_BRAKING | SUDDEN_ACCELERATION | SPEEDING | ILLEGAL_TURN | ROAD_RAGE",
  "timestamp_seconds": "number",
  "confidence_score": "number (0.0-1.0)",
  "severity_level": "LOW | MEDIUM | HIGH | CRITICAL",
  "description": "string",
  "detection_metadata": "object",
  "detected_at": "timestamp"
}
```

### EvidenceClip
```json
{
  "clip_id": "uuid",
  "violation_id": "uuid",
  "job_id": "uuid",
  "s3_key": "string",
  "start_time_seconds": "number",
  "end_time_seconds": "number",
  "duration_seconds": "number",
  "file_size_bytes": "number",
  "clip_status": "PROCESSING | READY | FAILED | EXPIRED",
  "download_url": "string",
  "created_at": "timestamp",
  "expires_at": "timestamp"
}
```

### DownloadLog
```json
{
  "download_id": "uuid",
  "user_id": "uuid",
  "clip_id": "uuid",
  "download_type": "SINGLE | BATCH | ZIP",
  "downloaded_at": "timestamp",
  "user_agent": "string",
  "ip_address": "string"
}
```

---

## 📊 Monitoring & Logging

### API Metrics
- 응답 시간 모니터링
- 에러율 추적
- 처리량 측정
- 사용자별 사용량 통계

### Error Logging
- 모든 에러는 CloudWatch에 로깅
- 중요 에러는 Slack 알림
- 사용자 행동 추적 (GDPR 준수)

---

## 🔄 Versioning

API 버전 관리는 URL 경로를 통해 수행됩니다:
- Current: `/api/v1/`
- Future: `/api/v2/`

하위 호환성은 최소 6개월간 유지됩니다.

---

## 📞 Support

### Error Reporting
API 에러 발생 시 다음 정보를 포함하여 문의해주세요:
- Request ID (응답 헤더의 `X-Request-ID`)
- 타임스탬프
- 요청 내용 (민감 정보 제외)
- 에러 응답
