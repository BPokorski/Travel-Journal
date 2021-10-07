package com.example.traveljournal.utils

import android.content.Context
import android.location.Geocoder
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.model.response.PhotoDataResponse

class PhotoDataUtils(private var context: Context) {

    private var sessionManager: SessionManager = SessionManager(context)

    fun saveAddressInfo(photoData: PhotoDataResponse?) {
        sessionManager.removeSingleItem("address")
        sessionManager.removeSingleItem("country_name")
        sessionManager.removeSingleItem("sub_admin_area")

        var addresses = photoData?.latitude?.let { photoData?.longitude?.let { it1 -> Geocoder(context).getFromLocation(it, it1, 1) } }
        var address = addresses?.get(0)

        if (photoData != null) {
            sessionManager.savePhotoData(photoData)
        }
        if (address?.thoroughfare != null) {
            sessionManager.savePlaceAddress(address.thoroughfare)
        }
        if (address?.countryName != null) {
            sessionManager.saveCountryName(address.countryName)
        }
        if (address?.subAdminArea != null) {
            sessionManager.saveSubAdminArea(address.subAdminArea)
        }
    }
}