package com.csalisbury.trueshuffle.shuffle

import com.csalisbury.trueshuffle.services.SpotifyApiService
import kaaes.spotify.webapi.android.models.PlaylistTrack
import javax.inject.Inject
import kotlin.math.min

class LimitedShuffle @Inject constructor(apiService: SpotifyApiService) : Shuffle(apiService) {
    override val name = "Limited Shuffle"
    override val suffix = "Limited"
    override val sortOrder = 2

    @ShuffleProperty
    var artistLimit = 15

    override fun shuffleAlgorithm(tracks: List<PlaylistTrack>): List<PlaylistTrack> {
        return tracks.groupBy { it.track.artists.firstOrNull()?.name ?: "" }
            .map { it.value.shuffled() }
            .map { it.subList(0, min(it.count(), artistLimit)) }
            .flatten()
            .shuffled()
    }
}