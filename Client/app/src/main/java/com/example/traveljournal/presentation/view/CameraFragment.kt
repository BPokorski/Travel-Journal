package com.example.traveljournal.presentation.view

import com.example.traveljournal.utils.GpsUtils
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.traveljournal.R
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.presentation.viewModel.CameraViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.fotoapparat.Fotoapparat
import io.fotoapparat.result.PhotoResult
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraView
import java.io.File

class CameraFragment:Fragment() {
    private lateinit var fragmentContext: Context
    private lateinit var cameraView: CameraView
    private lateinit var fabCamera: FloatingActionButton
    private lateinit var result: ImageView
    private lateinit var fabSave:FloatingActionButton
    private lateinit var fabDelete:FloatingActionButton
    private lateinit var fabBack:FloatingActionButton
    private lateinit var sessionManager: SessionManager
    private val cameraViewModel:CameraViewModel by viewModels()
    private lateinit var gpsUtils:GpsUtils
    private var photoResult:PhotoResult? = null
    var fotoapparat: Fotoapparat? = null
    var bitmap: Bitmap? = null
    val filename = "test.jpg"
    var location:Location? = null
    private lateinit var savingText: TextView
    private lateinit var animationDrawable: AnimationDrawable
    private lateinit var progressBar:ImageView
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.camera_fragment, container, false)

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
        cameraView = view.findViewById(R.id.camera_view)

        fabCamera = view.findViewById(R.id.fab_camera)
        fabDelete = view.findViewById(R.id.fab_delete)
        fabSave = view.findViewById(R.id.fab_save)
        fabBack = view.findViewById(R.id.fab_back)
        gpsUtils = GpsUtils()
        gpsUtils.init(fragmentContext)

        result = view.findViewById(R.id.result)
        savingText = view.findViewById(R.id.saving_text)

        cameraViewModel.init(fragmentContext)
        createFotoapparat()

        fabCamera.setOnClickListener {
            location = gpsUtils.getLocation()
            if (location == null && gpsUtils.isLocationEnabled) {
                Toast.makeText(fragmentContext, getString(R.string.location_progress), Toast.LENGTH_SHORT).show()
            } else { takePhoto()
            }
        }
        fabBack.setOnClickListener {
            goBack()
        }
        fabDelete.setOnClickListener {
            delete()
        }
        fabSave.setOnClickListener {
            save()
        }
    }

    private fun createFotoapparat() {
        fotoapparat = Fotoapparat(
            context = fragmentContext,
            view = cameraView,
            lensPosition = back(),
        )
    }

    private fun takePhoto() {
        photoResult = fotoapparat?.takePicture()
        photoResult?.toBitmap()?.whenAvailable { bitmapPhoto ->

            result.visibility = View.VISIBLE
            result.setImageBitmap(bitmapPhoto?.bitmap)

            result.rotation = (-bitmapPhoto?.rotationDegrees!!).toFloat()

            fabCamera.visibility = View.INVISIBLE
            fabBack.visibility = View.INVISIBLE

            fabCamera.isClickable = false
            fabBack.isClickable = false

            fabDelete.visibility = View.VISIBLE
            fabSave.visibility = View.VISIBLE
            fabDelete.isClickable = true
        }
    }

    override fun onStart() {
        super.onStart()
        fotoapparat?.start()
        if (hasNoPermissions()) {
            requestPermission()
        } else {
            fotoapparat?.start()
        }
    }

    override fun onStop() {
        super.onStop()
        fotoapparat?.stop()
    }

    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(fragmentContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragmentContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragmentContext,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragmentContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragmentContext,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), permissions,0)
    }

    private fun goBack() {
        Navigator.navigateToQuote(activity)
    }

    private fun delete() {
        photoResult = null

        result.visibility = View.INVISIBLE
        fabDelete.visibility = View.INVISIBLE
        fabSave.visibility = View.INVISIBLE

        fabDelete.isClickable = false

        fabBack.visibility = View.VISIBLE
        fabBack.isClickable = true


        fabCamera.visibility = View.VISIBLE
        fabCamera.isClickable = true
    }

    private fun save() {
        var login = sessionManager.fetchLogin()
        var path = fragmentContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath
        var file = File(path, "tempImg.jpg")
        photoResult?.saveToFile(file)?.await()

            cameraViewModel.addPhoto(login, file, location)?.observe(viewLifecycleOwner, Observer {
            when(it?.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(fragmentContext, "Photo added successfully", Toast.LENGTH_SHORT).show()
                    var photoData = it.data
                    cameraViewModel.saveAddressInfo(photoData)

                    gpsUtils.stopUsingGPS()

                    Navigator.navigateToPhoto(activity)
                }
                Resource.Status.LOADING -> {
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(fragmentContext, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        gpsUtils.onActivityResult(requestCode, resultCode, data)
    }
}
