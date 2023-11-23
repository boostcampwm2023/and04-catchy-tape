package com.ohdodok.catchytape.core.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ohdodok.catchytape.core.data.BuildConfig
import com.ohdodok.catchytape.core.data.model.ErrorResponse
import com.ohdodok.catchytape.core.domain.model.CtErrorType
import com.ohdodok.catchytape.core.domain.model.CtException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

    @Singleton
    @Provides
    fun provideOkHttpClient(errorInterceptor: Interceptor): OkHttpClient {
        val logger = HttpLoggingInterceptor.Logger { message -> Timber.tag("okHttp").d(message) }
        val httpInterceptor = HttpLoggingInterceptor(logger)
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(errorInterceptor)
            .addInterceptor(httpInterceptor)
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


    @Singleton
    @Provides
    @Throws(RuntimeException::class)
    fun provideErrorInterceptor(): Interceptor {
        return Interceptor { chain ->
            try {
                val response = chain.proceed(chain.request())
                if (response.isSuccessful) return@Interceptor response

                val errorString = response.body?.string()
                val errorResponse = Json.decodeFromString<ErrorResponse>(errorString ?: "")

                when (errorResponse.statusCode) { // TODO : 서버가 정해 지면, statusCode 대신, 변경 예정
                    400 -> throw CtException(errorResponse.message, CtErrorType.BAD_REQUEST)
                    401 -> throw CtException(errorResponse.message, CtErrorType.WRONG_TOKEN)
                    409 -> throw CtException(errorResponse.message, CtErrorType.DUPLICATED_NICKNAME)
                    500 -> throw CtException(errorResponse.message, CtErrorType.SERVER)
                    else -> response
                }
            } catch (e: Exception) {
                when (e) {
                    is ConnectException -> throw CtException(e.message ?: "", CtErrorType.CONNECTION)
                    is SSLHandshakeException -> throw CtException(e.message ?: "", CtErrorType.SSL_HAND_SHAKE)
                    else -> throw CtException(e.message ?: "", CtErrorType.IO)
                }
            }
        }
    }
}
