package com.csalisbury.trueshuffle

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.shuffle.Shuffle
import javax.inject.Inject

class ShuffleActivity : AppCompatActivity() {
    @Inject
    lateinit var shuffles: Set<@JvmSuppressWildcards Shuffle>

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_shuffle)

        setupShuffleOptions()
    }

    private fun setupShuffleOptions() {
        val playlistAdapter = ShuffleAdapter(shuffles.toList().sortedBy { it.sortOrder })

        val listView = findViewById<RecyclerView>(R.id.list_view)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = playlistAdapter
    }
}