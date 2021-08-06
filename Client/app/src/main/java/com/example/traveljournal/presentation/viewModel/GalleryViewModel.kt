package com.example.traveljournal.presentation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.data.repository.pagination.PhotoDescriptionSourceFactory
import com.example.traveljournal.utils.DescriptionUtils

class GalleryViewModel:ViewModel() {

    lateinit var photoDescriptionPagedList: LiveData<PagedList<PhotoDescriptionResponse>>
    lateinit var liveDataSource:LiveData<PageKeyedDataSource<Int, PhotoDescriptionResponse>>
    private lateinit var descriptionUtils: DescriptionUtils
    fun init(context: Context) {
        val photoDescriptionSourceFactory = PhotoDescriptionSourceFactory(context)

        descriptionUtils = DescriptionUtils(context)
        liveDataSource = photoDescriptionSourceFactory.getPhotoDescriptionLiveData()

        val config = PagedList.Config.Builder()
                .setPageSize(3)
                .setEnablePlaceholders(false)
                .build()

        photoDescriptionPagedList = LivePagedListBuilder<Int, PhotoDescriptionResponse>(photoDescriptionSourceFactory, config).build()
    }

    fun saveAddressInfo(photoDescription: PhotoDescriptionResponse?) {
        descriptionUtils.saveAddressInfo(photoDescription)
    }
}