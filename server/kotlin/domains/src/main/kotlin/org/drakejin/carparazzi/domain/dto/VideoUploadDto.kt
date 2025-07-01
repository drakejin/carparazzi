package org.drakejin.carparazzi.domain.dto

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
    }
}
