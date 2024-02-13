package com.ohdodok.catchytape.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ohdodok.catchytape.core.data.BuildConfig
import com.ohdodok.catchytape.core.data.datasource.TokenLocalDataSource
import com.ohdodok.catchytape.core.data.di.qualifier.AuthInterceptor
import com.ohdodok.catchytape.core.data.di.qualifier.ErrorInterceptor
import com.ohdodok.catchytape.core.data.model.ErrorResponse
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtErrorType.Companion.ctErrorEnums
import com.ohdodok.catchytape.core.domain.model.CtException
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
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
    fun provideAuthInterceptor(tokenLocalDataSource: TokenLocalDataSource): Interceptor {

        return Interceptor { chain ->
            val accessToken = runBlocking { tokenLocalDataSource.getAccessToken() }
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()

            chain.proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor.Logger { message ->
            if (message.contains("�")) return@Logger

            Timber.tag("okHttp").d(message)
        }

        return HttpLoggingInterceptor(logger)
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @AuthInterceptor authInterceptor: Interceptor,
        @ErrorInterceptor errorInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(errorInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val jsonConfig = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @ErrorInterceptor
    @Singleton
    @Provides
    fun provideErrorInterceptor(
        authRepository: Lazy<AuthRepository>
    ): Interceptor {
        return Interceptor { chain ->
            try {
                val originalRequest = chain.request()
                val response = chain.proceed(originalRequest)
                if (response.isSuccessful) return@Interceptor response
                val errorString = response.body?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorString ?: "")

                if (errorResponse.statusCode == 401) {
                    val newAccessToken = runBlocking {
                        val authTokenResponse = authRepository.get().refreshToken()
                        authTokenResponse.first().accessToken
                    }
                    val newRequest = originalRequest.putTokenHeader(newAccessToken)
                    chain.proceed(newRequest)
                }

                val ctError = ctErrorEnums.find { it.errorCode == errorResponse.errorCode }
                if (ctError != null) {
                    throw CtException(errorResponse.message, ctError)
                } else {
                    throw CtException(errorResponse.message, CtErrorType.UN_KNOWN)
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> throw CtException(e.message, CtErrorType.CONNECTION)
                    is SSLHandshakeException -> throw CtException(
                        e.message,
                        CtErrorType.SSL_HAND_SHAKE
                    )

                    is CtException -> throw e
                    else -> throw CtException(e.message, CtErrorType.IO)
                }
            }
        }
    }

    private fun Request.putTokenHeader(accessToken: String): Request {
        return this.newBuilder()
            .addHeader("Authorization", accessToken)
            .build()
    }
}