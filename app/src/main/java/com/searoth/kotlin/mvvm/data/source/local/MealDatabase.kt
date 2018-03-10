package com.searoth.kotlin.mvvm.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.searoth.kotlin.mvvm.data.Meal

/**
 * The Room Database that contains the Meal table.
 */
@Database(entities = [(Meal::class)], version = 1)
abstract class MealDatabase : RoomDatabase() {

    abstract fun mealDao(): MealsDao

    companion object {

        private var INSTANCE: MealDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): MealDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            MealDatabase::class.java, "Meals.db")
                            .build()
                }
                return INSTANCE!!
            }
        }
    }

}