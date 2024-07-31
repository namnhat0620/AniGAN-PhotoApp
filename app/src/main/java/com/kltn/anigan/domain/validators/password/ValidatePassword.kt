package com.kltn.anigan.domain.validators.password

class ValidatePassword {
    fun excute(password: String): PasswordValidationState {
        val validateMinimum = validateMinimum(password)

        val hasError = listOf(
            validateMinimum
        ).all {it}

        return PasswordValidationState(
            hasMinimum = validateMinimum,
            successful = hasError
        )
    }

    private fun validateMinimum(password: String) =
        !password.contains(" ") &&
        !password.contains("\t") &&
        !password.contains("\n") &&
        password.matches(Regex(".{6,}"))
}