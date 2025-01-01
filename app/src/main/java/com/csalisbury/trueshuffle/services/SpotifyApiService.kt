package com.csalisbury.trueshuffle.services

import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.PlaylistSimple
import kaaes.spotify.webapi.android.models.UserPrivate

class SpotifyApiService(token: String) {
    private val spotify: SpotifyService

    init {
        val api = SpotifyApi()
        api.setAccessToken(token)
        spotify = api.service
    }

    private var _user: UserPrivate? = null
    private val user: UserPrivate
        get() {
            if (_user == null) {
                _user = spotify.me
            }

            return _user!!
        }

    fun getPlaylists(): List<PlaylistSimple> {
        var playlists: List<PlaylistSimple> = listOf()
        var page = 0
        val pageSize = 10
        do {
            val playlistPage = spotify.getMyPlaylists(
                mapOf(
                    SpotifyService.OFFSET to page * pageSize,
                    SpotifyService.LIMIT to pageSize
                )
            )

            playlists = playlists + playlistPage.items
            page++
        } while (!playlistPage.next.isNullOrEmpty())

        return playlists
            .filter { p -> p.owner.id == user.id }
            .filter { p -> !p.name.endsWith("Limited") && !p.name.endsWith("Shuffled") }
            .sortedBy { p -> p.name }
    }
}