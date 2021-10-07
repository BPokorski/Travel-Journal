package com.example.traveljournal.presentation.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.traveljournal.R
import com.example.traveljournal.data.GlideApp
import com.example.traveljournal.data.SessionManager

class PhotoFragment: Fragment() {
    private lateinit var mapButton: Button
    private lateinit var photoButton: Button
    private lateinit var galleryButton: Button
    private lateinit var fragmentContext: Context
    private lateinit var sessionManager: SessionManager
    private lateinit var photoFrame:ImageView
    private lateinit var photo:ImageView
    private lateinit var fullScreen:ImageView

    private lateinit var dateCardView:CardView
    private lateinit var dateTextView: TextView

    private lateinit var placeCardView:CardView
    private lateinit var placeInfo:RelativeLayout
    private lateinit var countryTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var subAdminAreaTextView: TextView

    private lateinit var descriptionPageButton: Button

    private var isImageClicked:Boolean = false

    private var rotate:Float = 0F
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.photo_fragment, container, false)

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
        galleryButton = view.findViewById(R.id.gallery_button)
        photoButton = view.findViewById(R.id.photo_button)

        dateCardView = view.findViewById(R.id.date_card)
        dateTextView = view.findViewById(R.id.date_text)

        placeCardView = view.findViewById(R.id.place_card)
        placeInfo = view.findViewById(R.id.place_info)
        countryTextView = view.findViewById(R.id.country_text)
        subAdminAreaTextView = view.findViewById(R.id.sub_admin_area_text)
        addressTextView = view.findViewById(R.id.address_text)

        photo = view.findViewById(R.id.photo)
        photoFrame = view.findViewById(R.id.frame)
        fullScreen = view.findViewById(R.id.full_screen)

        descriptionPageButton = view.findViewById(R.id.description_page_button)
        mapButton.scaleX = (-1).toFloat()
        galleryButton.scaleX = (-1).toFloat()
        photoButton.scaleX = (-1).toFloat()
        var photoDataResponse = sessionManager.fetchPhotoData()
        rotate =  photoDataResponse?.rotateAngle?.toFloat()!!
        photo.rotation = rotate
        photoFrame.rotation = photoDataResponse?.rotateAngle?.toFloat()!!

        var placeName = sessionManager.fetchCountryName()
        var subAdmin = sessionManager.fetchSubAdmin()
        var placeAddress = sessionManager.fetchAddress()

        val serverName = getString(R.string.server_name)
        var photoUrl = serverName + sessionManager.fetchLogin()  + "/photo/"  + photoDataResponse?.photoId
        var glideUrl:GlideUrl = GlideUrl(photoUrl, LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer " + sessionManager.fetchAuthToken())
                .build())

        var circularProgressDrawable = CircularProgressDrawable(fragmentContext)
        circularProgressDrawable.backgroundColor = Color.TRANSPARENT
        circularProgressDrawable.setStartEndTrim(0.toFloat(), 1.toFloat())
        circularProgressDrawable.setStyle(CircularProgressDrawable.LARGE)
        circularProgressDrawable.setColorSchemeColors(Color.WHITE)

        circularProgressDrawable.start()
        GlideApp.with(fragmentContext)
                .load(glideUrl)
                .timeout(10000)
                .placeholder(circularProgressDrawable)
                .transform(RoundedCorners(16))
                .into(photo)

        GlideApp.with(fragmentContext)
                .load(glideUrl)
                .timeout(10000)
                .into(fullScreen)

        countryTextView.text = placeName
        dateTextView.text = photoDataResponse?.date

        if (!placeAddress.equals(null)) {
            addressTextView.text = placeAddress
        }
        if (!placeAddress.equals(null)) {
            subAdminAreaTextView.text = subAdmin
        }
        photo.setOnClickListener {
            if (!isImageClicked) {
                fullScreen.visibility = View.VISIBLE
                fullScreen.isClickable = true

                mapButton.visibility = View.INVISIBLE
                galleryButton.visibility = View.INVISIBLE
                photoButton.visibility = View.INVISIBLE

                mapButton.isClickable = false
                galleryButton.isClickable = false
                photoButton.isClickable = false

                isImageClicked = true
            }
        }

        fullScreen.setOnClickListener {
            if (isImageClicked) {
                fullScreen.visibility = View.INVISIBLE
                fullScreen.isClickable = false

                mapButton.visibility = View.VISIBLE
                galleryButton.visibility = View.VISIBLE
                photoButton.visibility = View.VISIBLE

                mapButton.isClickable = true
                galleryButton.isClickable = true
                photoButton.isClickable = true

                isImageClicked = false
            }
        }

        dateCardView.setOnClickListener {
            if (dateTextView.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(dateCardView, AutoTransition())
                dateTextView.visibility = View.GONE
            } else {
                TransitionManager.beginDelayedTransition(dateCardView, AutoTransition())
                dateTextView.visibility = View.VISIBLE
            }
        }

        placeCardView.setOnClickListener {
            if (placeInfo.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(placeCardView, AutoTransition())
                placeInfo.visibility = View.GONE
            } else {
                TransitionManager.beginDelayedTransition(placeCardView, AutoTransition())
                placeInfo.visibility = View.VISIBLE
            }
        }

        descriptionPageButton.setOnClickListener {
            Navigator.navigateToDescription(activity)
        }
    }
}