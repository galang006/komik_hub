package com.example.pa_mobile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite ORDER BY title ASC")
    fun getAllFavorites(): LiveData<List<Favorite>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)
    @Query("DELETE FROM favorite WHERE id = :komikId")
    suspend fun deleteByKomikId(komikId: String)
}