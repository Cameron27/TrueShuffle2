package com.csalisbury.trueshuffle.services

import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyService
import kaaes.spotify.webapi.android.models.Playlist
import kaaes.spotify.webapi.android.models.PlaylistSimple
import kaaes.spotify.webapi.android.models.PlaylistTrack
import kaaes.spotify.webapi.android.models.TrackToRemove
import kaaes.spotify.webapi.android.models.TracksToRemove
import kaaes.spotify.webapi.android.models.UserPrivate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

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
            .sortedBy { p -> p.name }
    }

    fun getTracks(playlistId: String): List<PlaylistTrack> {
        if (spotify == null)
            throw Exception("Spotify token not set.")

        var tracks: List<PlaylistTrack> = listOf()
        var page = 0
        val pageSize = 100
        do {
            val tracksPage = spotify!!.getPlaylistTracks(
                user.id,
                playlistId,
                mapOf(
                    SpotifyService.OFFSET to page * pageSize,
                    SpotifyService.LIMIT to pageSize,
                    SpotifyService.FIELDS to "items.track(uri,artists.name),next"
                )
            )

            tracks = tracks + tracksPage.items
            page++
        } while (!tracksPage.next.isNullOrEmpty())

        return tracks
    }

    fun removeTracksFromPlaylist(playlistId: String) {
        if (spotify == null)
            throw Exception("Spotify token not set.")

        val tracks = getTracks(playlistId)

        val pageSize = 100
        var page = 0
        var subTracks: List<PlaylistTrack> = listOf()
        while (page * pageSize < tracks.count() &&
            tracks.subList(page * pageSize, min(tracks.count(), (page + 1) * pageSize))
                .also { subTracks = it }.isNotEmpty()
        ) {
            val tracksToRemove = TracksToRemove()
            tracksToRemove.tracks = subTracks.map {
                val t = TrackToRemove()
                t.uri = it.track.uri
                t
            }

            spotify?.removeTracksFromPlaylist(user.id, playlistId, tracksToRemove)

            page++
        }
    }

    fun addTracksToPlaylist(playlistId: String, tracks: List<PlaylistTrack>) {
        if (spotify == null)
            throw Exception("Spotify token not set.")

        val pageSize = 100
        var page = 0
        var subTracks: List<PlaylistTrack> = listOf()
        while (page * pageSize < tracks.count() &&
            tracks.subList(page * pageSize, min(tracks.count(), (page + 1) * pageSize))
                .also { subTracks = it }.isNotEmpty()
        ) {
            val uris = subTracks.map { it.track.uri }

            spotify!!.addTracksToPlaylist(
                user.id,
                playlistId,
                mapOf(),
                mapOf("uris" to uris, "position" to 0)
            )

            page++
        }
    }

    fun getPlaylist(playlistId: String): Playlist {
        if (spotify == null)
            throw Exception("Spotify token not set.")

        return spotify!!.getPlaylist(user.id, playlistId)
    }

    fun getOrCreatePlaylistByName(name: String): Playlist {
        if (spotify == null)
            throw Exception("Spotify token not set.")

        val playlist = getPlaylists().firstOrNull { it.name == name }
        if (playlist != null) {
            return spotify!!.getPlaylist(user.id, playlist.id)
        }

        val newPlaylist = spotify!!.createPlaylist(
            user.id,
            mapOf("name" to name, "description" to "", "public" to false)
        )

        return newPlaylist
    }
}