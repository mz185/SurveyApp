package com.example.surveyapp.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Marinos Zinonos on 30/09/2022.
 */

data class Answer(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("answer")
    val answer: String = ""
)
