package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.entity.generated.tables.pojos.AnalysisJobs
import java.time.OffsetDateTime
import java.util.*

data class AnalysisJobDto(
    val jobId: UUID,
    val uploadId: UUID,
    val jobStatus: JobStatus,
    val startedAt: OffsetDateTime?,
    val completedAt: OffsetDateTime?,
    val processingTimeMs: Long?,
    val totalSegments: Int,
    val totalViolationsDetected: Int,
    val errorMessage: String?,
    val processingMetadata: String?
) {
    enum class JobStatus {
        QUEUED, PROCESSING, COMPLETED, FAILED
    }

    companion object {
        fun fromEntity(entity: AnalysisJobs): AnalysisJobDto {
            return AnalysisJobDto(
                jobId = entity.jobId,
                uploadId = entity.uploadId,
                jobStatus = JobStatus.valueOf(entity.jobStatus),
                startedAt = entity.startedAt,
                completedAt = entity.completedAt,
                processingTimeMs = entity.processingTimeMs,
                totalSegments = entity.totalSegments,
                totalViolationsDetected = entity.totalViolationsDetected,
                errorMessage = entity.errorMessage,
                processingMetadata = entity.processingMetadata?.toString()
            )
        }
    }
}
