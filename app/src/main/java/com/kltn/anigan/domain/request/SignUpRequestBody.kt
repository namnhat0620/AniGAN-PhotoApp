package com.kltn.anigan.domain.request

data class SignUpRequestBody (
    val username: String,
    val enabled: Boolean,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)