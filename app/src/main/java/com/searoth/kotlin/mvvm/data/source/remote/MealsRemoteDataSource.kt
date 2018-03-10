package com.searoth.kotlin.mvvm.data.source.remote

import android.support.annotation.VisibleForTesting
import com.searoth.kotlin.mvvm.data.Meal
import com.searoth.kotlin.mvvm.data.source.MealsDataSource
import com.google.common.collect.Lists
import com.google.gson.GsonBuilder
import com.searoth.kotlin.mvvm.other.Models
import com.searoth.kotlin.mvvm.util.AppExecutors
import okhttp3.*
import java.io.IOException

/**
 * Created by cr on 2/26/2018.
 */
class MealsRemoteDataSource private constructor(
        val appExecutors: AppExecutors
) : MealsDataSource {

    private var MEALS_SERVICE_DATA = LinkedHashMap<String, Meal>(2)

    init {
//        addMeal("Snack1",
//                4.5f,
//                9.99f,
//                "Homemade bread, mesquite grilled pork, black bean, avocado, tomato, roasted onions. Served with potato salad and chips.",
//                "Call 15 minutes ahead of time. Other notes go here",
//                "2 tablespoons fresh thyme leaves, roughly chopped",
//                "http://www.gamermessenger.com/savorlyapp/food/chickenItza.jpg",
//                "0"
//                )
//        addMeal("Snack2",
//                5f,
//                5.75f,
//                "beans, lettuce, tomatoes, avocado, onions, cilantroAsada (Steak) - Carnitas (Fried Pork) - Al Pastor (Roasted Pork) - Pollo (Chicken) - Lengua (Tongue) - Chile Relleno - Vegetarian",
//                "Call 15 minutes ahead of time. Other notes go here",
//                "2 tablespoons fresh thyme leaves, roughly chopped",
//                "http://www.gamermessenger.com/savorlyapp/food/king.jpg",
//                "1"
//                )
    }

    private fun addMeal(name: String, rating: Float, price: Float, description: String, notes: String, ingredients: String, imageUrl: String, id: String){
        val newMeal = Meal(name, rating, price, description, notes, ingredients, imageUrl, id)
        MEALS_SERVICE_DATA[newMeal.id] = newMeal
    }

    /**
     * Note: [LoadMealsCallback.onDataNotAvailable] is never fired. in a real remote data source implemetnation this would be fired if the server
     * cant be contacted or thhe server returns an error
     */
    override fun getMeals(callback: MealsDataSource.LoadMealsCallback) {
        appExecutors.networkIO.execute{
            val url = "http://www.gamermessenger.com/savorlyapp/food_feed.json"

            val request = Request.Builder().url(url).build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    val body = response?.body()?.string()
                    val gson = GsonBuilder().create()
                    val meals =  gson.fromJson(body, Models.LocalFeed::class.java)

                    for(meal: Models.FoodItem in meals.meals){
                        addMeal(meal.name,
                                meal.rating,
                                meal.price.toFloat(),
                                meal.description,
                                meal.notes,
                                meal.ingredients.toString(),
                                meal.imageUrl,
                                meal.id.toString()
                        )
                    }
                    val finalMeals = Lists.newArrayList(MEALS_SERVICE_DATA.values)
                    callback.onMealsLoaded(finalMeals)
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    println("Failed to execute request")
                }
            })
        }
    }

    /**
     * Note: [GetMealCallback.onDataNotAvailable] is never fired.
     * real world scenario this gets fired if server cant be contacted or it returns error
     */
    override fun getMeal(mealId: String, callback: MealsDataSource.GetMealCallback) {
        val meal = MEALS_SERVICE_DATA[mealId]
        if (meal != null) {
            callback.onMealLoaded(meal)
        }
    }

    override fun saveMeal(meal: Meal) {
        MEALS_SERVICE_DATA[meal.id] = meal
    }

    override fun refreshMeals() {
        /**not required because {@link MealsRepository } handles the logic of refreshing*/
    }

    override fun deleteAllMeals() {
        MEALS_SERVICE_DATA.clear()
    }

    override fun deleteMeal(mealId: String) {
        MEALS_SERVICE_DATA.remove(mealId)
    }

    override fun favoriteMeal(meal: Meal) {
        val favoriteMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients, meal.imageurl, meal.id).apply {
            isFavorite = true
        }
        MEALS_SERVICE_DATA[meal.id] = favoriteMeal
    }

    override fun favoriteMeal(mealId: String) {

    }

    override fun unFavoriteMeal(meal: Meal) {
        val favoriteMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients, meal.imageurl, meal.id).apply {
            isFavorite = false
        }
        MEALS_SERVICE_DATA[meal.id] = favoriteMeal
    }

    override fun unFavoriteMeal(mealId: String) {

    }

    override fun viewMeal(meal: Meal) {
        val viewMeal = Meal(meal.name, meal.rating, meal.price, meal.description, meal.notes, meal.ingredients, meal.imageurl, meal.id).apply {
            views++
        }
        MEALS_SERVICE_DATA[meal.id] = viewMeal
    }

    override fun viewMeal(mealId: String) {

    }

    override fun clearFavoriteMeals() {
        MEALS_SERVICE_DATA = MEALS_SERVICE_DATA.filterValues{
            !it.isFavorite
        } as LinkedHashMap<String, Meal>
    }

    companion object {
        private var INSTANCE: MealsRemoteDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors): MealsRemoteDataSource{
            if(INSTANCE == null){
                synchronized(MealsRemoteDataSource::javaClass){
                    INSTANCE = MealsRemoteDataSource(appExecutors)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance(){
            INSTANCE = null
        }
    }

}