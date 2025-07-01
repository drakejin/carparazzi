 # carparazzi - AI 기반 교통위반 자동감지 및 신고지원 서비스

## 📋 Document Overview

**문서 제목**: carparazzi PRD (2인팀 개발 버전)
**버전**: 1.0
**작성일**: 2025-07-01
**팀 구성**: 2
**개발 기간**: 3개월 (MVP)

---

## 🎯 Product Vision

### 핵심 목표
"운전자가 블랙박스 영상을 업로드하면 AI가 자동으로 교통위반을 감지하고, 신고용 증거영상 구간을 제안해주는 서비스"

### 핵심 가치 제안
- **간편함**: 영상 업로드 → 자동 분석 → 트리밍 구간 제안
- **정확성**: AI 기반 위반행위 자동 감지
- **실용성**: 바로 사용 가능한 증거영상 클립 생성

---

## 🚗 Problem Statement

### 현재 문제점
1. **시간 소모**: 긴 블랙박스 영상에서 위반 구간 수동 탐색 (30분~1시간)
2. **증거 부족**: 위반 순간만 포착해서 전후 맥락 부족으로 신고 기각
3. **전문성 부족**: 어떤 행위가 위반인지 판단 어려움
4. **편집 복잡성**: 영상 편집 툴 사용법 모름

### 해결 방향
- AI 자동 위반 감지로 수동 작업 제거
- 전후 맥락 포함한 스마트 트리밍
- 직관적인 웹 인터페이스 제공

---

## 👥 Target Users

### Primary User: 일반 운전자
**특징:**
- 블랙박스 설치 완료
- 교통위반 목격 경험 있음
- 신고 의향 있지만 방법을 모름
- 영상 편집 경험 부족

**Pain Points:**
- "위반 구간 찾는게 너무 힘들어요"
- "어디서부터 어디까지 잘라야 할지 모르겠어요"
- "신고용으로 어떤 부분이 중요한지 모르겠어요"

---

## 🔧 Core Features (MVP)

### Feature 1: 영상 업로드 시스템
**기능:** 블랙박스 영상 업로드 및 전처리

**Requirements:**
- 드래그 앤 드롭 업로드
- MP4, AVI 형식 지원
- 최대 2GB, 2시간 영상 지원
- 업로드 진행률 표시

**기술 구현:**
```kotlin
@RestController
class VideoUploadController {

    @PostMapping("/api/videos/upload")
    suspend fun uploadVideo(
        @RequestParam("file") file: MultipartFile
    ): UploadResponse {

        // S3 업로드
        val s3Key = s3Service.uploadVideo(file)

        // SQS로 분석 작업 큐잉
        val jobId = sqsService.enqueueAnalysisJob(s3Key)

        return UploadResponse(jobId, "QUEUED")
    }
}
```

### Feature 2: AI 위반행위 감지
**기능:** 업로드된 영상에서 교통위반 자동 감지

**감지 대상 (MVP 단계):**
1. **신호위반**: 적색신호 무시
2. **차선침범**: 실선 침범
3. **급정거**: 급브레이킹 패턴
4. **충돌사고**: 접촉/추돌
5. **난폭운전**: 급가속/급차선변경

**기술 구현:**
```kotlin
class ViolationDetectionService {

    suspend fun analyzeVideo(videoPath: String): List<ViolationEvent> {

        // 1. 오버랩 세그먼트 생성 (10분 + 2분 오버랩)
        val segments = createOverlappingSegments(videoPath)

        // 2. 병렬 AI 분석
        val allEvents = segments.map { segment ->
            async(Dispatchers.IO) {
                aiEngine.detectViolations(segment)
            }
        }.awaitAll().flatten()

        // 3. 중복 제거 및 컨텍스트 분석
        val uniqueEvents = removeDuplicateEvents(allEvents)
        return enhanceWithContext(uniqueEvents)
    }
}
```

**AI 모델 스택:**
- **YOLO**: 차량/신호등 객체 감지
- **OpenCV**: 차선 감지 (Hough Transform)
- **Custom Model**: 위반행위 분류

### Feature 3: 스마트 영상 트리밍
**기능:** 위반 구간을 포함한 최적 증거영상 생성

**트리밍 로직:**
- 위반 시점 10초 전 ~ 2분 후
- 연속 위반 시 통합 클립 생성
- 전후 맥락 고려한 구간 조정

**기술 구현:**
```kotlin
class SmartTrimService {

    suspend fun generateTrimSuggestions(
        videoPath: String,
        violations: List<ViolationEvent>
    ): List<TrimSuggestion> {

        return violations.map { violation ->
            async {
                val startTime = maxOf(0.0, violation.timestamp - 10)
                val endTime = violation.timestamp + 120

                // FFmpeg로 트리밍
                val trimmedPath = ffmpegProcessor.trimVideo(
                    inputPath = videoPath,
                    startTime = startTime,
                    duration = endTime - startTime
                )

                TrimSuggestion(
                    violationId = violation.id,
                    trimmedVideoPath = trimmedPath,
                    startTime = startTime,
                    endTime = endTime,
                    description = violation.description
                )
            }
        }.awaitAll()
    }
}
```

