package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.domain.dto.common.ValidationResult
import org.drakejin.carparazzi.domain.dto.common.combine
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

        /**
         * 증거 클립 검증
         */
        fun validate(clip: EvidenceClipDto): ValidationResult {
            val validations = listOf(
                validateS3Key(clip.s3Key),
                validateTimeRange(clip.startTimeSeconds, clip.endTimeSeconds),
                validateFileSize(clip.fileSizeBytes),
                validateDownloadUrl(clip.downloadUrl)
            )
            return validations.combine()
        }

        /**
         * S3 키 검증
         */
        fun validateS3Key(s3Key: String?): ValidationResult {
            return when {
                s3Key.isNullOrBlank() -> ValidationResult.Invalid(listOf("S3 키는 필수입니다"))
                s3Key.length > 500 -> ValidationResult.Invalid(listOf("S3 키는 500자를 초과할 수 없습니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 시간 범위 검증
         */
        fun validateTimeRange(startTime: BigDecimal?, endTime: BigDecimal?): ValidationResult {
            val errors = mutableListOf<String>()

            if (startTime == null) {
                errors.add("시작 시간은 필수입니다")
            } else if (startTime < BigDecimal.ZERO) {
                errors.add("시작 시간은 0 이상이어야 합니다")
            }

            if (endTime == null) {
                errors.add("종료 시간은 필수입니다")
            }

            if (startTime != null && endTime != null && endTime <= startTime) {
                errors.add("종료 시간은 시작 시간보다 커야 합니다")
            }

            return if (errors.isEmpty()) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid(errors)
            }
        }

        /**
         * 파일 크기 검증
         */
        fun validateFileSize(fileSizeBytes: Long?): ValidationResult {
            return when {
                fileSizeBytes != null && fileSizeBytes <= 0 -> ValidationResult.Invalid(listOf("파일 크기는 0보다 커야 합니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 클립 상태 검증
         */
        fun validateClipStatus(clipStatus: String?): ValidationResult {
            return when {
                clipStatus.isNullOrBlank() -> ValidationResult.Valid // DEFAULT 'PROCESSING'
                !ClipStatus.values().any { it.name == clipStatus } -> ValidationResult.Invalid(
                    listOf("클립 상태는 ${ClipStatus.values().joinToString(", ")} 중 하나여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }

        /**
         * 다운로드 URL 검증
         */
        fun validateDownloadUrl(downloadUrl: String?): ValidationResult {
            return when {
                downloadUrl != null && downloadUrl.length > 1000 -> ValidationResult.Invalid(listOf("다운로드 URL은 1000자를 초과할 수 없습니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 클립 지속시간 계산 및 검증
         */
        fun calculateAndValidateDuration(startTime: BigDecimal?, endTime: BigDecimal?): Pair<BigDecimal?, ValidationResult> {
            return when {
                startTime == null || endTime == null -> Pair(null, ValidationResult.Invalid(listOf("시작 시간과 종료 시간이 필요합니다")))
                endTime <= startTime -> Pair(null, ValidationResult.Invalid(listOf("종료 시간은 시작 시간보다 커야 합니다")))
                else -> {
                    val duration = endTime.subtract(startTime)
                    Pair(duration, ValidationResult.Valid)
                }
            }
        }
    }
}
