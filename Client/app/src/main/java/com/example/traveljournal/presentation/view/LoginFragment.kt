package com.example.traveljournal.presentation.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.traveljournal.R
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.repository.Resource
import com.example.traveljournal.presentation.viewModel.LoginViewModel

class LoginFragment: Fragment() {
    private lateinit var editLogin: EditText;
    private lateinit var editPassword: EditText;
    private lateinit var signInButton: Button;
    private lateinit var signUpButton: Button;
    private lateinit var fragmentContext: Context;
    private lateinit var sessionManager: SessionManager
    private lateinit var loadingText: TextView
    private lateinit var animationDrawable: AnimationDrawable
    private lateinit var progressBar: ImageView
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
): View? = inflater.inflate(R.layout.login_fragment, container, false)
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

        progressBar = view.findViewById(R.id.login_loading_bar)!!
        progressBar.setBackgroundResource(R.drawable.loading_bar_animation)
        loadingText = view.findViewById(R.id.login_loading_text)!!

        animationDrawable = progressBar.background as AnimationDrawable
        loginViewModel.init(fragmentContext)
        editLogin = view.findViewById(R.id.sign_in_login_text)
        editPassword = view.findViewById(R.id.sign_in_password_text)
        signInButton = view.findViewById(R.id.sign_in_button)
        signUpButton = view.findViewById(R.id.sign_up_layout_button)

        signInButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE
            animationDrawable.start()
            var userName: String = editLogin.text.toString();
            var password: String = editPassword.text.toString();

            loginViewModel.signIn(userName, password)
                ?.observe(viewLifecycleOwner, Observer {
                    when (it?.status) {
                        Resource.Status.SUCCESS -> {
                            it.data?.token?.let { it1 -> sessionManager.saveAuthToken(it1) }
                            it.data?.login?.let { it1 -> sessionManager.saveLogin(it1) }

                            progressBar.visibility = View.INVISIBLE
                            loadingText.visibility = View.INVISIBLE
                            animationDrawable.stop()
                            Navigator.navigateToQuote(activity)
                        }
                        Resource.Status.LOADING -> {
                        }
                        Resource.Status.ERROR -> {
                            progressBar.visibility = View.INVISIBLE
                            loadingText.visibility = View.INVISIBLE
                            animationDrawable.stop()
                            Toast.makeText(fragmentContext, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
        signUpButton.setOnClickListener {
            Navigator.navigateToRegister(activity);
        }
    }
}

