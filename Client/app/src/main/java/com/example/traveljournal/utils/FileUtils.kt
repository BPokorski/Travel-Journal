package com.example.traveljournal.utils

//import android.media.ExifInterface
import android.graphics.Bitmap
import android.location.Location
import android.net.wifi.WifiManager
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileUtils {

     fun fileToMultiPartConverter(file: File?): MultipartBody.Part {
        var requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file!!)
        var fileBody: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)
        return fileBody
    }

    fun setGeoTag(file: File?, location: Location) {
        var exifInterface: ExifInterface? = file?.absolutePath?.let { ExifInterface(it) }

        exifInterface?.setLatLong(location.latitude, location.longitude)

        exifInterface?.saveAttributes()
    }


}