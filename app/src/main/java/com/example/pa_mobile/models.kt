package com.example.pa_mobile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

object Constants {
    const val RAPID_API_KEY = "efd6a1bd64msh66b24a4e4d70b9ep144bcfjsn28ebca2cd5bd"
    //'X-RapidAPI-Key': '54783c9b68mshcba9b7aef779a18p17a6a7jsn115a58dc1f34',
    //'X-RapidAPI-Key': '84cb9e3cc0mshbb407e0ce7ac26cp10601ejsn7427c13b53a8',
    //'X-RapidAPI-Key': 'b53bb91f8amsh6407729f528c092p1e9773jsne59c6c7be581',
    //'X-RapidAPI-Key': '48cce3ae37msh430cd677c655b34p1c8193jsnf204ece7a8f6',
    //'X-RapidAPI-Key': 'fde55c18c6mshb86b944fd98ac2dp10a830jsne1b73a94e42c',
    //'X-RapidAPI-Key': 'efd6a1bd64msh66b24a4e4d70b9ep144bcfjsn28ebca2cd5bd',
    //'X-RapidAPI-Key': '2581ce78fcmsh88b5380efc84077p170246jsn44368fc3b694'
}

data class KomikResponse(
    val code: Int,
    val data: List<Komik>
)

data class DetailResponse(
    val code: Int,
    val data: Komik
)

data class Komik(
    val id: String,
    val title: String,
    @SerializedName("sub_title")
    val subTitle: String,
    val status: String,
    val thumb: String,
    val summary: String,
    val authors: List<String>,
    val genres: List<String>,
    val nsfw: Boolean,
    val type: String,
    @SerializedName("total_chapter")
    val totalChapter: Int,
    @SerializedName("create_at")
    val createAt: Long,
    @SerializedName("update_at")
    val updateAt: Long
)


data class ChapterResponse(
    val code: Int,
    val data: List<Chapter>
)

data class Chapter(
    val id: String,
    val manga: String,
    val title: String,
    val create_at: Long,
    val update_at: Long
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(manga)
        parcel.writeString(title)
        parcel.writeLong(create_at)
        parcel.writeLong(update_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chapter> {
        override fun createFromParcel(parcel: Parcel): Chapter {
            return Chapter(parcel)
        }

        override fun newArray(size: Int): Array<Chapter?> {
            return arrayOfNulls(size)
        }
    }
}

data class ChapterFetchResponse(
    val code: Int,
    val data: List<ChapterFetch>
)

data class ChapterFetch(
    val id: String,
    val chapter: String,
    val manga: String,
    val index: Int,
    val link: String
)
