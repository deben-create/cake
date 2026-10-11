package com.example.sweetbites.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val prepMinutes: Int,
    val bakeMinutes: Int,
    val difficulty: String,
    val servings: Int,
    val emoji: String,
    val colorHex: String,
    val ingredients: String,
    val steps: String,
    val tip: String,
    val isFavorite: Boolean = false,
    val lastViewed: Long = 0L
) {
    fun ingredientLines(): List<String> = ingredients.split("|").filter { it.isNotBlank() }
    fun stepLines(): List<String> = steps.split("|").filter { it.isNotBlank() }
}
