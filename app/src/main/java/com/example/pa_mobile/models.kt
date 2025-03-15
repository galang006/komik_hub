package com.example.pa_mobile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

object Constants {
    const val RAPID_API_KEY = ""
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
