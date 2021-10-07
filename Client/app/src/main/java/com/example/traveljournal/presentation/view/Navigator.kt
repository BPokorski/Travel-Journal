package com.example.traveljournal.presentation.view

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.traveljournal.R

class Navigator {
    companion object {
        fun navigateToLogin(
                activity: FragmentActivity?
        ) {
            replaceFragment(
                    LoginFragment(),
                    activity
            )
        }

        fun navigateToQuote(
                activity: FragmentActivity?
        ) {
            replaceFragment(
                    QuoteFragment(),
                    activity
            )
        }
        fun navigateToRegister(
                activity: FragmentActivity?
        ) {
            replaceFragment(
                    RegisterFragment(),
                    activity
            )
        }
        fun navigateToMap(
            activity: FragmentActivity?
        ){
            replaceFragment(
                MapFragment(),
                activity
            )
        }
        fun navigateToPhoto(
                activity: FragmentActivity?
        ){
            replaceFragment(
                    PhotoFragment(),
                    activity
            )
        }

        fun navigateToCamera(
            activity: FragmentActivity?
        ){
            replaceFragment(
                CameraFragment(),
                activity
            )
        }
        fun navigateToGallery(
                activity: FragmentActivity?
        ){
            replaceFragment(
                    GalleryFragment(),
                    activity
            )
        }

        fun navigateToDescription(
                activity: FragmentActivity?
        ) {
            replaceFragment(
                    DescriptionFragment(),
                    activity
            )
        }
        private fun replaceFragment(
                fragment: Fragment,
                activity: FragmentActivity?

        ) {
            activity?.supportFragmentManager?.beginTransaction()?.run {
                replace(R.id.root_container, fragment)
                commit()
            }
        }


    }

}