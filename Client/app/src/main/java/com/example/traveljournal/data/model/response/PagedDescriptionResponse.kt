package com.example.traveljournal.data.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PagedDescriptionResponse {
    @SerializedName("totalItems")
    @Expose
    var totalItems:Long? = null
    @SerializedName("totalPages")
    @Expose
    var totalPages:Int? = null
    @SerializedName("currentPage")
    @Expose
    var currentPage:Int? = null
    @SerializedName("descriptions")
    @Expose
    var descriptions:List<PhotoDescriptionResponse>? = null
}