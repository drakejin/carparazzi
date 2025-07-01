package org.drakejin.carparazzi.controller.analysis.dto

import org.drakejin.carparazzi.controller.common.dto.PaginationInfo
import java.time.Instant
import java.util.*

// Request DTOs
data class AnalysisStartRequestDto(
    val uploadId: UUID
)

// Response DTOs
data class AnalysisJobResponseDto(
    val jobId: UUID,
    val uploadId: UUID,
    val jobStatus: JobStatus,
    val startedAt: Instant,
    val estimatedCompletionTime: Instant?
)

data class AnalysisStatusResponseDto(
    val jobId: UUID,
    val jobStatus: JobStatus,
    val progressPercentage: Int,
    val currentStage: String,
    val startedAt: Instant,
    val estimatedCompletionTime: Instant?,
    val processingMetadata: ProcessingMetadata?
)

data class ProcessingMetadata(
    val totalSegments: Int,
    val processedSegments: Int,
    val currentSegment: Int
)

data class AnalysisResultResponseDto(
    val jobId: UUID,
    val jobStatus: JobStatus,
    val startedAt: Instant,
    val completedAt: Instant?,
    val processingTimeMs: Long,
    val totalViolationsDetected: Int,
    val violations: List<ViolationSummaryDto>
)

data class ViolationSummaryDto(
    val violationId: UUID,
    val violationType: ViolationType,
    val timestampSeconds: Double,
    val confidenceScore: Double,
    val severityLevel: SeverityLevel,
    val description: String,
    val detectedAt: Instant
)

data class AnalysisJobListResponseDto(
    val jobs: List<AnalysisJobSummaryDto>,
    val pagination: PaginationInfo
)

data class AnalysisJobSummaryDto(
    val jobId: UUID,
    val uploadId: UUID,
    val originalFilename: String,
    val jobStatus: JobStatus,
    val totalViolationsDetected: Int,
    val processingTimeMs: Long,
    val startedAt: Instant,
    val completedAt: Instant?
)

// Enums
enum class JobStatus {
    QUEUED, PROCESSING, COMPLETED, FAILED
}

enum class ViolationType {
    SIGNAL_VIOLATION,
    LANE_VIOLATION,
    COLLISION,
    SUDDEN_BRAKING,
    SUDDEN_ACCELERATION,
    SPEEDING,
    ILLEGAL_TURN,
    ROAD_RAGE
}

enum class SeverityLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}
