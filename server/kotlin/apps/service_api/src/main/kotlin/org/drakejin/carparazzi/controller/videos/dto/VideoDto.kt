package org.drakejin.carparazzi.controller.videos.dto

import org.drakejin.carparazzi.controller.common.dto.PaginationInfo
import java.time.Instant
import java.util.*

// Response DTOs
data class VideoUploadResponseDto(
    val uploadId: UUID,
    val originalFilename: String,
    val fileSizeBytes: Long,
    val fileFormat: String,
    val uploadStatus: UploadStatus,
    val uploadedAt: Instant,
    val s3Key: String
)

data class VideoInfoResponseDto(
    val uploadId: UUID,
    val originalFilename: String,
    val fileSizeBytes: Long,
    val durationSeconds: Int?,
    val fileFormat: String,
    val uploadStatus: UploadStatus,
    val uploadedAt: Instant,
    val completedAt: Instant?,
    val analysisStatus: String?
)

data class VideoListResponseDto(
    val videos: List<VideoInfoResponseDto>,
    val pagination: PaginationInfo
)

// Enums
enum class UploadStatus {
    UPLOADING, COMPLETED, FAILED
}
