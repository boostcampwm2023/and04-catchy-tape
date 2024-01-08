package com.ohdodok.catchytape.core.domain.usecase.playlist

import com.ohdodok.catchytape.core.domain.model.Music
import com.ohdodok.catchytape.core.domain.model.Playlist
import com.ohdodok.catchytape.core.domain.repository.PlaylistRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetPlaylistsUseCaseTest : BehaviorSpec({
    given("GetPlaylistsUseCase 호출 하는 상황에서") {
        val playlistRepository: PlaylistRepository = mockk()
        val getPlaylistsUseCase = GetPlaylistsUseCase(playlistRepository)
        val dummyRecentMusics = listOf(
            Music(
                id = "odio",
                title = "dis",
                artist = "epicurei",
                imageUrl = "https://duckduckgo.com/?q=dolorum",
                musicUrl = "https://search.yahoo.com/search?p=volutpat"
            ),
            Music(
                id = "quot",
                title = "reque",
                artist = "iuvaret",
                imageUrl = "http://www.bing.com/search?q=efficitur",
                musicUrl = "https://www.google.com/#q=maximus"
            )
        )

        val dummyPlaylists = listOf(
            Playlist(
                id = 7316,
                title = "maiorum",
                thumbnailUrl = "http://www.bing.com/search?q=novum",
                trackSize = 5275
            ),
            Playlist(
                id = 7862,
                title = "dictum",
                thumbnailUrl = "https://duckduckgo.com/?q=commune",
                trackSize = 2537
            )
        )

        `when`("playlistId이 RECENT_PLAYLIST_ID 이라면") {
            every { playlistRepository.getPlaylists() } returns flow { emit(dummyPlaylists) }
            every { playlistRepository.getRecentPlaylist() } returns flow { emit(dummyRecentMusics) }
            val result = getPlaylistsUseCase.invoke().first()
            val excepted = Playlist(
                id = RECENT_PLAYLIST_ID,
                title = "최근 재생 목록",
                thumbnailUrl = "https://duckduckgo.com/?q=dolorum",
                trackSize = 2,
            )

            then("recentMusics 들로 새로운 Playlist를 만들고 합치고, id로 정렬한 새로운 Playlist를 반환한다.") {
                result.first() shouldBe excepted
            }
        }
    }

})
