package com.hibernatev2.appstoredemo.rest.model.response

import com.google.gson.annotations.SerializedName
import com.hibernatev2.appstoredemo.model.Entry
import java.util.*

class AppEntryResponse : BaseResponse() {

    @SerializedName("feed")
    lateinit var data: Feed

    inner class Feed {
        @SerializedName("entry")
        lateinit var entries: ArrayList<Entry>
    }
}
