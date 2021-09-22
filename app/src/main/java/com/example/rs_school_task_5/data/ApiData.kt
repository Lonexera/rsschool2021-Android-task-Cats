package com.example.rs_school_task_5.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ApiData(
    @Json(name = "") val cats: List<Cat>
)

@JsonClass(generateAdapter = true)
data class Cat(
    @Json(name = "id") val id: String,
    @Json(name = "url") val imageUrl: String
)