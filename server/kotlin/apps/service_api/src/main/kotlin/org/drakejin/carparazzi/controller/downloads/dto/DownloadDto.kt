package org.drakejin.carparazzi.controller.downloads.dto

import org.drakejin.carparazzi.domain.dto.ViolationEventDto.ViolationType
import org.drakejin.carparazzi.domain.dto.DownloadLogDto.DownloadType
import org.drakejin.carparazzi.controller.common.dto.PaginationInfo
import java.time.OffsetDateTime
import java.util.*

// Response DTOs
data class DownloadUrlResponseDto(
    val downloadUrl: String,
    val expiresAt: OffsetDateTime,
    val fileSizeBytes: Long,
    val filename: String
)

data class AnalysisZipDownloadResponseDto(
    val downloadUrl: String,
    val expiresAt: OffsetDateTime,
    val fileSizeBytes: Long,
    val filename: String,
    val includedClips: List<IncludedClipDto>
)

data class IncludedClipDto(
    val clipId: UUID,
    val violationType: ViolationType,
    val filename: String
)

data class DownloadHistoryResponseDto(
    val downloads: List<DownloadHistoryItemDto>,
    val pagination: PaginationInfo
)

data class DownloadHistoryItemDto(
    val downloadId: UUID,
    val clipId: UUID?,
    val downloadType: DownloadType,
    val violationType: ViolationType?,
    val downloadedAt: OffsetDateTime
)
