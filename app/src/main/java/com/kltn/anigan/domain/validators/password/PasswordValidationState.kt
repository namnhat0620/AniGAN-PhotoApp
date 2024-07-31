package com.kltn.anigan.domain.validators.password

data class PasswordValidationState(
    val hasMinimum: Boolean = false,
    val successful: Boolean = false
)
