package com.example.traveljournal.presentation.view.recyclerView

import com.example.traveljournal.data.model.response.PhotoDataResponse

interface CardClickListener {
    fun onCardClickListener(photoData: PhotoDataResponse?, position: Int)
}