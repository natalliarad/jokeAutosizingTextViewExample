package com.natallia.radaman.jokesTextViewResizable

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.joke_item.view.*

class JokesAdapter : RecyclerView.Adapter<JokesAdapter.JokeViewHolder>() {
    private var listener: Listener? = null
    private var jokesList = listOf<String>()

    fun update(jokesList: List<String>) {
        this.jokesList = this.jokesList + jokesList
        notifyItemInserted(this.jokesList.size)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JokesAdapter.JokeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.joke_item, parent, false)
        return JokeViewHolder(view)
    }

    override fun getItemCount() = jokesList.size

    override fun onBindViewHolder(holder: JokeViewHolder, position: Int) {
        holder.bind(position, listener)
    }

    inner class JokeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jokeTextView = itemView.jokeText
        val emojiView = itemView.emoji

        fun bind(position: Int, listener: Listener?) {
            val joke = jokesList[position]
            jokeTextView.text = joke
            itemView.setOnClickListener {
                listener?.onJokeClick(joke)
            }
            emojiView.text = getEmoji(position)
        }

        private fun getEmoji(position: Int): String {
            return when (position % 4) {
                0 -> "😀"
                1 -> "😆"
                2 -> "😂"
                else -> "🤪"
            }
        }
    }

    interface Listener {
        fun onJokeClick(joke: String)
    }
}
