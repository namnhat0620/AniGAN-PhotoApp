package com.kltn.anigan.domain.response

data class GetMyPlanResponse (
    val statusCode: Int,
    val message: String,
    val data: MyPlan
)

data class MyPlan(
    val expired_day: String,
    val remain_generation: Int
)