package com.example.traveljournal.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhotoDescriptionResponse {
    @SerializedName("id")
    @Expose
    var id:String? = null


    @SerializedName("photoId")
    @Expose
    var photoId:String? = null
    @SerializedName("description")
    @Expose
    var description:String? = null
    @SerializedName("date")
    @Expose
    var date:String? = null
    @SerializedName("country")
    @Expose
    var country:String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
    @SerializedName("rotateAngle")
    @Expose
    var rotateAngle: Int? = null
}