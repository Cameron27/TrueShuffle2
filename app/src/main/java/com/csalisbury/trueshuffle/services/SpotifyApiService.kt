package com.csalisbury.trueshuffle.services

import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.PlaylistSimple
import kaaes.spotify.webapi.android.models.UserPrivate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyApiService @Inject constructor() {
    private var spotify: SpotifyService? = null

    private var _user: UserPrivate? = null
    private val user: UserPrivate
        get() {
            if (spotify != null && _user == null) {
                _user = spotify!!.me
            }

            return _user!!
        }

    fun setToken(value: String) {
        if (value.isNotEmpty()) {
            val api = SpotifyApi()
            api.setAccessToken(value)
            spotify = api.service
        } else {
            spotify = null
        }
    }

    fun getPlaylists(): List<PlaylistSimple> {
        if (spotify == null)
            throw Exception("Spotify token not set.")

        var playlists: List<PlaylistSimple> = listOf()
        var page = 0
        val pageSize = 10
        do {
            val playlistPage = spotify!!.getMyPlaylists(
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