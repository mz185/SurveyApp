package com.example.surveyapp.ui

/**
 * Created by Marinos Zinonos on 29/09/2022.
 */

data class PagerUiState(
    val isPreviousEnabled: Boolean = false,
    val isNextEnabled: Boolean = false,
    val counterText: String = "",
    val currentQuestion: String = "",
    val currentAnswer: String = ""
)
