package com.ohdodok.catchytape.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ohdodok.catchytape.core.data.BuildConfig
import com.ohdodok.catchytape.core.data.datasource.TokenLocalDataSource
import com.ohdodok.catchytape.core.data.di.qualifier.AuthInterceptor
import com.ohdodok.catchytape.core.data.di.qualifier.ErrorInterceptor
import com.ohdodok.catchytape.core.data.model.ErrorResponse
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import timber.log.Timber
import java.net.ConnectException
import javax.inject.Singleton
import javax.net.ssl.SSLHandshakeException


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @AuthInterceptor
    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenDataSource: TokenLocalDataSource): Interceptor {

        val accessToken = runBlocking { tokenDataSource.getAccessToken() }

        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            chain.proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor.Logger { message -> Timber.tag("okHttp").d(message) }
        return HttpLoggingInterceptor(logger)
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptor authInterceptor: Interceptor,
        @ErrorInterceptor errorInterceptor : Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @ErrorInterceptor
    @Singleton
    @Provides
    fun provideErrorInterceptor(): Interceptor {
        return Interceptor { chain ->
            try {
                val response = chain.proceed(chain.request())
                if (response.isSuccessful) return@Interceptor response
                val errorString = response.body?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorString ?: "")

                when (errorResponse.errorCode) { // TODO : 서버가 정해 지면, statusCode 대신, 변경 예정
                    400 -> throw CtException(errorResponse.message, CtErrorType.BAD_REQUEST)
                    401 -> throw CtException(errorResponse.message, CtErrorType.WRONG_TOKEN)
                    409 -> throw CtException(errorResponse.message, CtErrorType.DUPLICATED_NICKNAME)
                    500 -> throw CtException(errorResponse.message, CtErrorType.SERVER)
                    else -> throw CtException(errorResponse.message, CtErrorType.IO)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> throw CtException(e.message, CtErrorType.CONNECTION)
                    is SSLHandshakeException -> throw CtException(e.message, CtErrorType.SSL_HAND_SHAKE)
                    else -> throw CtException(e.message, CtErrorType.IO)
                }
            }
        }
    }
}
