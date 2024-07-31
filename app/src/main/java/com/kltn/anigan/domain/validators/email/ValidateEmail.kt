package com.kltn.anigan.domain.validators.email

class ValidateEmail {
    fun excute(value: String): EmailValidationState {
        val isValid = validateEmail(value)

        val hasError = listOf(
            isValid
        ).all {it}

        return EmailValidationState(
            isValid = isValid,
            successful = hasError
        )
    }

    private fun validateEmail(email: String): Boolean =
        email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
}