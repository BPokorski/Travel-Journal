package com.example.traveljournal.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.data.model.response.PhotoDescriptionResponse
import com.example.traveljournal.presentation.view.recyclerView.CardClickListener
import com.example.traveljournal.presentation.viewModel.GalleryViewModel
import com.example.traveljournal.presentation.view.recyclerView.CustomItemSpaceDecoration
import com.example.traveljournal.presentation.view.recyclerView.PhotoDescriptionAdapter

class GalleryFragment:Fragment(), CardClickListener {
    private lateinit var recyclerView:RecyclerView
    private lateinit var fragmentContext: Context
    private val galleryViewModel:GalleryViewModel by viewModels()

    private lateinit var photoDescriptionAdapter: PhotoDescriptionAdapter
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.gallery_layout, container, false)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context;
    }
    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?

    ) {
        super.onViewCreated(view, savedInstanceState)

        galleryViewModel.init(fragmentContext)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(fragmentContext, 1, RecyclerView.VERTICAL, false)
        photoDescriptionAdapter = PhotoDescriptionAdapter(fragmentContext)

        galleryViewModel.photoDescriptionPagedList.observe(viewLifecycleOwner, Observer {
            photoDescriptionAdapter.submitList(it)
        })
        recyclerView.addItemDecoration(CustomItemSpaceDecoration(50))
        recyclerView.adapter = photoDescriptionAdapter
        photoDescriptionAdapter.setOnCardClickListener(this)
    }

    override fun onCardClickListener(photoDescription: PhotoDescriptionResponse?, position: Int) {
        Toast.makeText(fragmentContext, "Photo choosen", Toast.LENGTH_SHORT).show()
        galleryViewModel.saveAddressInfo(photoDescription)
        Navigator.navigateToPhoto(activity)
    }


}