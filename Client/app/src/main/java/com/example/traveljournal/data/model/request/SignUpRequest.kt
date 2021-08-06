package com.example.traveljournal.data.model.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignUpRequest {
    @SerializedName("login")
    @Expose
    var login: String? = null

    @SerializedName("email")
    @Expose
    var email:String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null
}