package com.example.sweetbites.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY title") fun observeAll(): Flow<List<Recipe>>
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title") fun observeFavorites(): Flow<List<Recipe>>
    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1") suspend fun getById(id: Int): Recipe?
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(recipes: List<Recipe>)
    @Query("UPDATE recipes SET isFavorite = :favorite WHERE id = :id") suspend fun setFavorite(id: Int, favorite: Boolean)
    @Query("UPDATE recipes SET lastViewed = :time WHERE id = :id") suspend fun markViewed(id: Int, time: Long)
    @Query("SELECT * FROM recipes WHERE lastViewed > 0 ORDER BY lastViewed DESC LIMIT 12") fun observeRecent(): Flow<List<Recipe>>
}
