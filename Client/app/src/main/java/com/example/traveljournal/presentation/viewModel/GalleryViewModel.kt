package com.example.traveljournal.presentation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.example.traveljournal.data.repository.pagination.PhotoDataSourceFactory
import com.example.traveljournal.utils.PhotoDataUtils

class GalleryViewModel:ViewModel() {

    lateinit var photoDataPagedList: LiveData<PagedList<PhotoDataResponse>>
    lateinit var liveDataSource:LiveData<PageKeyedDataSource<Int, PhotoDataResponse>>
    private lateinit var photoDataUtils: PhotoDataUtils

    fun init(context: Context) {
        val photoDataSourceFactory = PhotoDataSourceFactory(context)

        photoDataUtils = PhotoDataUtils(context)
        liveDataSource = photoDataSourceFactory.getPhotoDataLiveData()

        val config = PagedList.Config.Builder()
                .setPageSize(3)
                .setEnablePlaceholders(false)
                .build()

        photoDataPagedList = LivePagedListBuilder<Int, PhotoDataResponse>(photoDataSourceFactory, config).build()
    }

    fun saveAddressInfo(photoData: PhotoDataResponse?) {
        photoDataUtils.saveAddressInfo(photoData)
    }
}