package com.csalisbury.trueshuffle

import android.text.InputType.TYPE_CLASS_NUMBER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.csalisbury.trueshuffle.shuffle.Shuffle
import com.csalisbury.trueshuffle.shuffle.ShuffleProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.typeOf

class ShuffleAdapter(private val shuffles: List<Shuffle>) :
    RecyclerView.Adapter<ShuffleAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playlistName: TextView = view.findViewById(R.id.shuffle_name_txt)
        val shuffleButton: Button = view.findViewById(R.id.shuffle_btn)
    }

    private val viewPropertyMap: MutableList<() -> Unit> = mutableListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.shuffle_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val shuffle = shuffles[position]

        viewHolder.playlistName.text = shuffle.name

        setShuffleProperties(shuffle, viewHolder)

        viewHolder.shuffleButton.setOnClickListener {
            onShuffleAction(shuffle)
        }
    }

    private fun setShuffleProperties(shuffle: Shuffle, viewHolder: ViewHolder) {
        val layout = viewHolder.itemView as ConstraintLayout

        var currentLastItem = viewHolder.playlistName

        val properties = shuffle::class.declaredMemberProperties
        for (property: KProperty1<out Shuffle, *> in properties) {
            if (!property.hasAnnotation<ShuffleProperty>()) {
                continue
            }

            if (property !is KMutableProperty1<out Shuffle, *>) {
                throw Exception("${ShuffleProperty::class.simpleName} must be mutable.")
            }

            when (val type: KType = property.returnType) {
                typeOf<Int>() -> {
                    val input = EditText(viewHolder.itemView.context)
                    val id = View.generateViewId()
                    input.id = id
                    input.inputType = TYPE_CLASS_NUMBER
                    input.width = 100
                    input.setText(
                        (property as KProperty1<Shuffle, Int>).get(shuffle).toString()
                    )
                    layout.addView(input)

                    val layoutParams = input.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.endToStart = R.id.shuffle_btn
                    layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    layoutParams.marginEnd = 8

                    val currentLastItemLayoutParams =
                        currentLastItem.layoutParams as ConstraintLayout.LayoutParams
                    currentLastItemLayoutParams.endToStart = input.id

                    viewPropertyMap.add({
                        val value = input.getText().toString().toIntOrNull() ?: return@add

                        val propertyMut = property as KMutableProperty1<Shuffle, Int>
                        propertyMut.set(shuffle, value)
                    })

                    currentLastItem = input
                }

                else -> {
                    throw Exception("${ShuffleProperty::class.simpleName} of type $type not supported.")
                }
            }
        }
    }

    private var onShuffleAction: (Shuffle) -> Unit = {}

    fun setOnShuffleListener(action: (Shuffle) -> Unit) {
        onShuffleAction = {
            viewPropertyMap.forEach { it() }
            action(it)
        }
    }

    override fun getItemCount() = shuffles.size
}
