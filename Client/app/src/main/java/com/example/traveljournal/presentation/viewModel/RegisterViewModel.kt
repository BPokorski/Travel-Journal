package com.example.traveljournal.presentation.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.data.model.response.JwtResponse
import com.example.traveljournal.data.model.response.MessageResponse
import com.example.traveljournal.data.repository.Repository
import com.example.traveljournal.data.repository.Resource

class RegisterViewModel:ViewModel() {
    private var messageResponse: LiveData<Resource<MessageResponse?>?>? = null
    private lateinit var repository:Repository
    fun init(context: Context) {

        repository = Repository(context)


    }
    fun signUp(login:String, email:String, password: String): LiveData<Resource<MessageResponse?>?>? {

        messageResponse  = repository.signUp(login,email, password)
        return messageResponse

    }
}