package com.example.traveljournal.presentation.view.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R

class PhotoDataViewHolder(view:View): RecyclerView.ViewHolder(view) {
    var galleryImage: ImageView = itemView.findViewById(R.id.gallery_image)
    var galleryTitle: TextView = itemView.findViewById(R.id.gallery_title)

    var cardView:CardView = itemView.findViewById(R.id.card_view)

    companion object {
        fun create(parent:ViewGroup): PhotoDataViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
            return PhotoDataViewHolder(view)
        }
    }
}