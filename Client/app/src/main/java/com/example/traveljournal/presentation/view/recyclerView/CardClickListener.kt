package com.example.traveljournal.presentation.view.recyclerView

import com.example.traveljournal.data.model.response.PhotoDescriptionResponse

interface CardClickListener {
    fun onCardClickListener(photoDescription: PhotoDescriptionResponse?, position: Int)
}