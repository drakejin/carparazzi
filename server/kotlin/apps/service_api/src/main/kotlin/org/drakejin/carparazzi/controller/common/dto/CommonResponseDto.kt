package org.drakejin.carparazzi.controller.common.dto

import java.time.OffsetDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val timestamp: OffsetDateTime = OffsetDateTime.now()
)

data class ErrorDetail(
    val code: String,
    val message: String,
    val details: String? = null
)

data class PaginationInfo(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)
