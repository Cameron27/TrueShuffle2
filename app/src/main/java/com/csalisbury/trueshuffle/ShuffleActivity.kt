package com.csalisbury.trueshuffle

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.shuffle.Shuffle
import javax.inject.Inject
import kotlin.concurrent.thread

class ShuffleActivity : AppCompatActivity() {
    @Inject
    lateinit var shuffles: Set<@JvmSuppressWildcards Shuffle>

    private lateinit var playlistId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).appComponent.inject(this)

        playlistId = intent.extras?.getString("PlaylistId")
            ?: throw Exception("Playlist ID not provided.")

        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_shuffle)

        setupShuffleOptions()
    }

    private fun setupShuffleOptions() {
        val playlistAdapter =
            ShuffleAdapter(shuffles.toList().sortedBy { it.sortOrder })

        playlistAdapter.setOnShuffleListener {
            thread {
                it.shuffle(playlistId)

                runOnUiThread {
                    finish()
                }
            }
        }

        val listView = findViewById<RecyclerView>(R.id.list_view)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = playlistAdapter
    }
}