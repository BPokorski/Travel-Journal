package com.example.traveljournal.data.repository.pagination

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse


class PhotoDescriptionSourceFactory(val context: Context): DataSource.Factory<Int, PhotoDescriptionResponse>() {

    private var photoDescriptionLiveData = MutableLiveData<PageKeyedDataSource<Int, PhotoDescriptionResponse>>()

    override fun create(): DataSource<Int, PhotoDescriptionResponse> {
        val photoDescriptionDataSource = PhotoDescriptionDataSource(context)
        photoDescriptionLiveData.postValue(photoDescriptionDataSource)
        return photoDescriptionDataSource
    }


    fun getPhotoDescriptionLiveData(): MutableLiveData<PageKeyedDataSource<Int, PhotoDescriptionResponse>> {
        return photoDescriptionLiveData
    }

}