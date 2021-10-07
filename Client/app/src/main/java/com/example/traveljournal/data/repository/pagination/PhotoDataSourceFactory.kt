package com.example.traveljournal.data.repository.pagination

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.traveljournal.data.model.response.PhotoDataResponse


class PhotoDataSourceFactory(val context: Context): DataSource.Factory<Int, PhotoDataResponse>() {

    private var photoDataLiveData = MutableLiveData<PageKeyedDataSource<Int, PhotoDataResponse>>()

    override fun create(): DataSource<Int, PhotoDataResponse> {
        val photoDataDataSource = PhotoDataDataSource(context)
        photoDataLiveData.postValue(photoDataDataSource)
        return photoDataDataSource
    }

    fun getPhotoDataLiveData(): MutableLiveData<PageKeyedDataSource<Int, PhotoDataResponse>> {
        return photoDataLiveData
    }

}