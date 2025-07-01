package org.drakejin.carparazzi.domain.dto

import org.drakejin.carparazzi.entity.generated.tables.pojos.DownloadLogs
import java.time.OffsetDateTime
import java.util.*

data class DownloadLogDto(
    val downloadId: UUID,
    val userId: UUID,
    val clipId: UUID?,
    val downloadType: DownloadType,
    val downloadedAt: OffsetDateTime,
    val userAgent: String?,
    val ipAddress: String?
) {
    enum class DownloadType {
        SINGLE, BATCH, ZIP
    }

    companion object {
        fun fromEntity(entity: DownloadLogs): DownloadLogDto {
            return DownloadLogDto(
                downloadId = entity.downloadId,
                userId = entity.userId,
                clipId = entity.clipId,
                downloadType = DownloadType.valueOf(entity.downloadType),
                downloadedAt = entity.downloadedAt,
                userAgent = entity.userAgent,
                ipAddress = entity.ipAddress?.toString()
            )
        }
    }
}
