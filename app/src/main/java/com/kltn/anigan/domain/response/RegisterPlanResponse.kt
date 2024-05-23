package com.kltn.anigan.domain.response

data class RegisterPlanResponse (
    val statusCode: Int,
    val message: String,
    val data: RegisterPlan
)

data class RegisterPlan(
    val number_of_generation: Int
)