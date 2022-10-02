package com.example.surveyapp.data.repos

import com.example.surveyapp.data.models.Answer
import com.example.surveyapp.data.models.Question
import com.example.surveyapp.data.web.WebService
import com.example.surveyapp.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

class QuestionsRepository @Inject constructor(
    private val webService: WebService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadQuestions(): Result<List<Question>> {
        return withContext(dispatcher) {
            try {
                Result.success(webService.fetchQuestions())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun submitAnswer(answer: Answer): Result<Unit> {
        return withContext(dispatcher) {
            try {
                Result.success(webService.submitAnswer(answer))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}