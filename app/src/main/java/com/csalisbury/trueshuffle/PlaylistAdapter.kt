package com.csalisbury.trueshuffle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kaaes.spotify.webapi.android.models.PlaylistSimple

class PlaylistAdapter(private val playlists: List<PlaylistSimple>) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playlistName: TextView = view.findViewById(R.id.playlist_name_txt)
        val shuffleButton: Button = view.findViewById(R.id.shuffle_btn)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.playlist_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.playlistName.text = playlists[position].name

        viewHolder.shuffleButton.setOnClickListener {
            onShuffleAction(playlists[position])
        }
    }

    private var onShuffleAction: (PlaylistSimple) -> Unit = {}

    fun setOnShuffleListener(action: (PlaylistSimple) -> Unit) {
        onShuffleAction = action
    }

    override fun getItemCount() = playlists.size
}
