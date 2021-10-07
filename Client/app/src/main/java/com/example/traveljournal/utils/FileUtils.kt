package com.example.traveljournal.utils

//import android.media.ExifInterface
import android.location.Location
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FileUtils {

     fun fileToMultiPartConverter(file: File?): MultipartBody.Part {
        var requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file!!)
         return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }

    fun setGeoTag(file: File?, location: Location) {
        var exifInterface: ExifInterface? = file?.absolutePath?.let { ExifInterface(it) }
        exifInterface?.setLatLong(location.latitude, location.longitude)
        exifInterface?.saveAttributes()
    }
}