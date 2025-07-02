package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.domain.dto.common.ValidationResult
import org.drakejin.carparazzi.domain.dto.common.combine
import org.drakejin.carparazzi.entity.generated.tables.pojos.VideoUploads
import java.time.OffsetDateTime
import java.util.*

data class VideoUploadDto(
    val uploadId: UUID,
    val userId: UUID,
    val originalFilename: String,
    val s3Key: String,
    val fileSizeBytes: Long,
    val durationSeconds: Int?,
    val fileFormat: FileFormat,
    val uploadStatus: UploadStatus,
    val uploadedAt: OffsetDateTime,
    val completedAt: OffsetDateTime?
) {
    enum class FileFormat {
        mp4, avi, mov
    }

    enum class UploadStatus {
        UPLOADING, COMPLETED, FAILED
    }

    companion object {
        /**
         * 최대 파일 크기 (5GB)
         */
        private const val MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024 * 1024

        /**
         * 최대 영상 길이 (4시간)
         */
        private const val MAX_DURATION_SECONDS = 4 * 60 * 60

        fun fromEntity(entity: VideoUploads): VideoUploadDto {
            return VideoUploadDto(
                uploadId = entity.uploadId,
                userId = entity.userId,
                originalFilename = entity.originalFilename,
                s3Key = entity.s3Key,
                fileSizeBytes = entity.fileSizeBytes,
                durationSeconds = entity.durationSeconds,
                fileFormat = FileFormat.valueOf(entity.fileFormat),
                uploadStatus = UploadStatus.valueOf(entity.uploadStatus),
                uploadedAt = entity.uploadedAt,
                completedAt = entity.completedAt
            )
        }

        /**
         * 비디오 업로드 검증
         */
        fun validate(video: VideoUploadDto): ValidationResult {
            val validations = listOf(
                validateOriginalFilename(video.originalFilename),
                validateS3Key(video.s3Key),
                validateFileSize(video.fileSizeBytes),
                validateDuration(video.durationSeconds),
            )
            return validations.combine()
        }

        /**
         * 원본 파일명 검증
         */
        fun validateOriginalFilename(filename: String?): ValidationResult {
            return when {
                filename.isNullOrBlank() -> ValidationResult.Invalid(listOf("원본 파일명은 필수입니다"))
                filename.length > 255 -> ValidationResult.Invalid(listOf("파일명은 255자를 초과할 수 없습니다"))
                else -> ValidationResult.Valid
            }
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
         * 파일 크기 검증
         */
        fun validateFileSize(fileSizeBytes: Long?): ValidationResult {
            return when {
                fileSizeBytes == null -> ValidationResult.Invalid(listOf("파일 크기는 필수입니다"))
                fileSizeBytes <= 0 -> ValidationResult.Invalid(listOf("파일 크기는 0보다 커야 합니다"))
                fileSizeBytes > MAX_FILE_SIZE_BYTES -> ValidationResult.Invalid(listOf("파일 크기는 5GB를 초과할 수 없습니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 영상 길이 검증
         */
        fun validateDuration(durationSeconds: Int?): ValidationResult {
            return when {
                durationSeconds != null && durationSeconds <= 0 -> ValidationResult.Invalid(listOf("영상 길이는 0보다 커야 합니다"))
                durationSeconds != null && durationSeconds > MAX_DURATION_SECONDS -> ValidationResult.Invalid(listOf("영상 길이는 4시간을 초과할 수 없습니다"))
                else -> ValidationResult.Valid
            }
        }

        /**
         * 파일 형식 검증
         */
        fun validateFileFormat(fileFormat: String?): ValidationResult {
            return when {
                fileFormat.isNullOrBlank() -> ValidationResult.Invalid(listOf("파일 형식은 필수입니다"))
                !FileFormat.values().any { it.name.equals(fileFormat, ignoreCase = true) } -> ValidationResult.Invalid(
                    listOf("지원하는 파일 형식은 ${FileFormat.values().joinToString(", ")} 입니다")
                )
                else -> ValidationResult.Valid
            }
        }

        /**
         * 업로드 상태 검증
         */
        fun validateUploadStatus(uploadStatus: String?): ValidationResult {
            return when {
                uploadStatus.isNullOrBlank() -> ValidationResult.Valid // DEFAULT 'UPLOADING'
                !UploadStatus.values().any { it.name == uploadStatus } -> ValidationResult.Invalid(
                    listOf("업로드 상태는 ${UploadStatus.values().joinToString(", ")} 중 하나여야 합니다")
                )
                else -> ValidationResult.Valid
            }
        }
    }
}
