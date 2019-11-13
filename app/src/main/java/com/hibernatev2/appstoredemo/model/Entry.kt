package com.hibernatev2.appstoredemo.model

import com.google.gson.annotations.SerializedName

class Entry {
    @SerializedName("id")
    val id: EntryID? = null

    @SerializedName("im:name")
    val name: EntryName? = null

    @SerializedName("summary")
    val summary: EntrySummary? = null

    @SerializedName("im:artist")
    val artist: EntryArtist? = null

    @SerializedName("category")
    val category: EntryCategory? = null
}
