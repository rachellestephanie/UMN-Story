package com.example.if570_lab_uts_rachellestephanierianto_00000082784

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StoryAdapter(private val stories: List<Story>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val captionDisplay: TextView = itemView.findViewById(R.id.caption_display)
        val imageDisplay: ImageView = itemView.findViewById(R.id.image_display)

        fun bind(story: Story) {
            captionDisplay.text = story.caption
            Log.d("Adapter", "Binding story: ${story.caption}")

            if (!story.imageUrl.isNullOrEmpty()) {
                // Show the ImageView and load the image
                imageDisplay.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(story.imageUrl)
                    .into(imageDisplay)
            } else {
                // Hide the ImageView when imageUrl is null
                imageDisplay.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int {
        return stories.size
    }
}
