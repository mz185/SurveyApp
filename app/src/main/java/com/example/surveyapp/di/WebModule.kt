package com.example.surveyapp.di

import com.example.surveyapp.data.web.WebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WebModule {

    @Provides
    @Singleton
    fun provideWebService(): WebService {
        return WebService.create()
    }
}