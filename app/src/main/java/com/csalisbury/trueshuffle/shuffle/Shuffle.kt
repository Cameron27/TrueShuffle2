package com.csalisbury.trueshuffle.shuffle

import com.csalisbury.trueshuffle.services.SpotifyApiService
import kaaes.spotify.webapi.android.models.PlaylistTrack
import javax.inject.Inject

open class Shuffle @Inject constructor(private val apiService: SpotifyApiService) {
    open val name: String = "Shuffle"
    open val suffix: String = "Shuffled"
    open val sortOrder = 1

    fun shuffle(playlistId: String) {
        val tracks = apiService.getTracks(playlistId)

        val newTracks = shuffleAlgorithm(tracks)

        val playlist = apiService.getPlaylist(playlistId)
        val newPlaylistName = "${playlist.name} $suffix"
        val shuffledPlaylist = apiService.getOrCreatePlaylistByName(newPlaylistName)

        apiService.removeTracksFromPlaylist(shuffledPlaylist.id)
        apiService.addTracksToPlaylist(shuffledPlaylist.id, newTracks)
    }

    internal open fun shuffleAlgorithm(tracks: List<PlaylistTrack>): List<PlaylistTrack> {
        return tracks.shuffled()
    }
}