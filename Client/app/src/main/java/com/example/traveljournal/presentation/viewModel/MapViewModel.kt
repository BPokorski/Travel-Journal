package com.example.traveljournal.presentation.viewModel

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.CountryResponse
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.utils.PhotoDataUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle

class MapViewModel:ViewModel() {
    private var countryPhotoDataData: MutableLiveData<Resource<List<PhotoDataResponse>?>>? = null
    private var oceanPhotoDataData: MutableLiveData<Resource<List<PhotoDataResponse>?>>? = null
    private var countries: MutableLiveData<Resource<List<CountryResponse?>?>>? = null
    private lateinit var repository:Repository
    private lateinit var photoDataUtils: PhotoDataUtils
    var markerPhotoMap = hashMapOf<Marker, PhotoDataResponse>()
    var mMap: GoogleMap? = null
    var isCountryPhotoVisible:Boolean = false
    var isOceanPhotoVisible:Boolean = false

    fun init(context: Context) {
        repository = Repository(context)
        photoDataUtils = PhotoDataUtils(context)
    }

    fun getCountries(login: String): MutableLiveData<Resource<List<CountryResponse?>?>>? {
        if (countries != null) {
            return countries
        } else {
            countries = repository.getCountries(login)
        }
        return countries
    }

    fun getCountryPhotoData(login: String, country:String): MutableLiveData<Resource<List<PhotoDataResponse>?>>? {
        countryPhotoDataData = repository.getCountryPhotoData(login, country)
        return countryPhotoDataData
    }

    fun getOceanPhotoData(login: String): MutableLiveData<Resource<List<PhotoDataResponse>?>>? {
        if (oceanPhotoDataData != null) {
            return oceanPhotoDataData
        } else {
            oceanPhotoDataData = repository.getOceanPhotoData(login)
        }
        return oceanPhotoDataData
    }

    fun showCountries(layer: GeoJsonLayer?, countriesNameList:List<String?>?) {
            var availablePolygonsStyle = GeoJsonPolygonStyle()
            availablePolygonsStyle.isClickable = true
            availablePolygonsStyle.isVisible = true
            availablePolygonsStyle.strokeColor = Color.BLACK

            layer?.addLayerToMap()
            for (feature: GeoJsonFeature in layer?.features!!) {

                if (countriesNameList?.contains(feature.getProperty("NAME"))!!) {

                    feature.polygonStyle = availablePolygonsStyle
                }
            }
            isCountryPhotoVisible = true
    }

    fun hideCountries(layer:GeoJsonLayer?, markerCollection: MarkerManager.Collection?) {
        markerCollection?.clear()
        var availablePolygonsStyle = GeoJsonPolygonStyle()
        availablePolygonsStyle.isClickable = false
        availablePolygonsStyle.isVisible = false

       layer?.removeLayerFromMap()
        isCountryPhotoVisible = false
    }

    fun showMarkers(listOfData:List<PhotoDataResponse>?, markerCollection: MarkerManager.Collection, icon: BitmapDescriptor?) {
        for(photoData:PhotoDataResponse in listOfData!!) {
            var position = photoData.latitude?.let { photoData.longitude?.let { it1 -> LatLng(it, it1) } }

            var marker = markerCollection.addMarker(position?.let {
                MarkerOptions()
                        .icon(icon)
                        .title(photoData.date)
                        .position(it)
                        .visible(true)
                        .draggable(false)
            })
            markerPhotoMap[marker] = photoData
        }
    }

    fun saveAddressInfo(photoData: PhotoDataResponse?) {
        photoDataUtils.saveAddressInfo(photoData)
    }
}