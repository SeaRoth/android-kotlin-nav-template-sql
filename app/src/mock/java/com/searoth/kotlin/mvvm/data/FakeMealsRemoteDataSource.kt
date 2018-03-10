package com.searoth.kotlin.mvvm.data

import android.support.annotation.VisibleForTesting
import com.google.common.collect.Lists
import com.searoth.kotlin.mvvm.data.source.MealsDataSource
import java.util.LinkedHashMap

/**
 * Created by cr on 2/26/2018.
 */
/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
object FakeMealsRemoteDataSource : MealsDataSource {

    override fun getMeals(callback: MealsDataSource.LoadMealsCallback) {
        callback.onMealsLoaded(Lists.newArrayList(MEALS_SERVICE_DATA.values))
    }

    override fun getMeal(mealId: String, callback: MealsDataSource.GetMealCallback) {
        val meal = MEALS_SERVICE_DATA[mealId]
        meal?.let { callback.onMealLoaded(it) }
    }

    override fun saveMeal(meal: Meal) {
        MEALS_SERVICE_DATA[meal.id] = meal
    }

    override fun favoriteMeal(meal: Meal){
        val favoriteMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients,meal.imageurl,meal.id)
        favoriteMeal.isFavorite = true
        MEALS_SERVICE_DATA[meal.id] = favoriteMeal
    }

    override fun favoriteMeal(mealId: String){
        //not req
    }

    override fun unFavoriteMeal(meal: Meal) {
        val favoriteMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients,meal.imageurl,meal.id)
        favoriteMeal.isFavorite = false
        MEALS_SERVICE_DATA[meal.id] = favoriteMeal
    }

    override fun unFavoriteMeal(mealId: String) {
        //not req
    }

    override fun viewMeal(meal: Meal){
        val viewMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients,meal.imageurl,meal.id)
        MEALS_SERVICE_DATA[meal.id] = viewMeal
    }

    override fun viewMeal(mealId: String) {

    }

    override fun clearFavoriteMeals(){
        MEALS_SERVICE_DATA = MEALS_SERVICE_DATA.filterValues {
            !it.isMyFavorite
        } as LinkedHashMap<String, Meal>
    }

    override fun refreshMeals() {
        // Not required because the {@link MealsRepository} handles the logic of refreshing the
        // meals from all the available data sources.
    }

    override fun deleteAllMeals() {
        MEALS_SERVICE_DATA.clear()
    }

    override fun deleteMeal(mealId: String) {
        MEALS_SERVICE_DATA.remove(mealId)
    }

    private var MEALS_SERVICE_DATA: LinkedHashMap<String, Meal> = LinkedHashMap()


    @VisibleForTesting
    fun addMeals(vararg meals: Meal) {
        for (meal in meals) {
            MEALS_SERVICE_DATA[meal.id] = meal
        }
    }
}
