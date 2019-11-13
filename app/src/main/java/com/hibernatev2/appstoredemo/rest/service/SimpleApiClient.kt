package com.hibernatev2.appstoredemo.rest.service

import com.hibernatev2.appstoredemo.rest.model.response.AppEntryResponse
import com.hibernatev2.appstoredemo.rest.model.response.AppItemResponse

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SimpleApiClient {

    @GET("hk/rss/topfreeapplications/limit=100/json")
    fun top100FreeEntry(): Observable<AppEntryResponse>

    @GET("hk/rss/topgrossingapplications/limit=10/json")
    fun top10RecommendationEntry(): Observable<AppEntryResponse>

    @GET("hk/lookup")
    fun lookupItem(
            @Query("id") id: String
    ): Call<AppItemResponse>
}
