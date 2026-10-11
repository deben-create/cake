package com.example.sweetbites.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class SweetBitesDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile private var INSTANCE: SweetBitesDatabase? = null
        fun get(context: Context): SweetBitesDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, SweetBitesDatabase::class.java, "sweet_bites.db")
                .fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
