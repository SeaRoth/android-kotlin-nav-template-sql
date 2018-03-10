package com.searoth.kotlin.mvvm

import android.content.Context
import com.searoth.kotlin.mvvm.data.FakeMealsRemoteDataSource
import com.searoth.kotlin.mvvm.data.source.MealsRepository
import com.searoth.kotlin.mvvm.data.source.local.MealDatabase
import com.searoth.kotlin.mvvm.data.source.local.MealsLocalDataSource
import com.searoth.kotlin.mvvm.util.AppExecutors

/**
 * Created by cr on 2/26/2018.
 */
/**
 * Enables injection of mock implementations for
 * [MealsDataSource] at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {

    fun provideMealsRepository(context: Context): MealsRepository {
        val database = MealDatabase.getInstance(context)
        return MealsRepository.getInstance(FakeMealsRemoteDataSource,
                MealsLocalDataSource.getInstance(AppExecutors(), database.mealDao()))
    }
}
