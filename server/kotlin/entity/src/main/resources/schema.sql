-- Carparazzi MVP - Database Schema
-- PostgreSQL DDL for jOOQ code generation
-- Based on docs/ERD.20250701.STEP1.md

-- 1. Users Table (사용자 관리)
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    nickname VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    last_login_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),

    -- 인덱스
    CONSTRAINT users_email_valid CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}')
);

-- 이메일 인덱스 (로그인용)
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- 2. Video Uploads Table (영상 업로드 관리)
CREATE TABLE video_uploads (
    upload_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    s3_key VARCHAR(500) NOT NULL, -- S3 객체 키
    file_size_bytes BIGINT NOT NULL CHECK (file_size_bytes > 0),
    duration_seconds INTEGER CHECK (duration_seconds > 0),
    file_format VARCHAR(10) NOT NULL CHECK (file_format IN ('mp4', 'avi', 'mov')),
    upload_status VARCHAR(20) DEFAULT 'UPLOADING' CHECK (upload_status IN ('UPLOADING', 'COMPLETED', 'FAILED')),
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE,

    -- 외래키
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_video_uploads_user_id ON video_uploads(user_id);
CREATE INDEX idx_video_uploads_status ON video_uploads(upload_status);
CREATE INDEX idx_video_uploads_uploaded_at ON video_uploads(uploaded_at DESC);
CREATE UNIQUE INDEX idx_video_uploads_s3_key ON video_uploads(s3_key);

-- 3. Analysis Jobs Table (분석 작업 관리)
CREATE TABLE analysis_jobs (
    job_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    upload_id UUID NOT NULL,
    job_status VARCHAR(20) DEFAULT 'QUEUED' CHECK (job_status IN ('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED')),
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    processing_time_ms BIGINT CHECK (processing_time_ms >= 0),
    total_segments INTEGER DEFAULT 0 CHECK (total_segments >= 0),
    total_violations_detected INTEGER DEFAULT 0 CHECK (total_violations_detected >= 0),
    error_message TEXT,
    processing_metadata JSONB, -- 세그먼트 정보, 성능 메트릭 등

    -- 외래키
    FOREIGN KEY (upload_id) REFERENCES video_uploads(upload_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_analysis_jobs_upload_id ON analysis_jobs(upload_id);
CREATE INDEX idx_analysis_jobs_status ON analysis_jobs(job_status);
CREATE INDEX idx_analysis_jobs_started_at ON analysis_jobs(started_at DESC);

-- 업로드당 하나의 분석 작업만 허용
CREATE UNIQUE INDEX idx_analysis_jobs_upload_unique ON analysis_jobs(upload_id);

-- 4. Violation Events Table (위반 이벤트)
CREATE TABLE violation_events (
    violation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL,
    violation_type VARCHAR(30) NOT NULL CHECK (violation_type IN (
        'SIGNAL_VIOLATION',    -- 신호위반
        'LANE_VIOLATION',      -- 차선침범
        'COLLISION',           -- 충돌사고
        'SUDDEN_BRAKING',      -- 급브레이킹
        'SUDDEN_ACCELERATION', -- 급가속
        'SPEEDING',            -- 과속
        'ILLEGAL_TURN',        -- 불법회전
        'ROAD_RAGE'            -- 난폭운전
    )),
    timestamp_seconds DECIMAL(10, 3) NOT NULL CHECK (timestamp_seconds >= 0), -- 영상에서의 시점 (초.밀리초)
    confidence_score DECIMAL(5, 4) NOT NULL CHECK (confidence_score BETWEEN 0 AND 1), -- 신뢰도 (0.0000 ~ 1.0000)
    severity_level VARCHAR(10) DEFAULT 'MEDIUM' CHECK (severity_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    description TEXT NOT NULL, -- "적색 신호에서 직진 감지" 등
    detection_metadata JSONB, -- 바운딩 박스, 관련 이벤트 등
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    -- 외래키
    FOREIGN KEY (job_id) REFERENCES analysis_jobs(job_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_violation_events_job_id ON violation_events(job_id);
CREATE INDEX idx_violation_events_type ON violation_events(violation_type);
CREATE INDEX idx_violation_events_timestamp ON violation_events(timestamp_seconds);
CREATE INDEX idx_violation_events_confidence ON violation_events(confidence_score DESC);
CREATE INDEX idx_violation_events_severity ON violation_events(severity_level);

-- 5. Evidence Clips Table (증거 영상 클립)
CREATE TABLE evidence_clips (
    clip_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    violation_id UUID NOT NULL,
    job_id UUID NOT NULL, -- 빠른 조회를 위한 중복 컬럼
    s3_key VARCHAR(500) NOT NULL, -- 트리밍된 영상의 S3 키
    start_time_seconds DECIMAL(10, 3) NOT NULL CHECK (start_time_seconds >= 0),
    end_time_seconds DECIMAL(10, 3) NOT NULL CHECK (end_time_seconds > start_time_seconds),
    duration_seconds DECIMAL(10, 3) GENERATED ALWAYS AS (end_time_seconds - start_time_seconds) STORED,
    file_size_bytes BIGINT CHECK (file_size_bytes > 0),
    clip_status VARCHAR(20) DEFAULT 'PROCESSING' CHECK (clip_status IN ('PROCESSING', 'READY', 'FAILED', 'EXPIRED')),
    download_url VARCHAR(1000), -- Pre-signed URL (임시)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() + INTERVAL '7 days', -- 7일 후 만료

    -- 외래키
    FOREIGN KEY (violation_id) REFERENCES violation_events(violation_id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES analysis_jobs(job_id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_evidence_clips_violation_id ON evidence_clips(violation_id);
CREATE INDEX idx_evidence_clips_job_id ON evidence_clips(job_id);
CREATE INDEX idx_evidence_clips_status ON evidence_clips(clip_status);
CREATE INDEX idx_evidence_clips_expires_at ON evidence_clips(expires_at);
CREATE UNIQUE INDEX idx_evidence_clips_s3_key ON evidence_clips(s3_key);

-- 위반 이벤트당 하나의 클립만 허용
CREATE UNIQUE INDEX idx_evidence_clips_violation_unique ON evidence_clips(violation_id);

-- 6. Download Logs Table (다운로드 추적)
CREATE TABLE download_logs (
    download_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    clip_id UUID,
    download_type VARCHAR(20) DEFAULT 'SINGLE' CHECK (download_type IN ('SINGLE', 'BATCH', 'ZIP')),
    downloaded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_agent TEXT,
    ip_address INET,

    -- 외래키
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (clip_id) REFERENCES evidence_clips(clip_id) ON DELETE SET NULL
);

-- 인덱스
CREATE INDEX idx_download_logs_user_id ON download_logs(user_id);
CREATE INDEX idx_download_logs_clip_id ON download_logs(clip_id);
CREATE INDEX idx_download_logs_downloaded_at ON download_logs(downloaded_at DESC);
CREATE INDEX idx_download_logs_ip_address ON download_logs(ip_address);

-- 자동 정리 함수 (만료된 파일 삭제)
CREATE OR REPLACE FUNCTION cleanup_expired_clips()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    -- 만료된 클립들의 상태를 EXPIRED로 변경
    UPDATE evidence_clips
    SET clip_status = 'EXPIRED'
    WHERE expires_at < NOW()
    AND clip_status = 'READY';

    GET DIAGNOSTICS deleted_count = ROW_COUNT;

    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- 사용자별 분석 통계 뷰
CREATE VIEW user_analysis_stats AS
SELECT
    u.user_id,
    u.email,
    u.nickname,
    COUNT(DISTINCT vu.upload_id) as total_uploads,
    COUNT(DISTINCT aj.job_id) as total_analyses,
    COUNT(DISTINCT ve.violation_id) as total_violations,
    COUNT(DISTINCT ec.clip_id) as total_clips,
    COUNT(DISTINCT dl.download_id) as total_downloads,
    MAX(vu.uploaded_at) as last_upload_at
FROM users u
LEFT JOIN video_uploads vu ON u.user_id = vu.user_id
LEFT JOIN analysis_jobs aj ON vu.upload_id = aj.upload_id
LEFT JOIN violation_events ve ON aj.job_id = ve.job_id
LEFT JOIN evidence_clips ec ON ve.violation_id = ec.violation_id
LEFT JOIN download_logs dl ON u.user_id = dl.user_id
GROUP BY u.user_id, u.email, u.nickname;

-- 위반 유형별 통계 뷰
CREATE VIEW violation_type_stats AS
SELECT
    violation_type,
    COUNT(*) as total_count,
    AVG(confidence_score) as avg_confidence,
    COUNT(CASE WHEN severity_level = 'CRITICAL' THEN 1 END) as critical_count,
    COUNT(CASE WHEN severity_level = 'HIGH' THEN 1 END) as high_count,
    COUNT(CASE WHEN severity_level = 'MEDIUM' THEN 1 END) as medium_count,
    COUNT(CASE WHEN severity_level = 'LOW' THEN 1 END) as low_count
FROM violation_events
GROUP BY violation_type
ORDER BY total_count DESC;

-- 성능 최적화 인덱스
CREATE INDEX idx_jobs_status_started ON analysis_jobs(job_status, started_at);
CREATE INDEX idx_clips_status_expires ON evidence_clips(clip_status, expires_at);

-- 부분 인덱스 (성능 향상)
CREATE INDEX idx_active_jobs ON analysis_jobs(started_at)
WHERE job_status IN ('QUEUED', 'PROCESSING');

CREATE INDEX idx_ready_clips ON evidence_clips(created_at)
WHERE clip_status = 'READY';
