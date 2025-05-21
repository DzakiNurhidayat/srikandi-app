package org.example.project.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.example.project.data.remote.AuthTokenProvider
import org.example.project.data.repositories.AuthRepository

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideAuthTokenProvider(
        sharedPreferences: SharedPreferences
    ): AuthTokenProvider = AuthRepository(sharedPreferences)
}
