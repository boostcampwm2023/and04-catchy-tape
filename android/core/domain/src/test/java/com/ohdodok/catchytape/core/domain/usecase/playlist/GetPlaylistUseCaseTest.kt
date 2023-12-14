package com.ohdodok.catchytape.core.domain.usecase.playlist

import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetPlaylistUseCaseTest : BehaviorSpec({

    given("accessToken 을 얻을 수 있는 상황에서") {
        val playlistRepository: PlaylistRepository = mockk()
        val getPlaylistUseCase = GetPlaylistUseCase(playlistRepository)
        val dummyRecentPlaylist = listOf(
            Music(
                id = "odio",
                title = "dis",
                artist = "epicurei",
                imageUrl = "https://duckduckgo.com/?q=dolorum",
                musicUrl = "https://search.yahoo.com/search?p=volutpat"
            )
        )

        val dummyNormalPlaylist = listOf(
            Music(
                id = "feugiat",
                title = "fabellas",
                artist = "adolescens",
                imageUrl = "https://www.google.com/#q=sollicitudin",
                musicUrl = "http://www.bing.com/search?q=invenire"
            )
        )

        `when`("playlistId이 RECENT_PLAYLIST_ID 이라면") {
            val testPlaylistId = 0 // RECENT_PLAYLIST_ID 상수 값은 0 이다.
            every { playlistRepository.getRecentPlaylist() } returns flow {
                emit(dummyRecentPlaylist)
            }
            val result = getPlaylistUseCase.invoke(playlistId = testPlaylistId).first()
            then("getRecentPlaylist 를 호출 한다") { result shouldBe dummyRecentPlaylist }
        }

        `when`("playlistId이 RECENT_PLAYLIST_ID 이 아니면") {
            val testPlaylistId = 123
            every { playlistRepository.getPlaylist(testPlaylistId) } returns flow {
                emit(dummyNormalPlaylist)
            }
            val result = getPlaylistUseCase.invoke(playlistId = testPlaylistId).first()
            then("getPlaylist 를 호출 한다") { result shouldBe dummyNormalPlaylist }
        }
    }
})
