package com.example.sweetbites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sweetbites.data.Recipe
import com.example.sweetbites.data.RecipeRepository
import com.example.sweetbites.data.SweetBitesDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(SweetBitesDatabase.get(application).recipeDao())
    val recipes = repository.recipes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val favorites = repository.favorites.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val recent = repository.recent.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun toggleFavorite(recipe: Recipe) = viewModelScope.launch { repository.toggleFavorite(recipe) }
    fun markViewed(id: Int) = viewModelScope.launch { repository.markViewed(id) }
}
