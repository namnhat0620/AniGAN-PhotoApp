package com.kltn.anigan.domain.response
data class LoginResponse(
    val access_token: String,
    val refresh_token: String
)