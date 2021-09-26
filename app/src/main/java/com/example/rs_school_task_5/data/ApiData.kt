package com.example.rs_school_task_5.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class ApiCat(
    val breeds: List<Any>,
    val categories: List<Category>,
    val height: Int,
    val id: String,
    val url: String,
    val width: Int
)

data class Category(
    val id: Int,
    val name: String
)