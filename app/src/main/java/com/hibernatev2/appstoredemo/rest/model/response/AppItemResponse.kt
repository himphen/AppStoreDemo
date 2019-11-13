package com.hibernatev2.appstoredemo.rest.model.response

import com.google.gson.annotations.SerializedName
import com.hibernatev2.appstoredemo.model.AppItem

class AppItemResponse : BaseResponse() {
    @SerializedName("results")
    var results: List<AppItem>? = null
}
