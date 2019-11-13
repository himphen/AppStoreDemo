package com.hibernatev2.appstoredemo.model

import com.google.gson.annotations.SerializedName

class AppItem {
    @SerializedName("trackId")
    val id: String? = null

    @SerializedName("artworkUrl512")
    val iconUrl: String? = null

    @SerializedName("trackName")
    val title: String? = null

    @SerializedName("genres")
    val category: List<String>? = null

    @SerializedName("averageUserRating")
    val rating: Float? = null

    @SerializedName("userRatingCount")
    val ratingCount: String? = null

}
