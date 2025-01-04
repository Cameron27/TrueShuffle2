package com.csalisbury.trueshuffle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.shuffle.Shuffle

class ShuffleAdapter(private val shuffles: List<Shuffle>) :
    RecyclerView.Adapter<ShuffleAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playlistName: TextView = view.findViewById(R.id.shuffle_name_txt)
        val shuffleButton: Button = view.findViewById(R.id.shuffle_btn)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shuffle_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.playlistName.text = shuffles[position].name

        viewHolder.shuffleButton.setOnClickListener {
            onShuffleAction(shuffles[position])
        }
    }

    private var onShuffleAction: (Shuffle) -> Unit = {}

    fun setOnShuffleListener(action: (Shuffle) -> Unit) {
        onShuffleAction = action
    }

    override fun getItemCount() = shuffles.size
}
