package com.ohdodok.catchytape.core.data.di

import com.ohdodok.catchytape.core.data.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideSignupApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}