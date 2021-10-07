package com.example.traveljournal.presentation.viewModel

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.utils.PhotoDataUtils
import com.example.traveljournal.utils.FileUtils
import java.io.File

class CameraViewModel: ViewModel(){

    private var photoData: MutableLiveData<Resource<PhotoDataResponse?>?>? = null
    private lateinit var repository:Repository
    private lateinit var photoDataUtils: PhotoDataUtils

    private var location:Location? = null
    fun init(context: Context) {
        repository = Repository(context)
        photoDataUtils = PhotoDataUtils(context)

    }
    fun addPhoto(login: String?, file: File?, location: Location?): MutableLiveData<Resource<PhotoDataResponse?>?>? {
        var fileUtils = FileUtils()

        if (location != null) {
            fileUtils.setGeoTag(file, location!!)
        }

       var multipart = fileUtils.fileToMultiPartConverter(file)
        photoData = repository.addPhoto(login, multipart)
        return photoData
    }

    fun saveAddressInfo(photoData: PhotoDataResponse?) {
        photoDataUtils.saveAddressInfo(photoData)
    }
}