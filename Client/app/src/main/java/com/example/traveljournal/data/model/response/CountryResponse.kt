package com.example.traveljournal.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryResponse {
    @SerializedName("continent")
    @Expose
    var continent:String? = null
    @SerializedName("name")
    @Expose
    var name:String? = null
}