package com.csalisbury.trueshuffle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.services.SpotifyApiService
import kaaes.spotify.webapi.android.models.PlaylistSimple
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var _apiService: SpotifyApiService? = null
    private val apiService: SpotifyApiService
        get() {
            if (_apiService == null) {
                val token = getSharedPreferences("spotify", MODE_PRIVATE)
                    .getString("token", "") ?: ""
                _apiService = SpotifyApiService(token)
            }
            return _apiService!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPlaylistList()
    }

    private fun setupPlaylistList() {
        thread {
            val playlists = apiService.getPlaylists()
            val playlistAdapter = PlaylistAdapter(playlists)

            playlistAdapter.setOnShuffleListener(::showShufflePlaylistPopup)

            runOnUiThread {
                val listView = findViewById<RecyclerView>(R.id.list_view)
                listView.layoutManager = LinearLayoutManager(this)
                listView.adapter = playlistAdapter
            }
        }
    }

    private fun showShufflePlaylistPopup(playlist: PlaylistSimple) {
        val intent = Intent(this, ShuffleActivity::class.java)
        startActivity(intent)
    }
}