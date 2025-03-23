package com.csalisbury.trueshuffle.shuffle

import com.csalisbury.trueshuffle.services.SpotifyApiService
import kaaes.spotify.webapi.android.models.PlaylistTrack
import javax.inject.Inject

open class Shuffle @Inject constructor(private val apiService: SpotifyApiService) {
    open val name: String = "Shuffle"
    open val suffix: String = "Shuffled"
    open val sortOrder = 1

    // Keep original method for backward compatibility
    fun shuffle(playlistId: String) {
        shuffle(playlistId) { _ -> }
    }

    // Add new method with progress callback
    fun shuffle(playlistId: String, progressCallback: (String) -> Unit) {
        progressCallback("Downloading playlist tracks...")
        val tracks = apiService.getTracks(playlistId)
        
        progressCallback("Shuffling tracks...")
        val newTracks = shuffleAlgorithm(tracks)
        
        progressCallback("Getting destination playlist...")
        val playlist = apiService.getPlaylist(playlistId)
        val newPlaylistName = "${playlist.name} $suffix"
        val shuffledPlaylist = apiService.getOrCreatePlaylistByName(newPlaylistName)
        
        progressCallback("Removing old tracks...")
        apiService.removeTracksFromPlaylist(shuffledPlaylist.id)
        
        progressCallback("Adding newly shuffled tracks...")
        apiService.addTracksToPlaylist(shuffledPlaylist.id, newTracks)
        
        progressCallback("Shuffle complete!")
    }

    internal open fun shuffleAlgorithm(tracks: List<PlaylistTrack>): List<PlaylistTrack> {
        return tracks.shuffled()
    }
}