package com.example.traveljournal.presentation.view.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R

class PhotoDescriptionViewHolder(view:View): RecyclerView.ViewHolder(view) {


    var galleryImage: ImageView = itemView.findViewById(R.id.gallery_image)
    var galleryTitle: TextView = itemView.findViewById(R.id.gallery_title)

    var cardView:CardView = itemView.findViewById(R.id.card_view)
//    lateinit var onClickListener: PhotoDescriptionAdapter.CardItemClickListener
//    fun bind(photoDescription: PhotoDescriptionResponse?) {
//        if (photoDescription != null) {
//            itemView.gallery_title.text = photoDescription.country
//            var photoUrl = "http://192.168.43.153:8080/" + sessionManager.fetchLogin()  + "/photo/"  + photoDescription.photoId
//            var glideUrl: GlideUrl = GlideUrl(photoUrl, LazyHeaders.Builder()
//                    .addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken())
//                    .build())
////        var appGlide = AppGlideModule()
////
////        appGlide.with()
//
//            GlideApp.with(context)
//                    .load(glideUrl)
//                    .timeout(10000)
//                    .transform(RoundedCorners(16))
//                    .into(itemView.gallery_image)
//        }
//    }

    companion object {
        fun create(parent:ViewGroup): PhotoDescriptionViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
//                view.setOnClickListener (this)
            return PhotoDescriptionViewHolder(view)
        }
    }

//    override fun onClick(v: View?) {
//        var position = adapterPosition
//        onClickListener.onCardItemClick(position)
//    }
}