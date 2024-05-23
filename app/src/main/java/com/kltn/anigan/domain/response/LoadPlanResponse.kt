package com.kltn.anigan.domain.response

data class LoadPlanResponse (
    val statusCode: Int,
    val message: String,
    val data: PlanPaginationResponse
)

data class PlanPaginationResponse(
    val limit: Int,
    val total_record: Int,
    val list: List<Plan>
)

data class Plan(
    val plan_id: Int,
    val name: String,
    val amount: String,
    val number_of_generation: Int,
    val period: Int
)