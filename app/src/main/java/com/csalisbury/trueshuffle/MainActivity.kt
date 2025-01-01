package com.csalisbury.trueshuffle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.services.SpotifyApiService
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPlaylistList()
    }

    private fun setupPlaylistList() {
        thread {
            val playlists = apiService.getPlaylists()
            val dataset: List<String> = playlists.map { p -> p.name }
            val playlistAdapter = PlaylistAdapter(dataset)

            runOnUiThread {
                val listView = findViewById<RecyclerView>(R.id.list_view)
                listView.layoutManager = LinearLayoutManager(this)
                listView.adapter = playlistAdapter
            }
        }
    }
}