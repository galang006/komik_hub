// FavoriteViewModel.kt
package com.example.pa_mobile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FavoriteRepository
    val allFavorites: LiveData<List<Favorite>>

    init {
        val favoriteDao = FavoriteDatabase.getDatabase(application).favoriteDao()
        repository = FavoriteRepository(favoriteDao)
        allFavorites = repository.allFavorites
    }

    fun insert(favorite: Favorite) = viewModelScope.launch {
        repository.insert(favorite)
    }

    fun deleteByKomikId(komikId: String) = viewModelScope.launch {
        repository.deleteByKomikId(komikId)
    }
}
