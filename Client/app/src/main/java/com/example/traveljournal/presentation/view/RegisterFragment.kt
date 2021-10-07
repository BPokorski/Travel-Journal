package com.example.traveljournal.presentation.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.traveljournal.R
import com.example.traveljournal.data.retrofit.ServiceGenerator
import com.example.traveljournal.data.model.request.SignUpRequest
import com.example.traveljournal.data.model.response.MessageResponse
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.data.retrofit.TravelJournalService
import com.example.traveljournal.presentation.viewModel.RegisterViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment: Fragment() {
    private lateinit var editLogin: EditText;
    private lateinit var editEmail: EditText;
    private lateinit var editPassword: EditText;
    private lateinit var signUpButton: Button;
    private lateinit var signInButton: Button
    private lateinit var fragmentContext:Context;
    private val registerViewModel:RegisterViewModel by viewModels()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.register_fragment, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context;
    }
    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        registerViewModel.init(fragmentContext)
        editLogin = view.findViewById(R.id.sign_up_login_text)
        editEmail = view.findViewById(R.id.sign_up_email_text)
        editPassword = view.findViewById(R.id.sign_up_password_text)

        signUpButton = view.findViewById(R.id.sign_up_button)
        signInButton = view.findViewById(R.id.sign_in_layout_button)

        signUpButton.setOnClickListener {

            var login: String = editLogin.text.toString();
            var email: String = editEmail.text.toString();
            var password: String = editPassword.text.toString();

           registerViewModel.signUp(login, email, password)?.observe(viewLifecycleOwner, Observer {
               when(it?.status) {
                   Resource.Status.SUCCESS -> {
                       Toast.makeText(fragmentContext, "Signed up successfully", Toast.LENGTH_LONG).show()
                       Navigator.navigateToLogin(activity)
                   }
                   Resource.Status.LOADING -> {
                   }
                   Resource.Status.ERROR -> {
                       Toast.makeText(fragmentContext, it.message, Toast.LENGTH_SHORT).show()
                   }
               }
           })
        }
        signInButton.setOnClickListener {
            Navigator.navigateToLogin(activity)
        }
    }

}