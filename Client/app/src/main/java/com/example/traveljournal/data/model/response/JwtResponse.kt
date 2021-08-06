package com.example.traveljournal.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class JwtResponse {
    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("login")
    @Expose
    var login: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("roles")
    @Expose
    var roles: List<String>? = null
}
