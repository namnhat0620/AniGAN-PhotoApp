package com.kltn.anigan.domain.validators.name

class ValidateName {
    fun excute(value: String): NameValidationState {
        val isValid = validate(value)

        val hasError = listOf(
            isValid
        ).all {it}

        return NameValidationState(
            isValid = isValid,
            successful = hasError
        )
    }

    private fun validate(value: String): Boolean =
        value.isNotBlank()
}