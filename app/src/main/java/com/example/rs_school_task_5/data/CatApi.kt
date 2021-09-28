package com.example.rs_school_task_5.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApi {

    @GET("/v1/images/search?api_key=e36165e9-704b-486a-af2a-6f961c23d505")
    suspend fun getListOfCats(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<ApiCat>>
}

object CatService {

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.thecatapi.com")
        .build()

    private val catService = retrofit.create(CatApi::class.java)

    suspend fun getListOfCats(page: Int, pageSize: Int): Response<List<ApiCat>> {
        return withContext(Dispatchers.IO) {
            catService.getListOfCats(page, pageSize)
        }
    }
}
