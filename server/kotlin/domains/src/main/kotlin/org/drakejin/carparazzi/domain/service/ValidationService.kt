package org.drakejin.carparazzi.domain.service

import org.drakejin.carparazzi.domain.dto.*
import org.drakejin.carparazzi.domain.dto.common.ValidationResult
import org.drakejin.carparazzi.domain.dto.common.combine
import org.drakejin.carparazzi.domain.util.EntityUtils
import java.math.BigDecimal

/**
 * 검증 서비스 - 모든 엔티티의 검증을 담당
 */
class ValidationService {

    /**
     * 사용자 생성 전 검증
     */
    fun validateUserForCreation(email: String, nickname: String?, status: String?): ValidationResult {
        val validations = listOf(
            UserDto.validateEmail(email),
            UserDto.validateNickname(nickname),
            UserDto.validateStatus(status)
        )
        return validations.combine()
    }

    /**
     * 비디오 업로드 검증
     */
    fun validateVideoUploadForCreation(
        originalFilename: String,
        s3Key: String,
        fileSizeBytes: Long,
        durationSeconds: Int?,
        fileFormat: String,
        uploadStatus: String?
    ): ValidationResult {
        val validations = listOf(
            VideoUploadDto.validateOriginalFilename(originalFilename),
            VideoUploadDto.validateS3Key(s3Key),
            VideoUploadDto.validateFileSize(fileSizeBytes),
            VideoUploadDto.validateDuration(durationSeconds),
            VideoUploadDto.validateFileFormat(fileFormat),
            VideoUploadDto.validateUploadStatus(uploadStatus)
        )
        return validations.combine()
    }

    /**
     * 위반 이벤트 검증
     */
    fun validateViolationEventForCreation(
        violationType: String,
        timestampSeconds: BigDecimal,
        confidenceScore: BigDecimal,
        severityLevel: String?,
        description: String
    ): ValidationResult {
        val validations = listOf(
            ViolationEventDto.validateViolationType(violationType),
            ViolationEventDto.validateTimestampSeconds(timestampSeconds),
            ViolationEventDto.validateConfidenceScore(confidenceScore),
            ViolationEventDto.validateSeverityLevel(severityLevel),
            ViolationEventDto.validateDescription(description)
        )
        return validations.combine()
    }

    /**
     * 증거 클립 검증 및 지속시간 계산
     */
    fun validateAndPrepareEvidenceClip(
        s3Key: String,
        startTimeSeconds: BigDecimal,
        endTimeSeconds: BigDecimal,
        fileSizeBytes: Long?,
        clipStatus: String?
    ): Pair<ValidationResult, BigDecimal?> {
        val validations = listOf(
            EvidenceClipDto.validateS3Key(s3Key),
            EvidenceClipDto.validateTimeRange(startTimeSeconds, endTimeSeconds),
            EvidenceClipDto.validateFileSize(fileSizeBytes),
            EvidenceClipDto.validateClipStatus(clipStatus)
        )

        val validationResult = validations.combine()
        val (duration, durationValidation) = EvidenceClipDto.calculateAndValidateDuration(startTimeSeconds, endTimeSeconds)

        val finalValidation = listOf(validationResult, durationValidation).combine()

        return Pair(finalValidation, duration)
    }

    /**
     * 다운로드 로그 검증
     */
    fun validateDownloadLogForCreation(
        downloadType: String?,
        userAgent: String?,
        ipAddress: String?
    ): ValidationResult {
        val validations = listOf(
            DownloadLogDto.validateDownloadType(downloadType),
            DownloadLogDto.validateUserAgent(userAgent),
            DownloadLogDto.validateIpAddress(ipAddress)
        )
        return validations.combine()
    }

    /**
     * 검증 결과를 예외로 변환하는 헬퍼 메서드
     */
    fun throwIfInvalid(validationResult: ValidationResult, entityName: String = "Entity") {
        if (validationResult.isInvalid()) {
            val errors = validationResult.getErrors().joinToString(", ")
            throw IllegalArgumentException("$entityName validation failed: $errors")
        }
    }
}
