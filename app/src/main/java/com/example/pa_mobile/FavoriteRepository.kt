package com.example.pa_mobile

import androidx.lifecycle.LiveData

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    val allFavorites: LiveData<List<Favorite>> = favoriteDao.getAllFavorites()

    suspend fun insert(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun deleteByKomikId(komikId: String) {
        favoriteDao.deleteByKomikId(komikId)
    }

}