package com.example.surveyapp.data.web

import com.example.surveyapp.data.models.Answer
import com.example.surveyapp.data.models.Question
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

interface WebService {

    @GET("/questions")
    suspend fun fetchQuestions(): List<Question>

    @POST("/question/submit")
    suspend fun submitAnswer(@Body answer: Answer)

    companion object {

        private const val BASE_URL = "https://powerful-peak-54206.herokuapp.com/"

        fun create(): WebService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()
                .create(WebService::class.java)
        }
    }
}