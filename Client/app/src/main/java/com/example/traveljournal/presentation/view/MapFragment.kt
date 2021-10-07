package com.example.traveljournal.presentation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.traveljournal.R
import com.example.traveljournal.data.model.response.PhotoDataResponse
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.presentation.viewModel.MapViewModel
import com.example.traveljournal.utils.MapUtils
import com.example.traveljournal.utils.StringUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.Feature
import com.google.maps.android.data.geojson.*

class MapFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GeoJsonLayer.GeoJsonOnFeatureClickListener {
    private var collection:MarkerManager.Collection? = null
    var markerManager:MarkerManager? = null
    var groundOverlayManager:GroundOverlayManager? = null
    var polygonManager:PolygonManager? = null
    var polylineManager:PolylineManager? = null
    private lateinit var login: String
    private var layer:GeoJsonLayer? = null
    private var markerPhotoMap = hashMapOf<Marker, PhotoDataResponse>()
    private var stringUtils: StringUtils = StringUtils()
    private var mapUtils: MapUtils = MapUtils()
    private lateinit var mapButton: Button
    private lateinit var photoButton: Button
    private lateinit var countryButton: Button
    private lateinit var fragmentContext: Context
    private lateinit var sessionManager: SessionManager
    private lateinit var loadingText: TextView
    private lateinit var animationDrawable:AnimationDrawable
    private lateinit var progressBar:ImageView
    private lateinit var fabCountriesPhotoButton:ExtendedFloatingActionButton
    private lateinit var fabOceanPhotoButton:ExtendedFloatingActionButton
    private var isCountryPhotoVisible:Boolean = false
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.map_fragment, container, false)
        progressBar = rootView.findViewById(R.id.loading_bar)!!
        progressBar.setBackgroundResource(R.drawable.loading_bar_animation)

        loadingText = rootView.findViewById(R.id.loading_text)!!
        animationDrawable = progressBar.background as AnimationDrawable

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context;
        sessionManager = SessionManager(context);
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mapButton = view.findViewById(R.id.map_button)
        countryButton = view.findViewById(R.id.gallery_button)
        photoButton = view.findViewById(R.id.photo_button)
        fabCountriesPhotoButton = view.findViewById(R.id.fab_countries_photo_button)
        fabOceanPhotoButton = view.findViewById(R.id.fab_oceans_photo_button)

        mapViewModel.init(fragmentContext)
        mapButton.scaleX = (-1).toFloat()
        countryButton.scaleX = (-1).toFloat()
        photoButton.scaleX = (-1).toFloat()

        login = sessionManager.fetchLogin().toString()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fabCountriesPhotoButton.setOnClickListener {
            if (!mapViewModel.isCountryPhotoVisible) {
                fabCountriesPhotoButton.shrink()
                progressBar.visibility = View.VISIBLE
                loadingText.visibility = View.VISIBLE
                animationDrawable.start()

                mapViewModel.getCountries(login)?.observe(viewLifecycleOwner, Observer {
                    when(it?.status) {
                        Resource.Status.SUCCESS -> {

                            var countriesName = it.data?.map {countryResponse ->  countryResponse?.name}

                            mapViewModel.showCountries(layer, countriesName)

                            progressBar.visibility = View.INVISIBLE
                            loadingText.visibility = View.INVISIBLE
                            animationDrawable.stop()
                        }
                        Resource.Status.LOADING -> {
                        }
                        Resource.Status.ERROR -> {
                            progressBar.visibility = View.INVISIBLE
                            loadingText.visibility = View.INVISIBLE
                            animationDrawable.stop()
                            Toast.makeText(fragmentContext, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                fabCountriesPhotoButton.extend()
                mapViewModel.hideCountries(layer, collection)
            }
        }

        fabOceanPhotoButton.setOnClickListener {
            if(!mapViewModel.isOceanPhotoVisible) {
                fabOceanPhotoButton.shrink()
                progressBar.visibility = View.VISIBLE
                loadingText.visibility = View.VISIBLE
                animationDrawable.start()

                collection?.clear()

                mapViewModel.getOceanPhotoData(login)?.observe(viewLifecycleOwner, Observer {
                    when(it?.status) {
                        Resource.Status.SUCCESS -> {
                            mapViewModel.showMarkers(it.data, collection!!, vectorToBitmap(R.drawable.marker_icon, R.color.cover_text))
                            progressBar.visibility = View.INVISIBLE
                            loadingText.visibility = View.INVISIBLE
                            animationDrawable.stop()
                        }
                        Resource.Status.LOADING -> {
                        }
                        Resource.Status.ERROR -> {
                            progressBar.visibility = View.INVISIBLE
                            loadingText.visibility = View.INVISIBLE
                            animationDrawable.stop()
                            Toast.makeText(fragmentContext, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                mapViewModel.isOceanPhotoVisible = true
            } else {
                collection?.clear()
                fabOceanPhotoButton.extend()
                mapViewModel.isOceanPhotoVisible = false
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap){
        mapViewModel.mMap = googleMap

        mapViewModel.mMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))

        markerManager = MarkerManager(mapViewModel.mMap)
        groundOverlayManager = GroundOverlayManager(mapViewModel.mMap!!)
        polygonManager = PolygonManager(mapViewModel.mMap)
        polylineManager = PolylineManager(mapViewModel.mMap!!)

        collection = markerManager!!.newCollection()

        mapViewModel.mMap!!.uiSettings.isMapToolbarEnabled = false

        layer = loadGeojson(mapViewModel.mMap!!, R.raw.countries, markerManager!!, groundOverlayManager!!, polygonManager, polylineManager)

        fabCountriesPhotoButton.isClickable = true
        fabCountriesPhotoButton.visibility = View.VISIBLE

        fabOceanPhotoButton.visibility = View.VISIBLE
        mapViewModel.mMap!!.setMaxZoomPreference(5.toFloat())

        layer?.setOnFeatureClickListener(this)

        collection?.setOnMarkerClickListener(this)
    }

    private fun loadGeojson(googleMap: GoogleMap,
                            geojsonId: Int,
                            markerManager: MarkerManager,
                            groundOverlayManager: GroundOverlayManager,
                            polylineManager: PolygonManager?,
                            polygonManager: PolylineManager?): GeoJsonLayer {

        var newLayer = GeoJsonLayer(googleMap, geojsonId, context, markerManager, polylineManager, polygonManager, groundOverlayManager)

        var polygonStyle = newLayer.defaultPolygonStyle
        polygonStyle.isClickable = false
        polygonStyle.isVisible = false

        return newLayer
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorRes colorId:Int): BitmapDescriptor? {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        vectorDrawable?.setTint(ResourcesCompat.getColor(resources, colorId, null))
        val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        var photoData = mapViewModel.markerPhotoMap[marker]
        mapViewModel.saveAddressInfo(photoData)
        Navigator.navigateToPhoto(activity)
        return false
    }

    override fun onFeatureClick(feature: Feature?) {
        progressBar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
        animationDrawable.start()
        collection?.clear()
        mapViewModel.getCountryPhotoData(login, feature?.getProperty("NAME")!!)?.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Resource.Status.SUCCESS -> {
                    mapViewModel.showMarkers(it.data, collection!!, vectorToBitmap(R.drawable.marker_icon, R.color.dark_brown))
                    progressBar.visibility = View.INVISIBLE
                    loadingText.visibility = View.INVISIBLE
                    animationDrawable.stop()
                }
                Resource.Status.LOADING -> {
                }
                Resource.Status.ERROR -> {

                    Toast.makeText(fragmentContext, "Internet issues. Try again", Toast.LENGTH_SHORT).show()
                }
            }
        })
        mapViewModel.mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(mapUtils.getLatLngBoundingBox(feature as GeoJsonFeature), 0))
    }
}