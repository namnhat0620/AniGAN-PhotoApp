package com.kltn.anigan.domain.validators.username

data class UsernameValidationState(
    val hasValue: Boolean = false,
    val validateSpace: Boolean = false,
    val successful: Boolean = false
)

