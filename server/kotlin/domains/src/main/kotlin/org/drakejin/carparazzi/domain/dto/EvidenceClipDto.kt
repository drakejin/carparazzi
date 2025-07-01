package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.entity.generated.tables.pojos.EvidenceClips
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class EvidenceClipDto(
    val clipId: UUID,
    val violationId: UUID,
    val jobId: UUID,
    val s3Key: String,
    val startTimeSeconds: BigDecimal,
    val endTimeSeconds: BigDecimal,
    val fileSizeBytes: Long?,
    val clipStatus: ClipStatus,
    val downloadUrl: String?,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime
) {
    enum class ClipStatus {
        PROCESSING, READY, FAILED, EXPIRED
    }

    // 계산된 속성: duration_seconds
    val durationSeconds: BigDecimal
        get() = endTimeSeconds - startTimeSeconds

    companion object {
        fun fromEntity(entity: EvidenceClips): EvidenceClipDto {
            return EvidenceClipDto(
                clipId = entity.clipId,
                violationId = entity.violationId,
                jobId = entity.jobId,
                s3Key = entity.s3Key,
                startTimeSeconds = entity.startTimeSeconds,
                endTimeSeconds = entity.endTimeSeconds,
                fileSizeBytes = entity.fileSizeBytes,
                clipStatus = ClipStatus.valueOf(entity.clipStatus),
                downloadUrl = entity.downloadUrl,
                createdAt = entity.createdAt,
                expiresAt = entity.expiresAt
            )
        }
    }
}
