package com.kltn.anigan.domain.validators.confirmPassword

class ValidateConfirmPassword {
    fun excute(value:String, password: String): ConfirmPasswordValidationState {
        val validate = validate(value, password)

        val hasError = listOf(
            validate
        ).all {it}

        return ConfirmPasswordValidationState(
            isValid = validate,
            successful = hasError
        )
    }

    private fun validate(value: String, password: String): Boolean = value == password
}