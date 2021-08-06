package com.example.traveljournal.presentation.viewModel

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.JwtResponse
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.utils.DescriptionUtils
import com.example.traveljournal.utils.FileUtils
import com.example.traveljournal.utils.GpsUtils
import io.fotoapparat.parameter.Flash
import java.io.File

class CameraViewModel: ViewModel(){

    private var photoData: MutableLiveData<Resource<PhotoDescriptionResponse?>?>? = null
    private lateinit var repository:Repository
    private lateinit var descriptionUtils: DescriptionUtils
//    private lateinit var gpsUtils:GpsUtils
//    private lateinit var context:Context
    private var location:Location? = null
    fun init(context: Context) {
//        this.context = context
        repository = Repository(context)
        descriptionUtils = DescriptionUtils(context)
//        gpsUtils = GpsUtils()
//        gpsUtils.init(context)
//        location = gpsUtils.getLocation()

    }
    fun addPhoto(login: String?, file: File?, location: Location?): MutableLiveData<Resource<PhotoDescriptionResponse?>?>? {
        var fileUtils = FileUtils()

        if (location != null) {
            fileUtils.setGeoTag(file, location!!)
        }

       var multipart = fileUtils.fileToMultiPartConverter(file)
//        repository = Repository(context)
        photoData = repository.addPhoto(login, multipart)
        return photoData
    }

    fun saveAddressInfo(photoDescription: PhotoDescriptionResponse?) {
        descriptionUtils.saveAddressInfo(photoDescription)
    }

//    fun stopGps() {
//        gpsUtils.stopUsingGPS()
//    }

//    fun checkLocation() {
//        System.out.println(location)
//        if (location == null) {
//            Toast.makeText(context, "Nie ma jeszcze lokalizacji", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(context, "Jest juz lokalizacja", Toast.LENGTH_SHORT).show()
//        }
//
//    }
//
//    fun getLocation() {
//        location = gpsUtils.getLocation()
//    }


}