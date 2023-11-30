package com.ohdodok.catchytape.core.data.di

import com.ohdodok.catchytape.core.data.repository.AuthRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.MusicRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.PlaylistRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.UploadRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.UserTokenRepositoryImpl
import com.ohdodok.catchytape.core.data.repository.UuidRepositoryImpl
import com.ohdodok.catchytape.core.domain.repository.AuthRepository
import com.ohdodok.catchytape.core.domain.repository.MusicRepository
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import com.ohdodok.catchytape.core.domain.repository.UploadRepository
import com.ohdodok.catchytape.core.domain.repository.UserTokenRepository
import com.ohdodok.catchytape.core.domain.repository.UuidRepository
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
    fun bindUrlRepository(urlRepositoryImpl: UploadRepositoryImpl): UploadRepository

    @Binds
    @Singleton
    fun bindUuidRepository(uuidRepositoryImpl: UuidRepositoryImpl): UuidRepository

    @Binds
    @Singleton
    fun bindPlaylistRepository(playlistRepositoryImpl: PlaylistRepositoryImpl): PlaylistRepository
}