### Feature 4: 결과 조회 및 다운로드
**기능:** 분석 결과 확인 및 트리밍된 영상 다운로드

**화면 구성:**
- 감지된 위반행위 목록
- 각 위반별 신뢰도 점수
- 트리밍된 영상 미리보기
- 일괄 다운로드 기능

**기술 구현:**
```kotlin
@GetMapping("/api/analysis/{jobId}")
suspend fun getAnalysisResult(
    @PathVariable jobId: String
): AnalysisResult {

    val job = analysisJobRepository.findById(jobId)

    return AnalysisResult(
        jobId = jobId,
        status = job.status,
        violations = job.detectedViolations,
        trimSuggestions = job.trimSuggestions,
        processingTime = job.processingTimeMs
    )
}
```

---

## 🎨 User Interface Design

### 1. 메인 페이지
```
┌─────────────────────────────────────┐
│        carparazzi            │
├─────────────────────────────────────┤
│                                     │
│    [드래그 앤 드롭 업로드 영역]        │
│         또는 파일 선택                │
│                                     │
│  지원 형식: MP4, AVI (최대 2GB)      │
├─────────────────────────────────────┤
│           [분석 시작]               │
└─────────────────────────────────────┘
```

### 2. 분석 진행 페이지
```
┌─────────────────────────────────────┐
│    영상 분석 중... 약 7분 소요       │
├─────────────────────────────────────┤
│  ████████████░░░░░░░░ 65%          │
│                                     │
│  현재 단계: AI 위반행위 감지 중...    │
├─────────────────────────────────────┤
│         [취소] [새 분석]            │
└─────────────────────────────────────┘
```

### 3. 결과 페이지
```
┌─────────────────────────────────────┐
│      분석 결과 (3개 위반 감지)       │
├─────────────────────────────────────┤
│  🚨 신호위반 (95% 신뢰도)           │
│      시간: 03:25 - 03:45           │
│      [미리보기] [다운로드]           │
├─────────────────────────────────────┤
│  ⚠️  차선침범 (87% 신뢰도)          │
│      시간: 07:12 - 07:32           │
│      [미리보기] [다운로드]           │
├─────────────────────────────────────┤
│  🔥 급브레이킹 (92% 신뢰도)         │
│      시간: 15:33 - 15:53           │
│      [미리보기] [다운로드]           │
├─────────────────────────────────────┤
│         [전체 다운로드]             │
└─────────────────────────────────────┘
```

---

## 🛠 Technical Architecture

### System Overview
```
[사용자] → [Web Frontend] → [API Gateway] → [Backend Service]
                                                    ↓
[S3 Storage] ← [SQS Queue] ← [Lambda Trigger] ← [Backend]
      ↓
[ECS Task] → [AI Analysis] → [FFmpeg Trimming] → [Results]
```

### Technology Stack

#### Frontend
- **Framework**: React.js + TypeScript
- **UI Library**: tailwind / radix
- **State Management**: React Query
- **File Upload**: React-Dropzone

#### Backend
- **Language**: Kotlin + Spring Boot
- **Database**: PostgreSQL (RDS)
- **Cache**: Redis (ElastiCache)
- **Queue**: Amazon SQS
- **Storage**: Amazon S3

#### AI/ML
- **Runtime**: ONNX Runtime
- **Vision**: OpenCV
- **Video Processing**: FFmpeg
- **Models**: YOLOv5 (객체감지), Custom CNN (위반분류)

#### Infrastructure
- **Cloud**: AWS
- **Container**: Docker + ECS Fargate
- **API**: Spring Boot REST API
- **Monitoring**: CloudWatch

### Database Schema

```sql
-- 분석 작업 테이블
CREATE TABLE analysis_jobs (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    video_s3_key VARCHAR(255),
    status VARCHAR(20), -- QUEUED, PROCESSING, COMPLETED, FAILED
    created_at TIMESTAMP,
    completed_at TIMESTAMP,
    processing_time_ms BIGINT
);

-- 위반 이벤트 테이블
CREATE TABLE violation_events (
    id VARCHAR(50) PRIMARY KEY,
    job_id VARCHAR(50),
    violation_type VARCHAR(50),
    timestamp_seconds DECIMAL(10,3),
    confidence DECIMAL(5,4),
    description TEXT,
    severity VARCHAR(20),
    metadata JSONB,
    FOREIGN KEY (job_id) REFERENCES analysis_jobs(id)
);

-- 트리밍 제안 테이블
CREATE TABLE trim_suggestions (
    id VARCHAR(50) PRIMARY KEY,
    violation_id VARCHAR(50),
    start_time DECIMAL(10,3),
    end_time DECIMAL(10,3),
    trimmed_video_s3_key VARCHAR(255),
    file_size_bytes BIGINT,
    FOREIGN KEY (violation_id) REFERENCES violation_events(id)
);
```

### API Endpoints

