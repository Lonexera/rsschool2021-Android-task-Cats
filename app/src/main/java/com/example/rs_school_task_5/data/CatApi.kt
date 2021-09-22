package com.example.rs_school_task_5.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface CatApi {


    @GET("/v1/images/search?api_key=e36165e9-704b-486a-af2a-6f961c23d505")
    suspend fun getListOfCats(): ApiData

}

object TheCatApiImpl {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://api.thecatapi.com")
        .build()

    private val catService = retrofit.create(CatApi::class.java)

    suspend fun getListOfCats(): List<Cat> {
        return withContext(Dispatchers.IO) {
            catService.getListOfCats().cats
        }
    }

}