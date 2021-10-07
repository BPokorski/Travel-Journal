package com.example.traveljournal.presentation.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource

class DescriptionViewModel:ViewModel() {
    private var dataData: MutableLiveData<Resource<PhotoDataResponse?>?>? = null
    private lateinit var repository: Repository

    fun init(context: Context) {
        repository = Repository(context)
    }

    fun updateDescription(login: String?, photoId: String?, description: String?): MutableLiveData<Resource<PhotoDataResponse?>?>? {
        dataData = repository.updateDescription(login, photoId, description)
        return dataData
    }
}