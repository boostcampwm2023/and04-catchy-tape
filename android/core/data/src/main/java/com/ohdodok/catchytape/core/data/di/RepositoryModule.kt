package com.ohdodok.catchytape.core.data.di

import com.ohdodok.catchytape.core.data.repository.AuthRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.MusicRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.UrlRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.UserTokenRepositoryImpl
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.repository.UrlRepository
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindUserTokenRepository(userTokenRepositoryImpl: UserTokenRepositoryImpl): UserTokenRepository

    @Binds
    @Singleton
    fun bindMusicRepository(musicRepositoryImpl: MusicRepositoryImpl): MusicRepository

    @Binds
    @Singleton
    fun bindUrlRepository(urlRepositoryImpl: UrlRepositoryImpl): UrlRepository

}