package com.kltn.anigan.domain.validators.username

class ValidateUsername {
    fun execute(username: String): UsernameValidationState {
        val validateValue = validateValue(username)
        val validateSpace = validateSpace(username)

        val hasError = listOf(
            validateValue,
            validateSpace
        ).all {it}

        return UsernameValidationState(
            hasValue = validateValue,
            validateSpace = validateSpace,
            successful = hasError
        )
    }


    private fun validateValue(username: String): Boolean =
        username.isNotBlank()

    private fun validateSpace(value: String): Boolean = !value.contains(" ")

}

