package org.drakejin.carparazzi.controller.evidence.dto

import org.drakejin.carparazzi.domain.dto.ViolationEventDto.ViolationType
import org.drakejin.carparazzi.domain.dto.EvidenceClipDto.ClipStatus
import java.time.OffsetDateTime
import java.util.*

// Request DTOs
data class EvidenceClipCreateRequestDto(
    val violationId: UUID,
    val customStartTime: Double?,
    val customEndTime: Double?
)

// Response DTOs
data class EvidenceClipResponseDto(
    val clipId: UUID,
    val violationId: UUID,
    val jobId: UUID,
    val startTimeSeconds: Double,
    val endTimeSeconds: Double,
    val durationSeconds: Double,
    val clipStatus: ClipStatus,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime
)

data class EvidenceClipDetailResponseDto(
    val clipId: UUID,
    val violationId: UUID,
    val jobId: UUID,
    val violationType: ViolationType,
    val violationDescription: String,
    val startTimeSeconds: Double,
    val endTimeSeconds: Double,
    val durationSeconds: Double,
    val fileSizeBytes: Long,
    val clipStatus: ClipStatus,
    val s3Key: String?,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime
)

data class EvidenceClipListResponseDto(
    val clips: List<EvidenceClipDetailResponseDto>
)
