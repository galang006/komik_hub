package com.example.pa_mobile

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite")
data class Favorite(
        @PrimaryKey val id: String,
        @ColumnInfo(name = "title") val title: String?,
        @ColumnInfo(name = "total_chapter") val totalChapter: Int,
        @ColumnInfo(name = "thumb_link") val thumbLink: String?
)