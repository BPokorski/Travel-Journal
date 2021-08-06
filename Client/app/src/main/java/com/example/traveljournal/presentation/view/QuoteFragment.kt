package com.example.traveljournal.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.traveljournal.R
import com.example.traveljournal.data.SessionManager

class QuoteFragment: Fragment() {
//    private lateinit var settingsButton: Button;
//    private lateinit var mapButton: Button;
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.quote_fragment, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

    }
}