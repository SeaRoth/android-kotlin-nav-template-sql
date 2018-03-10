package com.searoth.kotlin.mvvm.data.source.local

import android.support.annotation.VisibleForTesting
import com.searoth.kotlin.mvvm.data.Meal
import com.searoth.kotlin.mvvm.data.source.MealsDataSource
import com.searoth.kotlin.mvvm.util.AppExecutors

/**
 * Created by cr on 2/26/2018.
 */
class MealsLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val mealsDao: MealsDao
) : MealsDataSource {


    /**
     * Note: [LoadMealsCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getMeals(callback: MealsDataSource.LoadMealsCallback) {
        appExecutors.diskIO.execute{
            val meals = mealsDao.getMeals()
            appExecutors.mainThread.execute{
                if(meals.isEmpty()){
                    //if table new or empty
                    callback.onDataNotAvailable()
                }else{
                    callback.onMealsLoaded(meals)
                }
            }
        }
    }

    override fun getMeal(mealId: String, callback: MealsDataSource.GetMealCallback) {
        appExecutors.diskIO.execute{
            val meal = mealsDao.getMealById(mealId)
            appExecutors.mainThread.execute{
                if(meal != null){
                    callback.onMealLoaded(meal)
                }else{
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun favoriteMeal(meal: Meal) {
        appExecutors.diskIO.execute{ mealsDao.updateFavorite(meal.id, true)}
    }

    override fun favoriteMeal(mealId: String) {
        // Not required for the local data source because the {@link MealsRepository} handles
        // converting from a {@code mealId} to a {@link meal} using its cached data.
    }

    override fun unFavoriteMeal(meal: Meal) {
        appExecutors.diskIO.execute{ mealsDao.updateFavorite(meal.id, false)}
    }

    override fun unFavoriteMeal(mealId: String) {

    }

    override fun viewMeal(meal: Meal) {
        appExecutors.diskIO.execute{ mealsDao.viewMeal(meal.id)}
    }

    override fun viewMeal(mealId: String) {
    }

    override fun clearFavoriteMeals() {
        appExecutors.diskIO.execute{ mealsDao.removeFavoriteMeals() }
    }

    override fun saveMeal(meal: Meal) {
        appExecutors.diskIO.execute{mealsDao.insertMeal(meal)}
    }

    override fun refreshMeals() {
        /** not required because {@link MealsRepository} handles the logic*/
    }

    override fun deleteAllMeals() {
        appExecutors.diskIO.execute{mealsDao.deleteMeals()}
    }

    override fun deleteMeal(mealId: String) {
        appExecutors.diskIO.execute{ mealsDao.deleteMealsById(mealId)}
    }

    companion object {
        private var INSTANCE: MealsLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, mealsDao: MealsDao): MealsLocalDataSource{
            if(INSTANCE == null){
                synchronized(MealsLocalDataSource::javaClass){
                    INSTANCE = MealsLocalDataSource(appExecutors, mealsDao)
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