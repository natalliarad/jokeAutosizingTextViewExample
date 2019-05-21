package com.natallia.radaman.jokesTextViewResizable

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface JokesApi {
    @GET("search")
    @Headers("Accept: application/json")
    fun search(@Query("page") page: Int): Call<JokeSearchResult>
}

data class JokeSearchResult(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("next_page") val nextPage: Int,
    val results: List<Joke>
)

data class Joke(val joke: String)

object JokesRepository {
    private const val API_BASE_URL = "https://icanhazdadjoke.com"
    private val interceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(JokesApi::class.java)

    fun searchJokes(callback: RepositoryCallback<JokeSearchResult, String>, page: Int = 1) {
        val call = apiService.search(page)
        call.enqueue(object : Callback<JokeSearchResult> {
            override fun onFailure(call: Call<JokeSearchResult>?, t: Throwable?) {
                callback.onError(t?.message ?: "Unknown error")
            }

            override fun onResponse(
                call: Call<JokeSearchResult>?,
                response: Response<JokeSearchResult>?
            ) {
                response?.let {
                    if (it.isSuccessful) {
                        response.body()?.let {
                            callback.onSuccess(it)
                            return
                        }
                    }
                }
                callback.onError("Couldn't parse result")
            }
        })
    }
}

interface RepositoryCallback<T, E> {
    fun onSuccess(entity: T)
    fun onError(entity: E)
}
