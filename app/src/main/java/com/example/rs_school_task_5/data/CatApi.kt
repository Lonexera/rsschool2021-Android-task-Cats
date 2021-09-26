package com.example.rs_school_task_5.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CatApi {

    @GET("/v1/images/search?limit=10&api_key=e36165e9-704b-486a-af2a-6f961c23d505")
    fun getListOfCats(@Query("page") page: Int): Call<MutableList<ApiCat>>
}

object TheCatApiImpl {

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.thecatapi.com")
        .build()

    private val catService = retrofit.create(CatApi::class.java)

    suspend fun getListOfCats(page: Int): List<Cat> {
        return withContext(Dispatchers.IO) {
            val catsResponse = catService.getListOfCats(page).execute()
            val apiCatsList = catsResponse.body() as List<ApiCat>
            val cats = mutableListOf<Cat>()
            apiCatsList.map { apiCat ->
                Cat(
                    apiCat.id,
                    apiCat.url
                )
            }
        }
    }

}