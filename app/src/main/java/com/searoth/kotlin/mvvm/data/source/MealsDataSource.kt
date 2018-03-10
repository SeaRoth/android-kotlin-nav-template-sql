package com.searoth.kotlin.mvvm.data.source

import com.searoth.kotlin.mvvm.data.Meal

/**
 * Created by cr on 2/26/2018.
 */
interface MealsDataSource{
    interface LoadMealsCallback{
        fun onMealsLoaded(meals: List<Meal>)

        fun onDataNotAvailable()
    }

    interface GetMealCallback {
        fun onMealLoaded(meal: Meal)

        fun onDataNotAvailable()
    }

    fun getMeals(callback: LoadMealsCallback)

    fun getMeal(mealId: String, callback: GetMealCallback)

    fun saveMeal(meal: Meal)

    fun favoriteMeal(meal: Meal)

    fun favoriteMeal(mealId: String)

    fun viewMeal(meal: Meal)

    fun viewMeal(mealId: String)

    fun clearFavoriteMeals()

    fun refreshMeals()

    fun deleteAllMeals()

    fun deleteMeal(mealId: String)
}