package org.drakejin.carparazzi.domain.dto.common

/**
 * 검증 결과를 나타내는 sealed class
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorList: List<String>) : ValidationResult()

    fun isValid(): Boolean = this is Valid
    fun isInvalid(): Boolean = this is Invalid

    fun getErrors(): List<String> = when (this) {
        is Valid -> emptyList()
        is Invalid -> errorList
    }
}

/**
 * 여러 검증 결과를 합치는 확장 함수
 */
fun List<ValidationResult>.combine(): ValidationResult {
    val errors = this.flatMap { it.getErrors() }
    return if (errors.isEmpty()) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid(errors)
    }
}
