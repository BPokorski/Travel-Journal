package com.example.traveljournal.presentation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.SessionManager
import com.example.traveljournal.data.model.response.JwtResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource

public class LoginViewModel:ViewModel() {

    private var loginData:LiveData<Resource<JwtResponse?>?>? = null
    private lateinit var repository:Repository
     fun init(context: Context) {
         repository = Repository(context)
    }
    fun signIn(login:String, password: String): LiveData<Resource<JwtResponse?>?>? {
            loginData = repository.signIn(login, password)

        return loginData

    }

    
}