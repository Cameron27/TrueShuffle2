package com.csalisbury.trueshuffle

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var progressOverlay: View
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private var isShuffling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).appComponent.inject(this)

        playlistId = intent.extras?.getString("PlaylistId")
            ?: throw Exception("Playlist ID not provided.")

        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_shuffle)

        setupProgressOverlay()
        setupShuffleOptions()
    }

    private fun setupProgressOverlay() {
        progressOverlay = findViewById(R.id.progress_overlay)
        progressText = findViewById(R.id.progress_text)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun showProgress(message: String) {
        runOnUiThread {
            progressText.text = message
            progressOverlay.visibility = View.VISIBLE
            isShuffling = true
            
            // Make the window not cancelable when shuffling
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
        }
    }

    private fun hideProgress() {
        runOnUiThread {
            progressOverlay.visibility = View.GONE
            isShuffling = false
            
            // Clear the flags when shuffling is done
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        }
    }

    override fun onBackPressed() {
        if (!isShuffling) {
            super.onBackPressed()
        }
        // If shuffling, do nothing to prevent activity dismissal
    }

    private fun setupShuffleOptions() {
        val playlistAdapter =
            ShuffleAdapter(shuffles.toList().sortedBy { it.sortOrder })

        playlistAdapter.setOnShuffleListener {
            showProgress("Starting shuffle process...")
            
            thread {
                try {
                    // Using callbacks to update progress
                    it.shuffle(playlistId, { step ->
                        showProgress(step)
                    })
                } catch (e: Exception) {
                    showProgress("Error: ${e.message}")
                    Thread.sleep(1500) // Show error briefly
                } finally {
                    hideProgress()
                    runOnUiThread {
                        finish()
                    }
                }
            }
        }

        val listView = findViewById<RecyclerView>(R.id.list_view)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = playlistAdapter
    }
}