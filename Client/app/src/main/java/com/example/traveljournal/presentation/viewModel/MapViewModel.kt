package com.example.traveljournal.presentation.viewModel

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.CountryResponse
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.utils.DescriptionUtils
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
    private var countryPhotoDescriptionData: MutableLiveData<Resource<List<PhotoDescriptionResponse>?>>? = null
    private var oceanPhotoDescriptionData: MutableLiveData<Resource<List<PhotoDescriptionResponse>?>>? = null
    private var countries: MutableLiveData<Resource<List<CountryResponse?>?>>? = null
    private lateinit var repository:Repository
    private lateinit var descriptionUtils: DescriptionUtils
    var markerPhotoMap = hashMapOf<Marker, PhotoDescriptionResponse>()
    var mMap: GoogleMap? = null
    var isCountryPhotoVisible:Boolean = false
    var isOceanPhotoVisible:Boolean = false
    fun init(context: Context) {

        repository = Repository(context)
        descriptionUtils = DescriptionUtils(context)

    }

    fun getCountries(login: String): MutableLiveData<Resource<List<CountryResponse?>?>>? {
        if (countries != null) {
            return countries
        } else {
            countries = repository.getCountries(login)
        }
        return countries
    }

    fun getCountryPhotoDescriptions(login: String, country:String): MutableLiveData<Resource<List<PhotoDescriptionResponse>?>>? {
//        var repository = Repository(context)

        countryPhotoDescriptionData = repository.getCountryPhotoDescriptions(login, country)
        return countryPhotoDescriptionData
    }

    fun getOceanPhotoDescriptions(login: String): MutableLiveData<Resource<List<PhotoDescriptionResponse>?>>? {

        if (oceanPhotoDescriptionData != null) {
            return oceanPhotoDescriptionData
        } else {
            oceanPhotoDescriptionData = repository.getOceanPhotoDescriptions(login)
        }

        return oceanPhotoDescriptionData
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
//        var polygonStyle =layer?.defaultPolygonStyle
//        polygonStyle = availablePolygonsStyle
//
////        var defStyle = layer?.defaultPolygonStyle
////        defStyle = availablePolygonsStyle
       layer?.removeLayerFromMap()
//        for (feature: GeoJsonFeature in layer?.features!!) {
//
//            feature.polygonStyle = availablePolygonsStyle
//        }
        isCountryPhotoVisible = false


    }

    fun showMarkers(listOfDescriptions:List<PhotoDescriptionResponse>?, markerCollection: MarkerManager.Collection, icon: BitmapDescriptor?) {
        for(photoDescription:PhotoDescriptionResponse in listOfDescriptions!!) {
//            sessionManager.savePhotoDescription(photoDescription)
            var position = photoDescription.latitude?.let { photoDescription.longitude?.let { it1 -> LatLng(it, it1) } }


            var marker = markerCollection.addMarker(position?.let {
                MarkerOptions()
                        .icon(icon)
                        .title(photoDescription.date)
                        .position(it)
                        .visible(true)
                        .draggable(false)
            })
            markerPhotoMap[marker] = photoDescription

        }
    }

    fun saveAddressInfo(photoDescription: PhotoDescriptionResponse?) {
        descriptionUtils.saveAddressInfo(photoDescription)
    }


}