```kotlin
// 핵심 API 엔드포인트
@RestController
@RequestMapping("/api/v1")
class VideoAnalysisController {

    // 영상 업로드
    @PostMapping("/videos/upload")
    suspend fun uploadVideo(@RequestParam file: MultipartFile): UploadResponse

    // 분석 상태 조회
    @GetMapping("/analysis/{jobId}")
    suspend fun getAnalysisStatus(@PathVariable jobId: String): JobStatus

    // 분석 결과 조회
    @GetMapping("/analysis/{jobId}/results")
    suspend fun getAnalysisResults(@PathVariable jobId: String): AnalysisResult

    // 트리밍된 영상 다운로드 URL 생성
    @GetMapping("/trim/{trimId}/download")
    suspend fun getTrimDownloadUrl(@PathVariable trimId: String): DownloadUrl

    // 전체 결과 다운로드 (ZIP)
    @GetMapping("/analysis/{jobId}/download")
    suspend fun downloadAllResults(@PathVariable jobId: String): DownloadUrl
}
```

---

## 📋 Development Roadmap

### Phase 1: MVP (3개월)
- [ ] Spring Boot 기본 구조 설정
- [ ] S3 업로드 API 구현
- [ ] PostgreSQL 스키마 설계
- [ ] SQS 큐 처리 로직
- [ ] REST API 엔드포인트
- [ ] 사용자 세션 관리

- [ ] ECS 인프라 구축 (Terraform)
- [ ] YOLO 모델 통합
- [ ] OpenCV 차선 감지
- [ ] FFmpeg 영상 처리
- [ ] 오버랩 세그먼트 로직
- [ ] CI/CD 파이프라인

- [ ] React 프론트엔드
- [ ] AWS 인프라 설정
- [ ] 통합 테스트
- [ ] 성능 최적화

### Phase 2: 기능 확장 (추가 2개월)
- [ ] 더 많은 위반 유형 추가
- [ ] 트리밍 구간 수동 조정
- [ ] 분석 결과 공유 기능
- [ ] 모바일 최적화
- [ ] 성능 모니터링

### Phase 3: 고도화 (추가 3개월)
- [ ] 복합 위반 패턴 감지
- [ ] 상세 분석 리포트
- [ ] 사용자 피드백 학습
- [ ] API 외부 제공

---

## 🎯 Success Metrics

### 기술적 성능 지표
- **처리 시간**: 1시간 영상 < 10분 분석
- **정확도**: 위반 감지 정확도 > 85%
- **가용성**: 서비스 업타임 > 99%
- **응답 시간**: API 응답 < 2초

### 사용자 경험 지표
- **완료율**: 업로드 후 결과 확인까지 > 80%
- **재사용률**: 월 1회 이상 사용 > 50%
- **만족도**: 사용자 평점 > 4.0/5.0

---

## 🔒 Technical Constraints

### 개발 제약사항
- **팀 크기**: 2명 (백엔드 1명 + AI/DevOps 1명)
- **개발 기간**: 3개월 MVP
- **예산**: AWS 월 100만원 이하
- **인프라**: AWS 서비스만 사용

### 기술적 제약사항
- **파일 크기**: 최대 2GB (Lambda 제약)
- **동시 처리**: 최대 10개 영상 (비용 고려)
- **지원 형식**: MP4, AVI만 (개발 단순화)
- **분석 시간**: 최대 15분 (ECS Task 제한)

### 보안 요구사항
- **데이터 암호화**: S3 서버사이드 암호화
- **개인정보 보호**: 영상 30일 후 자동 삭제
- **접근 제어**: API 키 기반 인증
- **로그 관리**: CloudWatch 로그 수집

---

## 🚀 Deployment Strategy

### 개발 환경
- **Local**: Docker Compose로 통합 환경
- **Staging**: AWS 소규모 환경
- **Production**: AWS Auto Scaling 환경

### CI/CD Pipeline
```yaml
# GitHub Actions
name: Deploy to AWS
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Build Docker Image
        run: docker build -t video-analyzer .

      - name: Push to ECR
        run: |
          aws ecr get-login-password | docker login --username AWS --password-stdin $ECR_REGISTRY
          docker push $ECR_REGISTRY/video-analyzer:latest

      - name: Deploy to ECS
        run: aws ecs update-service --cluster video-analysis --service video-analyzer --force-new-deployment
```

### 모니터링
- **애플리케이션**: CloudWatch + Custom Metrics
- **인프라**: CloudWatch + AWS Config
- **비용**: AWS Cost Explorer 알림
- **에러**: Sentry 에러 트래킹

---

## 📝 Next Steps

### 즉시 시작할 작업
1. **AWS 계정 설정** 및 IAM 역할 구성
2. **GitHub 저장소** 생성 및 초기 구조
3. **Terraform 인프라** 코드 작성
4. **Spring Boot 프로젝트** 초기 설정
5. **React 프론트엔드** 프로젝트 생성

### 1주차 목표
- [ ] 기본 인프라 구축 (S3, RDS, SQS)
- [ ] 파일 업로드 API 완성
- [ ] 프론트엔드 업로드 페이지 완성
- [ ] ECS 태스크 기본 구조 완성

### 1개월 목표
- [ ] 기본 AI 모델 통합 완료
- [ ] 간단한 위반 감지 구현
- [ ] 영상 트리밍 기능 구현
- [ ] End-to-End 테스트 완료

