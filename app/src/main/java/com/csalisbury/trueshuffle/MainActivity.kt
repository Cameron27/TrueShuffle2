package com.csalisbury.trueshuffle

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.services.SpotifyApiService
import kaaes.spotify.webapi.android.models.PlaylistSimple
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var apiService: SpotifyApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).appComponent.inject(this)

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