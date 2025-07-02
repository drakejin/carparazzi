package org.drakejin.carparazzi.domain.util

import java.time.OffsetDateTime
import java.util.*

/**
 * 엔티티 관련 유틸리티 함수들
 */
object EntityUtils {

    /**
     * UUID 생성
     */
    fun generateUUID(): UUID = UUID.randomUUID()

    /**
     * 현재 시간 반환
     */
    fun now(): OffsetDateTime = OffsetDateTime.now()

    /**
     * 증거 클립 만료 시간 계산 (7일 후)
     */
    fun calculateClipExpirationTime(createdAt: OffsetDateTime = now()): OffsetDateTime {
        return createdAt.plusDays(7)
    }

    /**
     * 클립 지속 시간 계산
     */
    fun calculateDuration(startTime: java.math.BigDecimal, endTime: java.math.BigDecimal): java.math.BigDecimal {
        return endTime.subtract(startTime)
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환
     */
    fun formatFileSize(bytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        return String.format("%.2f %s", size, units[unitIndex])
    }

    /**
     * 시간(초)을 MM:SS 형태로 변환
     */
    fun formatDuration(seconds: java.math.BigDecimal): String {
        val totalSeconds = seconds.toInt()
        val minutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}
