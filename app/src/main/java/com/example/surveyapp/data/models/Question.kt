package com.example.surveyapp.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Marinos Zinonos on 26/09/2022.
 */

data class Question(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("question")
    val question: String = ""
)
