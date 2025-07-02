package org.drakejin.carparazzi.controller.common

import org.drakejin.carparazzi.controller.common.dto.ApiResponse
import org.drakejin.carparazzi.controller.common.dto.ErrorDetail
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.MissingRequestValueException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestValueException::class)
    fun handleMissingRequestValue(ex: MissingRequestValueException): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ApiResponse<Any>(
            success = false,
            data = null,
            error = ErrorDetail(
                code = "BAD_REQUEST",
                message = ex.reason ?: "Required request parameter is missing",
                details = ex.message
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ApiResponse<Any>(
            success = false,
            data = null,
            error = ErrorDetail(
                code = "BAD_REQUEST",
                message = "Invalid request input",
                details = ex.message
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ApiResponse<Any>(
            success = false,
            data = null,
            error = ErrorDetail(
                code = "BAD_REQUEST",
                message = "Invalid argument provided",
                details = ex.message
            )
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ApiResponse<Any>(
            success = false,
            data = null,
            error = ErrorDetail(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred",
                details = ex.message
            )
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
