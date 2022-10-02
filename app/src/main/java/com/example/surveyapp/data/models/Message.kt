package com.example.surveyapp.data.models

/**
 * Created by Marinos Zinonos on 30/09/2022.
 */

data class Message(
    val isSuccess: Boolean,
    val message: String,
    val action: (()-> Unit)? = null
)
