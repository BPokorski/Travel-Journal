package com.example.traveljournal.presentation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource

class DescriptionViewModel:ViewModel() {
    private var descriptionData: MutableLiveData<Resource<PhotoDescriptionResponse?>?>? = null
    private lateinit var repository: Repository

    fun init(context: Context) {
        repository = Repository(context)
    }

    fun updateDescription(login: String?, photoId: String?, description: String?): MutableLiveData<Resource<PhotoDescriptionResponse?>?>? {
        descriptionData = repository.updateDescription(login, photoId, description)
        return descriptionData
    }
}