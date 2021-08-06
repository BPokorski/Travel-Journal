package com.example.traveljournal.presentation.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.traveljournal.R
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.presentation.viewModel.DescriptionViewModel

class DescriptionFragment:Fragment() {
    private lateinit var description: TextView
    private lateinit var photoButton: Button
    private lateinit var updateDescriptionButton: Button
    private lateinit var sessionManager: SessionManager
    private lateinit var fragmentContext:Context
    private lateinit var editDescription:EditText
    private val descriptionViewModel: DescriptionViewModel by viewModels()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.description_fragment, container, false)
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
        photoButton = view.findViewById(R.id.photo_page_button)
        updateDescriptionButton = view.findViewById(R.id.update_description_button)
        description = view.findViewById(R.id.description)
        editDescription = view.findViewById(R.id.edit_description)

        descriptionViewModel.init(fragmentContext)
        var photoDescription = sessionManager.fetchPhotoDescription()

        if (!photoDescription?.description.isNullOrEmpty()) {
            description.text = photoDescription?.description
        }



        description.setOnClickListener {
            description.visibility = View.INVISIBLE
            description.isClickable = false
            editDescription.visibility = View.VISIBLE
            updateDescriptionButton.visibility = View.VISIBLE

            updateDescriptionButton.isClickable = true
        }

        updateDescriptionButton.setOnClickListener {
            var updatedDescription = editDescription.text.toString()

            var login = sessionManager.fetchLogin()
            var photoId = photoDescription?.photoId

            descriptionViewModel.updateDescription(login, photoId, updatedDescription)?.observe(viewLifecycleOwner, Observer {
                when(it?.status) {
                    Resource.Status.SUCCESS -> {
                        it.data?.let { it1 -> sessionManager.savePhotoDescription(it1) }

                        description.text = it.data?.description
                        editDescription.isClickable = false
                        editDescription.visibility = View.GONE

                        updateDescriptionButton.isClickable = false
                        updateDescriptionButton.visibility = View.INVISIBLE

                        description.visibility = View.VISIBLE
                        description.isClickable = true
                    }
                    Resource.Status.LOADING -> {

                    }
                    Resource.Status.ERROR -> {

                        Toast.makeText(fragmentContext, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        photoButton.setOnClickListener {
            Navigator.navigateToPhoto(activity)
        }
    }

}